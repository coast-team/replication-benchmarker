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
package crdt.tree.edgetree.connectionpolicy;

import collect.HashMapSet;
import crdt.set.CRDTSet;
import crdt.set.SetOperation;
import crdt.tree.edgetree.Edge;
import java.util.Observable;
import java.util.Set;

/**
 *
 * @author Stephane Martin
 */
public class SkipInc<T> extends EdgeConnectionPolicy<T> {
  HashMapSet<T, Edge<T>> orphans;/*
     * father est la clef
     */

    HashMapSet<T, Edge<T>> tEdges;
    HashMapSet<T, Edge<T>> fEdges;
    //HashTree tree;
    
  

    public SkipInc() {
        
    }

    
    @Override
    public EdgeConnectionPolicy<T> create() {
        RootInc<T> ret = new RootInc<T>();
        /*
         * ret.orphans = new HashMapSet<T, Node<T>>(); ret.tree = new HashTree();
         */
        ret.tEdges=new HashMapSet<T, Edge<T>> ();
        //ret.fEdges=new HashMapSet<T, Edge<T>> ();
        //ret.orphans=new HashMapSet<T, Edge<T>> ();
        return ret;
    }

    @Override
    public void update(Observable o, Object o1) {
        /*TODO : Peut-être Supprimer les non connexe à la racine et les mettre dans orphans */
        if (o instanceof CRDTSet
                && o1 instanceof SetOperation) {
            SetOperation o2 = (SetOperation) o1;
            Edge<T> edge = ((Edge<T>) o2.getContent())/*.clone()*/;
            if (o2.getType() == SetOperation.OpType.add) {
                
                tEdges.put(edge.getSon(), edge);
                this.emp.add(edge, this);
            } else {
                tEdges.remove(edge.getSon(),edge);
                this.emp.del(edge, this);
            } 
        }

    }

   
    @Override
    public Set<Edge<T>> getEdges() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
