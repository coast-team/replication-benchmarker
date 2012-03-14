/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.tree.edgetree.mappingpolicy;

import collect.Tree;
import crdt.Factory;
import crdt.tree.edgetree.Edge;
import crdt.tree.edgetree.connectionpolicy.EdgeConnectionPolicy;

/**
 *
 * @author moi
 */
public abstract class EdgeMappPolicy<T> implements Factory<EdgeMappPolicy<T>>/*
 * ,Observer
 */ {

    public abstract Tree<T> getTree();
    //public abstract Node<T> getRoot();

    public abstract void add(Edge<T> e, EdgeConnectionPolicy<T> ecp);

    public abstract void del(Edge<T> e, EdgeConnectionPolicy<T> ecp);

    /*public abstract void modif(Edge<T> e, EdgeConnectionPolicy<T> ecp);*/

    public abstract void moved(T OdFather, Edge<T> e, EdgeConnectionPolicy<T> ecp);
}
