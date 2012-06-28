/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.tree.fctree;

import collect.OrderedNode;
import crdt.CRDT;
import crdt.CRDTMessage;
import crdt.OperationBasedOneMessage;
import crdt.PreconditionException;
import crdt.tree.fctree.Operations.Add;
import crdt.tree.fctree.Operations.Del;
import crdt.tree.orderedtree.CRDTOrderedTree;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Stephane Martin <stephane.martin@loria.fr>
 */
public class FCTree<T> extends CRDTOrderedTree<T> {

    FCNode root;
    HashMap<FCIdentifier, FCNode> map=new HashMap<FCIdentifier, FCNode>();
    IdFactory idFactory = new IdFactory();
    FCPositionFactory positionFactory= new FCPositionFactory();

    @Override
    public CRDTMessage add(List<Integer> path, int p, T element) throws PreconditionException {
        //LinkedList <Integer> npath=new LinkedList<Integer>(path);
        //npath.addLast(p);
        FCNode node = root.getNodeFromPath(path);
        FCNode gnode = node.getChild(p - 1);
        FCNode lnode = node.getChild(p);
        Add add = new Add(element, positionFactory.createBetween(gnode, lnode), node.getId(), this.idFactory.createId());
        add.applyOnNode(node, this);
        return new OperationBasedOneMessage(add);
    }

    @Override
    public CRDTMessage remove(List<Integer> path) throws PreconditionException {
        FCNode node = root.getNodeFromPath(path);
        Del del = new Del(node.getId());
        del.applyOnNode(node, this);
        return new OperationBasedOneMessage(del);
    }

    public FCNode<T> getNodeById(FCIdentifier id) {
        return map.get(id);
    }

    @Override
    public void applyOneRemote(CRDTMessage op) {
        applyOneRemote((FCOperation) ((OperationBasedOneMessage) op).getOperation());
    }

    public void applyOneRemote(FCOperation<T> op) {
       /* System.out.println("op : "+op);
        System.out.println("before"+root);*/
        op.apply(this);
        //System.out.println("after"+root);
    }

    @Override
    public OrderedNode<T> lookup() {
        return root;
    }

    @Override
    public CRDT<OrderedNode<T>> create() {
        return new FCTree();
    }

    public HashMap<FCIdentifier, FCNode> getMap() {
        return map;
    }

    @Override
    public void setReplicaNumber(int replica) {
        idFactory.setReplicaNumber(replica);
    }

    public FCTree() {
        FCIdentifier idroot=new FCIdentifier(-1, 0);
        root=new FCNode(root, null, null, idroot);
        map.put(idroot, root);
    }

    @Override
    public String toString() {
        return "FCTree"+idFactory.getReplica() +"{"  + root + '}';
    }
    
    
}
