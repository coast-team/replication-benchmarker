/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.tree.graphtree;

import collect.HashMapSet;
import crdt.Factory;
import java.util.Observable;
import java.util.Observer;

/**
 *
 * @author moi
 */
public abstract class GraphConnectionPolicy<T> extends Observable implements Factory<GraphConnectionPolicy<T>>, Observer{
    protected GraphMappPolicy<T> gmp;

    /**
     * @param gmp the gmp to set
     */

    public void setGraphMappingPolicy(GraphMappPolicy<T> gmp) {
        this.gmp = gmp;
    }
    abstract public HashMapSet <T,Edge<T>> lookup ();
    @Override
    abstract public  void update(Observable o, Object op);
    
}
