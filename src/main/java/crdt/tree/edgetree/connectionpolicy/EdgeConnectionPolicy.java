/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.tree.edgetree.connectionpolicy;

import crdt.Factory;
import crdt.tree.edgetree.Edge;
import crdt.tree.edgetree.mappingpolicy.EdgeMappPolicy;
import java.util.Observer;
import java.util.Set;

/**
 *
 * @author Stephane Martin
 */
public abstract class EdgeConnectionPolicy<T> implements Factory <EdgeConnectionPolicy<T>>,Observer {
    protected EdgeMappPolicy<T> emp;
    
    abstract public Set <Edge<T>>getEdges();
    
    public void SetMappingPolicy(EdgeMappPolicy<T> emp){
        this.emp=emp;
    }
}
