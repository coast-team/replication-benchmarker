/**
 * Replication Benchmarker
 * https://github.com/score-team/replication-benchmarker/
 * Copyright (C) 2012 LORIA / Inria / SCORE Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package crdt.tree.edgetree;

import collect.Node;
import collect.Tree;
import collect.UnorderedNode;
import crdt.CRDTMessage;
import crdt.Factory;
import crdt.PreconditionException;
import crdt.set.CRDTSet;
import crdt.tree.CRDTUnorderedTree;
import crdt.tree.edgetree.connectionpolicy.EdgeConnectionPolicy;
import crdt.tree.edgetree.mappingpolicy.EdgeMappPolicy;
import java.util.HashMap;
import java.util.Iterator;

 
/**
 *
 * @author Stephane Martin
 */
public class EdgeTree<T> extends CRDTUnorderedTree<T>{
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
    public CRDTUnorderedTree create() {
        
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
    public CRDTMessage add(UnorderedNode<T> father, T element) throws PreconditionException {
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
    public CRDTMessage remove(UnorderedNode<T> subtree) throws PreconditionException {
        
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
    public UnorderedNode<T> getRoot() {
        return (UnorderedNode<T>) lookup().getRoot();
    }

    @Override
    public void applyOneRemote(CRDTMessage msg) {
        this.edgesSet.applyRemote(msg);
    }

    @Override
    public Tree<T> lookup() {
        return emp.getTree();
    }
    
    
    
   
    
    
}
