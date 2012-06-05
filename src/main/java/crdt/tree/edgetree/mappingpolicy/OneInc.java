/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.tree.edgetree.mappingpolicy;

import collect.HashMapSet;
import collect.Node;
import collect.Tree;
import crdt.tree.edgetree.Edge;
import crdt.tree.edgetree.connectionpolicy.EdgeConnectionPolicy;

/**
 *
 * @author Stephane Martin
 */
public class OneInc<T> extends EdgeMappPolicy<T> {
    HashMapSet<T,Node<T>> nodes;

    /*public static Choice higher=new Higher();
    public static Choice newer=new Newer();*/

    @Override
    public void add(Edge<T> e, EdgeConnectionPolicy<T> ecp) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void del(Edge<T> e, EdgeConnectionPolicy<T> ecp) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

   

    @Override
    public void moved(T OdFather, Edge<T> e, EdgeConnectionPolicy<T> ecp) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public static interface Choice<T>{
        boolean keepFirst(T first,T second);
    }
    
    public static class Higher<T> implements Choice<T>{

        @Override
        public boolean keepFirst(T first, T second) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
        
    }
    public static class Newer<T> implements Choice<T>{

        @Override
        public boolean keepFirst(T first, T second) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
        
    }
    public static class Shortest<T> implements Choice<T>{

        @Override
        public boolean keepFirst(T first, T second) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
        
    }
    
    
    @Override
    public Tree<T> getTree() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public EdgeMappPolicy<T> create() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    
    
}
