/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.tree.wordtree;

import collect.HashTree;
import collect.Node;
import collect.Tree;
import collect.UnorderedNode;
import crdt.CRDTMessage;
import crdt.Factory;
import crdt.PreconditionException;
import crdt.set.CRDTSet;
import crdt.tree.CRDTTree;
import java.io.Serializable;
import java.util.*;

/**
 *
 * @author urso
 * 
 * 
 */
public class WordTree<T> extends CRDTTree<T>{
    CRDTSet words;
    WordPolicy<T> wcp;
    
    public WordTree(Factory<CRDTSet<List<T>>> setFactory, Factory<WordPolicy<T>> wcp) {
        this.wcp = wcp.create();
        this.words =  setFactory.create();
        this.words.addObserver(this.wcp);
    }
    
    @Override
    public CRDTMessage add(UnorderedNode<T> father, T element) throws PreconditionException {
        if (!wcp.lookup().contains(father)) 
            throw new PreconditionException("Adding node " + element + " with father not in the tree");
        if (father.getChild(element) != null) 
            throw new PreconditionException("Adding node " + element + " already present under father");
        
        CRDTMessage msg = null;
        for (List<T> wf : wcp.addMapping(father)) {
            List<T> w =  new Word(wf, element);
            CRDTMessage add = words.add(w);
            msg = msg == null ? add : msg.concat(add);
        }
        return msg;
    }

    @Override
    public CRDTMessage remove(UnorderedNode<T> subtree) throws PreconditionException {
        if (wcp.lookup().getRoot() == subtree) 
            throw new PreconditionException("Removing root");
        if (!wcp.lookup().contains(subtree)) 
            throw new PreconditionException("Removing node " + subtree + " not in the tree");

        CRDTMessage msg = null;

        for (List<T> w : wcp.toBeRemoved(subtree)) {
            CRDTMessage del = words.remove(w);
            msg = msg == null ? del : msg.concat(del);
        }
        return msg;
    }

    @Override
    public void applyOneRemote(CRDTMessage op) {
        words.applyRemote(op);       
    }

    @Override
    public Tree<T> lookup() {
        return wcp.lookup();
    }

    @Override
    public UnorderedNode<T> getRoot() {
        return (UnorderedNode<T>) wcp.lookup().getRoot();
    }

    @Override
    public WordTree<T>  create() {
        return new WordTree<T>(words, wcp);
    }

    @Override
    public void setReplicaNumber(int replicaNumber) {
        super.setReplicaNumber(replicaNumber);
        words.setReplicaNumber(replicaNumber);
    }

    @Override
    public String toString() {
        return "WordTree<" + words.getClass() + ',' + wcp.getClass() + ">{" + this.getReplicaNumber() + '}';
    }

    @Override
    public synchronized void addObserver(Observer obsrvr) {
        super.addObserver(obsrvr);
        ((HashTree<T>) wcp.lookup()).addObserver(obsrvr);
    }
}
