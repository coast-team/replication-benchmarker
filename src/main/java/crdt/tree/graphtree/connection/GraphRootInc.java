/**
 * Replication Benchmarker
 * https://github.com/score-team/replication-benchmarker/
 * Copyright (C) 2013 LORIA / Inria / SCORE Team
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
