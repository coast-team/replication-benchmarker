/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.tree.graphtree.connection;

import collect.HashMapSet;
import crdt.tree.graphtree.Edge;
import crdt.tree.graphtree.mapping.MappingUpdateOperation;

/**
 *
 * @author Stephane Martin
 */
public class GraphRootInc<T> extends GraphConnectionPolicyInc<T> {

    
    HashMapSet<T, Edge<T>> tEdges;
    HashMapSet<T, Edge<T>> orphans;
    
    @Override
    void addEdge(Edge<T> edge) {
        if (!tEdges.containsKey(edge.getFather())){
            T father=edge.getFather();
            //edge.setFather(null);
            Edge<T> nedge = new Edge(null,edge.getSon());
            orphans.put(father, edge);
            gmp.update(this, new MappingUpdateOperation<Edge<T>>(MappingUpdateOperation.Type.add,edge,nedge));
            
        }
        lookup.put(edge.getFather(), edge);
        tEdges.put(edge.getSon(), edge);
        /* On rattache les fils */
        for (Edge <T>e : orphans.removeAll(edge.getSon())){
            lookup.remove(null, e);
            //e.setFather(edge.getSon());
            Edge<T> nedge = new Edge(edge.getSon(),e.getSon());
            gmp.update(this, new MappingUpdateOperation<Edge<T>>(MappingUpdateOperation.Type.move,edge,nedge));
            
            lookup.put(e.getFather(),e);
            
        }
        
    }

    @Override
    void delEdge(Edge<T> edge) {
        /* on mets ces fils à la racine*/
        for (Edge <T> e:lookup.getAll(edge.getSon())){
            T father=e.getFather();
            lookup.remove(father, e);
            
            //e.setFather(null);
            Edge<T> nedge = new Edge(null,e.getSon());
            orphans.put(father, e);
            lookup.put(null, e);
            gmp.update(this, new MappingUpdateOperation<Edge<T>>(MappingUpdateOperation.Type.move,e,nedge));
        }
        lookup.remove(edge.getFather(), edge);
        tEdges.remove(edge.getSon(), edge);
        gmp.update(this, new MappingUpdateOperation<Edge<T>>(MappingUpdateOperation.Type.del,edge));
    }


    @Override
    GraphConnectionPolicyInc createSpe() {
       GraphRootInc ret= new GraphRootInc();
        ret.orphans=new HashMapSet();
        ret.tEdges=new HashMapSet();
        return ret;
    }

    
}
