/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.tree.edgetree.connectionpolicy;

import collect.HashMapSet;
import crdt.set.CRDTSet;
import crdt.set.SetOperation;
import crdt.tree.edgetree.Edge;
import java.util.Observable;
import java.util.Set;

/**
 *
 * @author Stephane Martin
 */
public class SkipInc<T> extends EdgeConnectionPolicy<T> {
  HashMapSet<T, Edge<T>> orphans;/*
     * father est la clef
     */

    HashMapSet<T, Edge<T>> tEdges;
    HashMapSet<T, Edge<T>> fEdges;
    //HashTree tree;
    
  

    public SkipInc() {
        
    }

    
    @Override
    public EdgeConnectionPolicy<T> create() {
        RootInc<T> ret = new RootInc<T>();
        /*
         * ret.orphans = new HashMapSet<T, Node<T>>(); ret.tree = new HashTree();
         */
        ret.tEdges=new HashMapSet<T, Edge<T>> ();
        //ret.fEdges=new HashMapSet<T, Edge<T>> ();
        //ret.orphans=new HashMapSet<T, Edge<T>> ();
        return ret;
    }

    @Override
    public void update(Observable o, Object o1) {
        /*TODO : Peut-être Supprimer les non connexe à la racine et les mettre dans orphans */
        if (o instanceof CRDTSet
                && o1 instanceof SetOperation) {
            SetOperation o2 = (SetOperation) o1;
            Edge<T> edge = ((Edge<T>) o2.getContent())/*.clone()*/;
            if (o2.getType() == SetOperation.OpType.add) {
                
                tEdges.put(edge.getSon(), edge);
                this.emp.add(edge, this);
            } else {
                tEdges.remove(edge.getSon(),edge);
                this.emp.del(edge, this);
            } 
        }

    }

   
    @Override
    public Set<Edge<T>> getEdges() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
