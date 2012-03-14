/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.tree.graphtree;

import collect.Tree;
import crdt.Factory;
import java.util.Observer;
/**
 *
 * @author score
 */
public abstract class GraphMappPolicy<T> implements Factory<GraphMappPolicy<T>> , Observer {
    private GraphConnectionPolicy<T> gcp;
    
    public abstract Tree<T> lookup();

    /**
     * @param gcp the gcp to set
     */
    public void setGcp(GraphConnectionPolicy<T> gcp) {
        this.gcp = gcp;
    }

    public GraphConnectionPolicy<T> getGcp() {
        return gcp;
    }
    
}