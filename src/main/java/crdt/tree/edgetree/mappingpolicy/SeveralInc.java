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
import collect.HashTree;
import collect.Node;
import collect.Tree;
import crdt.tree.edgetree.Edge;
import crdt.tree.edgetree.connectionpolicy.EdgeConnectionPolicy;
import java.util.Set;

/**
 *
 * @author Stephane Martin
 */
public class SeveralInc<T> extends EdgeMappPolicy<T> {
    HashTree tree;
    HashMapSet<T,Node<T>> nodes; /*t est fils*/
    HashMapSet<T,Node<T>> fathers; /*t est le père*/
    //HashMap <Edge<T>,Node<T>> edges;
    
    @Override
    public Tree<T> getTree() {
        return tree;
    }

    @Override
    public EdgeMappPolicy<T> create() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    
    @Override
    public void add(Edge<T> e, EdgeConnectionPolicy<T> ecp) {
        Set<Node<T>> tfathers=nodes.getAll(e.getFather());
        Set<Node<T>> sons=nodes.getAll(e.getSon());
        
        for(Node <T> f:tfathers){
            Node<T> n = tree.add(f, e.getSon());
            nodes.put(e.getSon(),n);
            
        }
        //n, n)
    }

    @Override
    public void del(Edge<T> e, EdgeConnectionPolicy<T> ecp) {
        for(Node<T> n : nodes.getAll(e.getSon())){
            if (n.getFather()!=null && n.getFather().getValue()==e.getFather()){
                tree.remove(n);
            }
        }
    }


    @Override
    public void moved(T OdFather, Edge<T> e, EdgeConnectionPolicy<T> ecp) {
       /* boolean first=true;      
        Node<T> nodeForMove=null;
        for(Node<T> n : nodes.getAll(e.getSon())){
            if (nodeForMove==null){
               nodeForMove=n;
            }else{
                tree.remove(n);
            }
            
        }
        first=true;
        for(Node<T> n : nodes.getAll(e.getFather())){
            if (n.getFather()!=null && n.getFather().getValue()==OdFather){
                if (first)/*TODO : finish *
                    //tree.move(n,);
             ;               
            }
        }*/
    }

  

 
    
}
