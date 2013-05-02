/**
 * Replication Benchmarker
 * https://github.com/score-team/replication-benchmarker/ Copyright (C) 2013
 * LORIA / Inria / SCORE Team
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package crdt.tree.fctree;

import collect.OrderedNode;
import crdt.CRDTMessage;
import crdt.OperationBasedMessagesBag;
import crdt.OperationBasedOneMessage;
import crdt.PreconditionException;
import crdt.tree.fctree.Operations.Add;
import crdt.tree.fctree.Operations.Del;
import crdt.tree.fctree.Operations.Nop;
import crdt.tree.fctree.policy.PostAction;
import crdt.tree.orderedtree.CRDTOrderedTree;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @param <T>
 * @author Stephane Martin <stephane.martin@loria.fr>
 */
public abstract class FCTree<T> extends CRDTOrderedTree<T> {
    PostAction postAction = null;
    boolean removeEntireSubtree =false; //remove entire subtree on local delete
    FCNode root;
    HashMap<FCIdentifier, FCNode> map = new HashMap<FCIdentifier, FCNode>();
    //HashMap<FCIdentifier, FCNodeGf> idToCycle = new HashMap<FCIdentifier, FCNodeGf>();
    FCIdFactory idFactory = new FCIdFactory();
    FCPositionFactory positionFactory = new FCPositionFactory();
    

    /**
     * Add a node at end of path, in position p and return a message to others
     *
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
        FCIdentifier id = this.idFactory.createId();
        Add add = new Add(element, positionFactory.createBetweenNode(gnode, lnode, id), node.getId(), id);
        add.apply(node, this);
        return new OperationBasedOneMessage(add);
    }

    /**
     * del a node at end of path and return a message to others
     *
     * @param path
     * @return
     * @throws PreconditionException
     */
    @Override
    public CRDTMessage remove(List<Integer> path) throws PreconditionException {
        FCNode node = root.getNodeFromPath(path);
        if (node.getId().getReplicaNumber() < 0) {
            return new OperationBasedOneMessage(new Nop(this.idFactory.createId()));
        }
        if (removeEntireSubtree) {
            return delNode(node);
        } else {
            Del del = new Del(this.idFactory.createId(), node.getId());
            del.apply(node, this);
            return new OperationBasedOneMessage(del);
        }

    }

    private OperationBasedMessagesBag delNode(FCNode node) {
        OperationBasedMessagesBag ret = new OperationBasedMessagesBag();
        List<FCNode<T>> child=new LinkedList(node.getElements());
        for (FCNode<T> n : child) {
            ret.addMessage(delNode(n));
        }
        Del del = new Del(this.idFactory.createId(), node.getId());
        del.apply(node, this);
        ret.addMessage(new OperationBasedOneMessage(del));
        return ret;
    }
    @Override
    public CRDTMessage rename(List<Integer> path, T newValue){
        return new OperationBasedOneMessage(new Nop(this.idFactory.createId()));
    }
    
 @Override
    public CRDTMessage move(List<Integer> from, List<Integer> to, int p) {
        return new OperationBasedOneMessage(new Nop(this.idFactory.createId()));
    }
    

    /**
     * search node by identifier
     *
     * @param id
     * @return node identified by ID
     */
    public FCNode<T> getNodeById(FCIdentifier id) {
        return map.get(id);
    }

    /**
     * Apply one remote message from others
     *
     * @param op
     */
    @Override
    public void applyOneRemote(CRDTMessage op) {
        applyOneRemote((FCOperation) ((OperationBasedOneMessage) op).getOperation());
    }

    /**
     * Apply one remote message from others
     *
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
     *
     * @return
     */
    @Override
    public OrderedNode<T> lookup() {
        return root;
    }

    

    /**
     * Return the map of identifier -> fcnodes
     *
     * @return
     */
    public HashMap<FCIdentifier, FCNode> getMap() {
        return map;
    }

    /**
     * Set replicat number
     *
     * @param replica
     */
    @Override
    public void setReplicaNumber(int replica) {
        super.setReplicaNumber(replica);
        idFactory.setReplicaNumber(replica);
    }

    /**
     * Constructor for an tree with a root identified by site : -1 nbop : 0
     * 
     */
    public FCTree() {
        this(false);
    }
    /**
     * 
     * @param action Action trigger after add/del/move operation
     * @param removeEntireTree Remove entire subtree on local remove
     */
    public FCTree(PostAction action,boolean removeEntireTree) {
        this.removeEntireSubtree = removeEntireTree;
        this.postAction = action;
        if (postAction != null) {
            postAction.setTree(this);
        }
    }

    /**
     * 
     * @param removeEntireTree Remove entire subtree on local remove
     */
    public FCTree(boolean removeEntireTree) {
        //FCIdentifier idroot = new FCIdentifier(-1, 0);
        //root = new FCNodeGf(root, null, null, idroot);
        //map.put(idroot, root);
        this.removeEntireSubtree = removeEntireTree;
    }
    
    public FCTree(PostAction action) {
        this(action,false);
    }

    @Override
    public String toString() {
        return "FCTree" + idFactory.getReplica() + "{" + root + '}';
    }

    public PostAction getPostAction() {
        return postAction;
    }

    public FCNode getRoot() {
        return root;
    }

    public FCPositionFactory getPositionFactory() {
        return positionFactory;
    }

    public FCIdFactory getIdFactory() {
        return idFactory;
    }

    public boolean isRemoveEntireSubtree() {
        return removeEntireSubtree;
    }

    public void setRemoveEntireSubtree(boolean removeEntireSubtree) {
        this.removeEntireSubtree = removeEntireSubtree;
    }
    
    
}
