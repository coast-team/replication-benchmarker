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
import java.util.List;

/**
 *
 * @param <T> 
 * @author Stephane Martin <stephane.martin@loria.fr>
 */
public class FCTree<T> extends CRDTOrderedTree<T> {

    FCNode root;
    HashMap<FCIdentifier, FCNode> map=new HashMap<FCIdentifier, FCNode>();
    IdFactory idFactory = new IdFactory();
    FCPositionFactory positionFactory= new FCPositionFactory();

    /**
     * Add a node at end of path, in position p and return a message to others
     * @param path
     * @param p
     * @param element
     * @return
     * @throws PreconditionException
     */
    @Override
    public CRDTMessage add(List<Integer> path, int p, T element) throws PreconditionException {
        FCNode node = root.getNodeFromPath(path);
        FCNode gnode = node.getChild(p - 1);
        FCNode lnode = node.getChild(p);
        Add add = new Add(element, positionFactory.createBetween(gnode, lnode), node.getId(), this.idFactory.createId());
        add.applyOnNode(node, this);
        return new OperationBasedOneMessage(add);
    }

    /**
     * del a node at end of path and return a message to others
     * @param path
     * @return
     * @throws PreconditionException
     */
    @Override
    public CRDTMessage remove(List<Integer> path) throws PreconditionException {
        FCNode node = root.getNodeFromPath(path);
        Del del = new Del(node.getId());
        del.applyOnNode(node, this);
        return new OperationBasedOneMessage(del);
    }

    /**
     * search node by identifier
     * @param id
     * @return node identified by ID
     */
    public FCNode<T> getNodeById(FCIdentifier id) {
        return map.get(id);
    }

    /**
     * Apply one remote message from others
     * @param op
     */
    @Override
    public void applyOneRemote(CRDTMessage op) {
        applyOneRemote((FCOperation) ((OperationBasedOneMessage) op).getOperation());
    }

    /**
     *Apply one remote message from others
     * @param op
     */
    public void applyOneRemote(FCOperation<T> op) {
       /* System.out.println("op : "+op);
        System.out.println("before"+root);*/
        op.apply(this);
        //System.out.println("after"+root);
    }

    /**
     * return the lookup (the root)
     * @return
     */
    @Override
    public OrderedNode<T> lookup() {
        return root;
    }

    /**
     * factory of fctree
     * @return
     */
    @Override
    public CRDT<OrderedNode<T>> create() {
        return new FCTree();
    }

    /**
     * Return the map of identifier -> fcnodes
     * @return
     */
    public HashMap<FCIdentifier, FCNode> getMap() {
        return map;
    }

    /**
     * Set replicat number 
     * @param replica
     */
    @Override
    public void setReplicaNumber(int replica) {
        idFactory.setReplicaNumber(replica);
    }

    /**
     * Constructor for an tree with a root identified by site : -1 nbop : 0
     */
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
