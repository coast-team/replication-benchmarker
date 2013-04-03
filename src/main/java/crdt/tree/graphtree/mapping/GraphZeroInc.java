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
package crdt.tree.graphtree.mapping;

import collect.Node;
import collect.Tree;
import crdt.tree.graphtree.Edge;
import crdt.tree.graphtree.GraphMappPolicy;
import java.util.HashMap;

/**
 *
 * @author Stephane Martin
 */
public class GraphZeroInc<T> extends GraphMappPolicyInc<T> {
    Tree tree ;
    HashMap <Node,Integer> nbFather;
    
    @Override
    void addEdge(Edge<T> e) {
            
        
    }

    @Override
    void delEdge(Edge<T> e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    

    @Override
    public Tree<T> lookup() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public GraphMappPolicy<T> create() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    void moveEdge(Edge<T> OldFather, Edge<T> moved) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

   
    
}
