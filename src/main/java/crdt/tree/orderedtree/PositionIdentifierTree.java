/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.tree.orderedtree;

import collect.Node;
import collect.OrderedNode;
import collect.UnorderedNode;
import crdt.CRDTMessage;
import crdt.Factory;
import crdt.PreconditionException;
import crdt.tree.CRDTTree;
import crdt.tree.TreeOperation;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 *
 * @author urso
 */
public class PositionIdentifierTree<T> extends CRDTOrderedTree<T> implements Observer {
    CRDTTree tree;
    OrderedNode<T> root;

    public PositionIdentifierTree(OrderedNode<T> pic, Factory<CRDTTree<T>> tree) {
        this.tree = tree.create();
        this.root = pic;
        this.tree.addObserver(this); 
    }


    @Override
    public CRDTMessage add(List<Integer> path, int p, T element) throws PreconditionException {
        OrderedNode<T> f = root;
        UnorderedNode<Positioned<T>> uf = tree.getRoot();
        for (int i : path) {
            uf = uf.getChild(f.getPositioned(i));
            f = f.getChild(i);
        }
        PositionIdentifier pi = f.getNewPosition(p, element);
        CRDTMessage msg = tree.add(uf, new Positioned(pi, element));
        return msg;
    }

    @Override
    public CRDTMessage remove(List<Integer> path) throws PreconditionException {
        OrderedNode<T> f = root;
        UnorderedNode<Positioned<T>> uf = tree.getRoot();
        for (int i : path) {
            uf = uf.getChild(f.getPositioned(i));
            f = f.getChild(i);
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
        return new PositionIdentifierTree<T>(root.createNode(null), tree);
    }

    @Override
    public void update(Observable o, Object arg) {
        TreeOperation<Positioned<T>> op = (TreeOperation<Positioned<T>>) arg;
        PositionIdentifier pi = op.getContent().getPi();
        T t = op.getContent().getElem();
        if (op.getType() == TreeOperation.OpType.add) {
            OrderedNode<T> father = link(op.getNode());
            father.add(pi, t);
        } else if (op.getType() == TreeOperation.OpType.del) {
            OrderedNode<T> father = link(op.getNode());
            father.remove(pi, t);
        } else { // move seems to bug
            OrderedNode<T> father = link(op.getNode()), 
                    dest = link(op.getDest());
            father.remove(pi, t);
            dest.add(pi, t);
        }      
    }

    private OrderedNode link(Node<Positioned<T>> node) {
        List<Positioned<T>> path = node.getPath();
        OrderedNode<T> father = root;
        for (Positioned<T> p : path) {
            father = father.getChild(p);
        }
        return father;
    }

    @Override
    public void setReplicaNumber(int replicaNumber) {
        super.setReplicaNumber(replicaNumber);
        tree.setReplicaNumber(replicaNumber);
        root.setReplicaNumber(replicaNumber);
    }
}
