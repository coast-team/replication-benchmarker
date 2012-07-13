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
