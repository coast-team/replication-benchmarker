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
package crdt.tree.graphtree;

import crdt.tree.graphtree.mapping.GraphSeveral;
import crdt.tree.graphtree.mapping.GraphMappPolicyNoInc;
import collect.HashMapSet;
import java.util.Iterator;
import crdt.tree.graphtree.mapping.GraphZero;
import collect.Node;
import collect.Tree;
import collect.HashTree;
import java.util.Observable;
import org.junit.Test;
import static org.junit.Assert.*;
/**
 *
 * @author score
 */
public class MappPoliciesTest<T> {
    
    char a='a', b='b',c='c',d='d',
            e='e', x='x', y='y', z='z';
    
    GraphConnectionPolicy generateGraphPol() {
        final HashMapSet<Character, Edge<Character>> setTree = new HashMapSet();

        setTree.put(null, new Edge(null, a));
        setTree.put(a, new Edge(a, z));
        setTree.put(null, new Edge(null, b));
        setTree.put(b, new Edge(b, x));
        setTree.put(b, new Edge(b, y));
        setTree.put(null, new Edge(null, c));
        setTree.put(null, new Edge(null, z));

        setTree.put(null, new Edge(null, d));
        setTree.put(d, new Edge(d, e));
        setTree.put(d, new Edge(e, d));
         
        
        
        GraphConnectionPolicy<Character> gcp=new GraphConnectionPolicy<Character>(){
            @Override
            public HashMapSet<Character, Edge<Character>> lookup() {
                return setTree;
            }

            @Override
            public void update(Observable o, Object op) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public GraphConnectionPolicy<Character> create() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

        };
        
        
        return gcp;
    }

    
    @Test
    public void testGraphZero() {
        GraphMappPolicyNoInc graphPolicy = new GraphZero();

        Tree t = new HashTree();
        Node nodeA = t.add(t.getRoot(), a);

        Node nodeB = t.add(t.getRoot(), b);
        Node nodeX = t.add(nodeB, x);
        Node nodeY = t.add(nodeB, y);

        Node nodeC = t.add(t.getRoot(), c);
        
        graphPolicy.setGcp(this.generateGraphPol());
        graphPolicy.update(null, null);
        Tree result = graphPolicy.lookup();
        assertEquals(t, result);       
       
     }
     
          @Test
    public void testGraphSeveral() {
              
        GraphMappPolicyNoInc graphPolicy = new GraphSeveral();

        Tree t = new HashTree();
        Node nodeA = t.add(t.getRoot(), a);
        Node nodeZ = t.add(t.getRoot(), z);
        Node nodeC = t.add(t.getRoot(), c);
        Node nodeD = t.add(t.getRoot(), d);
        Node nodeB = t.add(t.getRoot(), b);
        Node nodeX = t.add(nodeB, x);
        Node nodeY = t.add(nodeB, y);
        
        Node nodeZA = t.add(nodeA, z);
        Node nodeE = t.add(nodeD, e);
        
        graphPolicy.setGcp(this.generateGraphPol());
        graphPolicy.update(null, null);
        Tree result = graphPolicy.lookup();
        assertEquals(t, result);       
         
     }
    
    
     @Test
    public void testGraphHigher() {
         
     }
     
     @Test
    public void testGraphNewer() {
     }
     

    @Test
    public void testGraphShortest() {
    }
    
    void display(Tree<T> look) {
        Iterator itr = look.getBFSIterator(look.getRoot());
        while(itr.hasNext())
        {
            Node<T> node = (Node<T>) itr.next();
            System.out.println(node);
        }

    }
}
