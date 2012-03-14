/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.tree.graphtree.mapping;

import crdt.tree.graphtree.Edge;
import crdt.tree.graphtree.GraphMappPolicy;
import java.util.Observable;

/**
 *
 * @author moi
 */
public abstract class GraphMappPolicyInc<T> extends GraphMappPolicy<T> {

    @Override
    public void update(Observable o, Object op) {

        if (op instanceof MappingUpdateOperation) {
            MappingUpdateOperation<Edge<T>> opm = (MappingUpdateOperation) op;
            switch (opm.getType()) {
                case add:
                    //if (opm.getObject() instanceof Edge) {
                        addEdge((Edge) opm.getObject());
                    /*} else {
                        addNode((T) opm.getObject());
                    }*/
                    break;
                case del:
                    //if (opm.getObject() instanceof Edge) {
                        delEdge((Edge) opm.getObject());
                    /*} else {
                        delNode((T) opm.getObject());
                    }*/
                    break;
                case move:
                    //if (opm.getObject() instanceof Edge) {
                        moveEdge(opm.getOld(),(Edge) opm.getObject());
                    /*} else {
                        throw new UnsupportedOperationException("On ne déplace pas de noeuds !");
                    }*/
            }
        }
    }

    abstract void addEdge(Edge<T> e);

    abstract void delEdge(Edge<T> e);

    /*abstract void addNode(T e);

    abstract void delNode(T e);*/

    abstract void moveEdge(Edge<T> OldFather,Edge<T> moved);
}
