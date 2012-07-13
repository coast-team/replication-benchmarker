/**
 * Replication Benchmarker
 * https://github.com/score-team/replication-benchmarker/
 * Copyright (C) 2012 LORIA / Inria / SCORE Team
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
