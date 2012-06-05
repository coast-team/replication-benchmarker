/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.tree.graphtree.connection;

import collect.HashMapSet;
import crdt.tree.graphtree.Edge;
import crdt.tree.graphtree.GraphConnectionPolicyNoInc;
import crdt.tree.graphtree.mapping.MappingUpdateOperation;
import java.util.HashSet;

/**
 *
 * @author Stephane Martin
 */
public class GraphSkipInc<T> extends  GraphConnectionPolicyInc<T> {
    //HashSet <T> Nodes;
    //HashMapSet <T,Edge<T>> tEdges;
    //HashMapSet <T,Edge<T>> orphans;

    @Override
    void addEdge(Edge<T> edge) {
        lookup.put(edge.getFather(), edge);
        gmp.update(this, new MappingUpdateOperation<Edge<T>>(MappingUpdateOperation.Type.add,edge));
        for(Edge <T> e : lookup.getAll(edge.getSon())){
            gmp.update(this, new MappingUpdateOperation<Edge<T>>(MappingUpdateOperation.Type.move,e));
        }
    }

    @Override
    void delEdge(Edge<T> edge) {
        lookup.remove(edge.getFather(), edge);
        gmp.update(this, new MappingUpdateOperation<Edge<T>>(MappingUpdateOperation.Type.del,edge));
    }

    @Override
    GraphConnectionPolicyInc createSpe() {
            return new GraphSkipInc<T>();
    }
   

  

   
    
}
