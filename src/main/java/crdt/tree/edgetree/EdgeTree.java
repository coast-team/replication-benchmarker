/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.tree.edgetree;

import collect.Node;
import collect.Tree;
import crdt.CRDTMessage;
import crdt.Factory;
import crdt.PreconditionException;
import crdt.set.CRDTSet;
import crdt.tree.CRDTTree;
import crdt.tree.edgetree.connectionpolicy.EdgeConnectionPolicy;
import crdt.tree.edgetree.mappingpolicy.EdgeMappPolicy;
import java.util.HashMap;
import java.util.Iterator;

 
/**
 *
 * @author moi
 */
public class EdgeTree<T> extends CRDTTree<T>{
    CRDTSet<Edge<T>> edgesSet;
    HashMap<T,Edge<T>> annuaire;
    //Node<T> root;
    EdgeMappPolicy<T> emp;
    EdgeConnectionPolicy<T> ecp;
    Tree CacheLoockup=null;
    //ObserveurIncrementalPolicy<Edge<T>> oip;
    
    /*--- Factory ---*/
    //<Edge<T>> setFactory;
    Factory<EdgeMappPolicy<T>> empF;
    Factory<EdgeConnectionPolicy<T>> ecpF;
    Factory<CRDTSet<Edge<T>>> setFactory;
    /*--- end Factory ---*/
    
    EdgeTree(Factory<CRDTSet<Edge<T>>> setFactory, Factory<EdgeMappPolicy<T>> emp,Factory<EdgeConnectionPolicy<T>> ecp){
        this.setFactory=setFactory;
        this.empF=emp;
        this.ecpF=ecp;
    }
   

    @Override
    public CRDTTree create() {
        
        EdgeTree<T> ret=new EdgeTree(setFactory,emp,ecp);
        ret.edgesSet=setFactory.create();
        annuaire=new HashMap<T,Edge<T>>();
        ret.emp=empF.create();
        ret.ecp=ecpF.create();
       /* ret.oip= new ObserveurIncrementalPolicy();
        if (ret.emp instanceof IncrementalPolicy)
            ret.oip.addIncrementalPolicy((IncrementalPolicy)ret.emp);
        if (ret.ecp instanceof IncrementalPolicy)
            ret.oip.addIncrementalPolicy((IncrementalPolicy)ret.ecp);
        */
        return ret;
    }
    

    @Override
    public CRDTMessage add(Node<T> father, T element) throws PreconditionException {
        if (!lookup().contains(father)) 
            throw new PreconditionException("Adding node with father not in the tree");
        Edge e = new Edge(father.getValue(),element);
        if (this.edgesSet.contains(e)){
             throw new PreconditionException("Adding already existing node ");
        }
        
        
        CRDTMessage ret=this.edgesSet.add(e);
        //oip.notifyAdd(e);
        return ret;
    }

    @Override
    public CRDTMessage remove(Node<T> subtree) throws PreconditionException {
        
        Iterator<? extends Node<T>> subtreeIt = lookup().getBFSIterator(subtree);
        CRDTMessage msg = null;
        
        while (subtreeIt.hasNext()) {
            Node<T> n = subtreeIt.next();
            Edge e=annuaire.get(n);
            CRDTMessage del = this.edgesSet.remove(e);
            //oip.notifyDel(e);
            msg = msg == null ? del : msg.concat(del);
        }
        
        return msg;
    }

    @Override
    public Node<T> getRoot() {
        return lookup().getRoot();
    }

    @Override
    public void applyRemote(CRDTMessage msg) {
        this.edgesSet.applyRemote(msg);
    }

    @Override
    public Tree<T> lookup() {
        return emp.getTree();
    }
    
    
    
   
    
    
}
