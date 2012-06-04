/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.tree.graphtree;

import collect.Node;
import collect.NodeImpl;
import collect.Tree;
import collect.UnorderedNode;
import crdt.CRDTMessage;
import crdt.Factory;
import crdt.set.CRDTSet;
import crdt.PreconditionException;
import crdt.tree.CRDTTree;
import java.util.*;

/**
 *
 * @author score
 */
public class Gtree<T> extends CRDTTree<T> {
    
    private CRDTSet<T> node;
    private CRDTSet<Edge<T>> edge;
    private GraphConnectionPolicyNoInc graphConnectPol;
    private GraphMappPolicy graphMappPol;
    private Factory<CRDTSet> factory;
    Factory<GraphConnectionPolicyNoInc>gcpFact;
    Factory<GraphMappPolicy> gmpFact;
    
    public Gtree(Factory<CRDTSet> factory, Factory<GraphConnectionPolicyNoInc>gcpFact, Factory<GraphMappPolicy> gmpFact)
    {
        this.gcpFact=gcpFact;
        this.gmpFact=gmpFact;
        this.factory=factory;
        this.graphConnectPol = gcpFact.create();
        this.graphMappPol = gmpFact.create();
        this.graphMappPol.setGcp(graphConnectPol);
        this.graphConnectPol.setGraphMappingPolicy(graphMappPol);
        this.node = factory.create();
        this.edge = factory.create();
//        this.node.addObserver(gcp);
//        this.node.addObserver(gmp);
//        
//        this.edge.addObserver(gcp);
//        this.edge.addObserver(gmp);
    }

    @Override
    public CRDTMessage add(UnorderedNode<T> fath, T t) throws PreconditionException {
        if (!this.lookup().contains(fath)) 
            throw new PreconditionException("Adding node with father not in the tree");        
        
        Edge<T> newEdge = new Edge<T>(fath.getValue(), t);      
        return new GTreeMessage(node.add((t)), edge.add(newEdge));
    }

    @Override
    public CRDTMessage remove(UnorderedNode<T> subtree) throws PreconditionException {
        if (this.lookup().getRoot() == subtree) {
            throw new PreconditionException("Removing root");
        }
        if (!this.lookup().contains(subtree)) {
            throw new PreconditionException("Removing node not in the tree");
        }

        Iterator<? extends Node<T>> it = this.lookup().getBFSIterator(subtree);
        CRDTMessage msgNode = null;
        CRDTMessage msgEdge = null;
        
        while(it.hasNext())
        {
            NodeImpl<T> nodeToDel = ( NodeImpl<T>) it.next();
            
             CRDTMessage Nodedel = node.remove((T)nodeToDel);
             CRDTMessage EdgeDel = edge.remove(new Edge((NodeImpl) nodeToDel.getFather(), nodeToDel));
            
             msgNode = msgNode == null ? Nodedel : msgNode.concat(Nodedel);
             msgEdge = msgEdge == null ? EdgeDel : msgEdge.concat(EdgeDel);             
        }        
        return new GTreeMessage(msgNode, msgEdge);
    }
    
    @Override
    public void applyRemote(CRDTMessage op) {
        this.node.applyRemote(((GTreeMessage)op).getNode());
        this.edge.applyRemote(((GTreeMessage)op).getEdge());        
    }

    @Override
    public Tree lookup() {
        return graphMappPol.lookup();
    }
    
    @Override
    public UnorderedNode<T> getRoot() {
        return (UnorderedNode<T>) this.lookup().getRoot();
    }
        
    public CRDTSet<T> getNode()
    {
        return node;
    }
    
    public CRDTSet<Edge<T>> getEdge()
    {
        return edge;
    }
    
    @Override
    public CRDTTree<T> create() {
        return new Gtree<T>(factory, graphConnectPol, graphMappPol);
    }
}