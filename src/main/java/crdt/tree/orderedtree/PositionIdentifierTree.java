/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.tree.orderedtree;

import collect.Node;
import collect.OrderedNode;
import collect.UnorderedNode;
import crdt.CRDT;
import crdt.CRDTMessage;
import crdt.Factory;
import crdt.PreconditionException;
import crdt.tree.CRDTTree;
import crdt.tree.TreeOperation;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 *
 * @author urso
 */
public class PositionIdentifierTree<T> extends CRDTOrderedTree<T> implements Observer {
    PositionIdentificator pic; 
    CRDTTree tree;
    OrderedNodeImpl<T> root;

    public PositionIdentifierTree(PositionIdentificator pic, Factory<CRDTTree<T>> tree) {
        this.pic = pic;
        this.tree = tree.create();
        this.root = new OrderedNodeImpl<T>(null, null, null);
        this.tree.addObserver(this); 
    }


    @Override
    public CRDTMessage add(List<Integer> path, int p, T element) throws PreconditionException {
        OrderedNodeImpl<T> f = root;
        UnorderedNode<Positioned<T>> uf = tree.getRoot();
        for (int i : path) {
            f = f.getChild(i);
            uf = uf.getChild(f.getPositioned());
        }
        PositionIdentifier pi = pic.generate(f, p==0 ? null : f.getChild(p-1).getPosition(), 
                p==f.getChildrenNumber() ? null : f.getChild(p).getPosition());
        CRDTMessage msg = tree.add(uf, new Positioned(pi, element));
        return msg;
    }

    @Override
    public CRDTMessage remove(List<Integer> path) throws PreconditionException {
        OrderedNodeImpl<T> f = root;
        UnorderedNode<Positioned<T>> uf = tree.getRoot();
        for (int i : path) {
            f = f.getChild(i);
            uf = uf.getChild(f.getPositioned());
        }
        CRDTMessage msg = tree.remove(uf);
        return msg;
    }

    @Override
    public void applyRemote(CRDTMessage msg) {
        tree.applyRemote(msg);
    }

    @Override
    public OrderedNode<T> lookup() {
        return root;
    }

    @Override
    public PositionIdentifierTree<T> create() {
        return new PositionIdentifierTree<T>(pic, tree);
    }

    @Override
    public void update(Observable o, Object arg) {
        TreeOperation<Positioned<T>> op = (TreeOperation<Positioned<T>>) arg;
        if (op.getType() == TreeOperation.OpType.add) {
            OrderedNodeImpl<T> father = link(op.getNode());
            father.add(pic.getInteger(father, op.getContent().getPi()), op.getContent());
        } else if (op.getType() == TreeOperation.OpType.del) {
            OrderedNodeImpl<T> father = link(op.getNode());
            father.remove(op.getContent());
        } else { // move
            OrderedNodeImpl<T> father = link(op.getNode()), 
                    dest = link(op.getDest());
            OrderedNodeImpl<T> node = father.remove(op.getContent());
            dest.place(pic.getInteger(dest, op.getContent().getPi()), node);
        }      
    }

    private OrderedNodeImpl link(Node<Positioned<T>> node) {
        List<Positioned<T>> path = node.getPath();
        OrderedNodeImpl<T> father = root;
        for (Positioned<T> p : path) {
            father = father.getChild(p);
        }
        return father;
    }
}
