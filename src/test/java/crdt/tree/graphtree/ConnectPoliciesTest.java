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

import crdt.tree.graphtree.connection.GraphCompact;
import collect.HashMapSet;
import crdt.tree.graphtree.connection.GraphReappear;
import crdt.tree.graphtree.connection.GraphRoot;
import java.util.Map;
import java.util.HashMap;
import crdt.tree.graphtree.connection.GraphSkip;
import java.util.HashSet;
import java.util.Iterator;
import crdt.set.SetOperation;
import crdt.tree.PseudoSet;
import collect.Node;
import java.util.Set;
import collect.Tree;
import collect.HashTree;
import org.junit.Test;
import static org.junit.Assert.*;

import static collect.Utils.*;

/**
 *
 * @author score
 */
public class ConnectPoliciesTest<T> {
    
    char a='a', b='b',c='c',d='d',
            e='e',m='m', x='x',k='k', y='y', z='z';
    Set<Character> node = toSet(a, b, c, d, e, x , k, y, z);  
    Edge edgeRA = new Edge(null, a);Edge edgeRB = new Edge(null, b);
    Edge edgeRC = new Edge(null, c);Edge edgeRD = new Edge(null, d);
    Edge edgeAZ = new Edge(a, z);
    Edge edgeBX = new Edge(b, x);Edge edgeBY = new Edge(b, y);
    Edge edgeCZ = new Edge(c, z);
    Edge edgeDE = new Edge(d, e);Edge edgeED = new Edge(e, d);
    Set<Edge> edge = toSet(edgeRA, edgeRB,edgeRC, edgeRD, edgeAZ, edgeBX, edgeBY, edgeCZ, edgeDE, edgeED);

    @Test
    public void testSkipEmpty() {
           GraphConnectionPolicyNoInc graphPolicy = new GraphSkip(); 
           
           graphPolicy.update(new PseudoSet(), new SetOperation(SetOperation.OpType.add, a));
           graphPolicy.connect();
           assertEquals(0, graphPolicy.lookup().keySet().size());
           assertEquals(0, graphPolicy.lookup().values().size());
    }
    
    @Test
    public void testSkip() {
        GraphConnectionPolicyNoInc graphPolicy = new GraphSkip(); 
                
        graphPolicy.update(new PseudoSet(node), new SetOperation(SetOperation.OpType.add, a));
        graphPolicy.update(new PseudoSet(edge), new SetOperation(SetOperation.OpType.add, edgeRA));
        
        node.remove(b);
        graphPolicy.update(new PseudoSet(node), new SetOperation(SetOperation.OpType.del, b));
        edge.remove(edgeRB);
        graphPolicy.update(new PseudoSet(edge), new SetOperation(SetOperation.OpType.del, edgeRB));

        HashMapSet<Character, Edge<T>> test = new HashMapSet();
        test.put(null, edgeRA);
        test.put(null, edgeRC);
        test.put(null, edgeRD);
        test.put(c, edgeCZ); test.put(a,edgeAZ);
        test.put(e,edgeED);
        test.put(d, edgeDE);
        
        HashMapSet<Character, Edge<T>> look = graphPolicy.lookup();
        Iterator itr = test.keySet().iterator();
        while(itr.hasNext())
        {
            Character ch = (Character) itr.next();
            for(Edge<T> edg : look.getAll(ch))
            {
                assertTrue(look.containsValue(ch, edg));
            }
        }
        itr = look.keySet().iterator();
        while(itr.hasNext())
        {
            Character ch = (Character) itr.next();
            for(Edge<T> edg : test.getAll(ch))
            {
                assertTrue(test.containsValue(ch, edg));
            }
        }
    }
    
    @Test
    public void testRootEmpty() {
           GraphConnectionPolicyNoInc graphPolicy = new GraphRoot(); 
           graphPolicy.update(new PseudoSet(), new SetOperation(SetOperation.OpType.add, a));
           graphPolicy.connect();
           assertEquals(0, graphPolicy.lookup().keySet().size());
           assertEquals(0, graphPolicy.lookup().values().size());
    }
    
    @Test
    public void testRoot() {
        GraphConnectionPolicy graphPolicy = new GraphRoot(); 
                
        graphPolicy.update(new PseudoSet(node), new SetOperation(SetOperation.OpType.add, a));
        graphPolicy.update(new PseudoSet(edge), new SetOperation(SetOperation.OpType.add, edgeRA));
        
        node.remove(b);
        graphPolicy.update(new PseudoSet(node), new SetOperation(SetOperation.OpType.del, b));
        edge.remove(edgeRB);
        graphPolicy.update(new PseudoSet(edge), new SetOperation(SetOperation.OpType.del, edgeRB));
        
        
        HashMapSet<Character, Edge<T>> test = new HashMapSet();
        test.put(null, edgeRA);
        test.put(null, edgeRC);
        test.put(c, edgeCZ);test.put(a,edgeAZ);
        test.put(null, edgeRD);test.put(e,edgeED);
        test.put(d, edgeDE);
        test.put(null, new Edge(null, x));
        test.put(null, new Edge(null, y));
        
        HashMapSet<Character, Edge<T>> look = graphPolicy.lookup();
                
        Iterator itr = test.keySet().iterator();
        while(itr.hasNext())
        {
            Character ch = (Character) itr.next();
            for(Edge<T> edg : look.getAll(ch))
            {
                assertTrue(look.containsValue(ch, edg));
            }
        }
        itr = look.keySet().iterator();
        while(itr.hasNext())
        {
            Character ch = (Character) itr.next();
            for(Edge<T> edg : test.getAll(ch))
            {
                assertTrue(test.containsValue(ch, edg));
            }
        }
    }
    
     @Test
    public void testReappearEmpty() {
           GraphConnectionPolicyNoInc graphPolicy = new GraphReappear(); 
           graphPolicy.update(new PseudoSet(), new SetOperation(SetOperation.OpType.add, a));
           graphPolicy.connect();
           assertEquals(0, graphPolicy.lookup().keySet().size());
           assertEquals(0, graphPolicy.lookup().values().size());
    }

         @Test
    public void testReappear() {
        
        GraphConnectionPolicyNoInc graphPolicy = new GraphReappear(); 
        graphPolicy.update(new PseudoSet(node), new SetOperation(SetOperation.OpType.add, a));
        graphPolicy.update(new PseudoSet(edge), new SetOperation(SetOperation.OpType.add, edgeRA));
       
        node.add(k);
        graphPolicy.update(new PseudoSet(node), new SetOperation(SetOperation.OpType.add, k));
        final Edge edgeXK = new Edge(x, k);        
        edge.add(edgeXK);
        graphPolicy.update(new PseudoSet(edge), new SetOperation(SetOperation.OpType.add, edgeXK));
        
        node.remove(x);
        graphPolicy.update(new PseudoSet(node), new SetOperation(SetOperation.OpType.del, x));
        edge.remove(edgeBX);
        graphPolicy.update(new PseudoSet(edge), new SetOperation(SetOperation.OpType.del, edgeBX));
        node.remove(b);
        graphPolicy.update(new PseudoSet(node), new SetOperation(SetOperation.OpType.del, b));
        edge.remove(edgeRB);
        graphPolicy.update(new PseudoSet(edge), new SetOperation(SetOperation.OpType.del, edgeRB));
        node.remove(y);
        graphPolicy.update(new PseudoSet(node), new SetOperation(SetOperation.OpType.del, y));
        edge.remove(edgeBY);
        graphPolicy.update(new PseudoSet(edge), new SetOperation(SetOperation.OpType.del, edgeBY));

        
       HashMapSet<Character, Edge<T>> test = new HashMapSet();
        test.put(null, edgeRA);
        test.put(null, edgeRB);
        test.put(b, edgeBX);
        test.put(null, edgeRC);
        test.put(c, edgeCZ);test.put(a,edgeAZ);
        test.put(null, edgeRD);test.put(e,edgeED);
        test.put(d, edgeDE);
        test.put(x, edgeXK);
        
        HashMapSet<Character, Edge<T>> look = graphPolicy.lookup();
        Iterator itr = test.keySet().iterator();
        while(itr.hasNext())
        {
            Character ch = (Character) itr.next();
            for(Edge<T> edg : look.getAll(ch))
            {
                assertTrue(look.containsValue(ch, edg));
            }
        }
        itr = look.keySet().iterator();
        while(itr.hasNext())
        {
            Character ch = (Character) itr.next();
            for(Edge<T> edg : test.getAll(ch))
            {
                assertTrue(test.containsValue(ch, edg));
            }
        }
    }
    
    //Test Reapperar : 2 father for the same node
     @Test
    public void testReappearSameFather() {
       GraphConnectionPolicyNoInc graphPolicy = new GraphReappear();

        graphPolicy.update(new PseudoSet(node), new SetOperation(SetOperation.OpType.add, a));
        graphPolicy.update(new PseudoSet(edge), new SetOperation(SetOperation.OpType.add, edgeRA));
        
        //add k under x
        node.add(k);
        graphPolicy.update(new PseudoSet(node), new SetOperation(SetOperation.OpType.add, k));
        final Edge edgeXK = new Edge(x, k);
        edge.add(edgeXK);
        graphPolicy.update(new PseudoSet(edge), new SetOperation(SetOperation.OpType.add, edgeXK));

        //add k under A <--- Shortest
        node.add(k);
        graphPolicy.update(new PseudoSet(node), new SetOperation(SetOperation.OpType.add, k));
        Edge edgeAK = new Edge(a, k);
        edge.add(edgeAK);
        graphPolicy.update(new PseudoSet(edge), new SetOperation(SetOperation.OpType.add, edgeAK));
        //add m under k
        node.add(m);
        graphPolicy.update(new PseudoSet(node), new SetOperation(SetOperation.OpType.add, m));
        Edge edgeKM = new Edge(k, m);
        edge.add(edgeKM);
        graphPolicy.update(new PseudoSet(edge), new SetOperation(SetOperation.OpType.add, edgeKM));
        
        //make (k,m) orphan
        node.remove(b);
        graphPolicy.update(new PseudoSet(node), new SetOperation(SetOperation.OpType.del, b));
        edge.remove(edgeRB);
        graphPolicy.update(new PseudoSet(edge), new SetOperation(SetOperation.OpType.del, edgeRB));
        node.remove(a);
        graphPolicy.update(new PseudoSet(node), new SetOperation(SetOperation.OpType.del, a));
        edge.remove(edgeRA);
        graphPolicy.update(new PseudoSet(edge), new SetOperation(SetOperation.OpType.del, edgeRA));
        node.remove(k);
        graphPolicy.update(new PseudoSet(node), new SetOperation(SetOperation.OpType.del, k));
        edge.remove(edgeAK);
        graphPolicy.update(new PseudoSet(edge), new SetOperation(SetOperation.OpType.del, edgeAK));
        node.remove(x);
        graphPolicy.update(new PseudoSet(node), new SetOperation(SetOperation.OpType.del, x));
        edge.remove(edgeBX);
        graphPolicy.update(new PseudoSet(edge), new SetOperation(SetOperation.OpType.del, edgeBX));
        edge.remove(edgeXK);
        graphPolicy.update(new PseudoSet(edge), new SetOperation(SetOperation.OpType.del, edgeXK));
        
        //remove y,z and here edges
        node.remove(y);
        graphPolicy.update(new PseudoSet(node), new SetOperation(SetOperation.OpType.del, y));
        edge.remove(edgeBY);
        graphPolicy.update(new PseudoSet(edge), new SetOperation(SetOperation.OpType.del, edgeBY));
        node.remove(z);
        graphPolicy.update(new PseudoSet(node), new SetOperation(SetOperation.OpType.del, z));
        edge.remove(edgeAZ);
        edge.remove(edgeCZ);
        graphPolicy.update(new PseudoSet(edge), new SetOperation(SetOperation.OpType.del, edgeAZ));
        graphPolicy.update(new PseudoSet(edge), new SetOperation(SetOperation.OpType.del, edgeCZ));
        
        HashMapSet<Character, Edge<T>> test = new HashMapSet();
        test.put(null, edgeRA);
        test.put(null, edgeRB);
        test.put(b, edgeBX);
        test.put(null, edgeRC);
        test.put(null, edgeRD);test.put(e,edgeED);
        test.put(d, edgeDE);
        test.put(x, edgeXK);
        test.put(k, edgeKM);
        test.put(a, edgeAK);
        
        HashMapSet<Character, Edge<T>> look = graphPolicy.lookup();
        
        Iterator itr = test.keySet().iterator();
        while(itr.hasNext())
        {
            Character ch = (Character) itr.next();
            for(Edge<T> edg : look.getAll(ch))
            {
                assertTrue(look.containsValue(ch, edg));
            }
        }
        
        itr = look.keySet().iterator();
        while(itr.hasNext())
        {
            Character ch = (Character) itr.next();
            for(Edge<T> edg : test.getAll(ch))
            {
                assertTrue(test.containsValue(ch, edg));
            }
        }
    }
     
     @Test
    public void testCompactEmpty() {
           GraphConnectionPolicyNoInc graphPolicy = new GraphCompact(); 
           graphPolicy.update(new PseudoSet(), new SetOperation(SetOperation.OpType.add, a));
           graphPolicy.connect();
           assertEquals(0, graphPolicy.lookup().keySet().size());
           assertEquals(0, graphPolicy.lookup().values().size());
    }

         @Test
    public void testCompact() {
        GraphConnectionPolicyNoInc graphPolicy = new GraphCompact(); 
        graphPolicy.update(new PseudoSet(node), new SetOperation(SetOperation.OpType.add, a));
        graphPolicy.update(new PseudoSet(edge), new SetOperation(SetOperation.OpType.add, edgeRA));
       
       //add k under x
        node.add(k);
        graphPolicy.update(new PseudoSet(node), new SetOperation(SetOperation.OpType.add, k));
        final Edge edgeXK = new Edge(x, k);
        edge.add(edgeXK);
        graphPolicy.update(new PseudoSet(edge), new SetOperation(SetOperation.OpType.add, edgeXK));

        //add k under A <--- Shortest
        node.add(k);
        graphPolicy.update(new PseudoSet(node), new SetOperation(SetOperation.OpType.add, k));
        Edge edgeAK = new Edge(a, k);
        edge.add(edgeAK);
        graphPolicy.update(new PseudoSet(edge), new SetOperation(SetOperation.OpType.add, edgeAK));
        //add m under k
        node.add(m);
        graphPolicy.update(new PseudoSet(node), new SetOperation(SetOperation.OpType.add, m));
        Edge edgeKM = new Edge(k, m);
        edge.add(edgeKM);
        graphPolicy.update(new PseudoSet(edge), new SetOperation(SetOperation.OpType.add, edgeKM));
        
        
        //make (k,m) orphan
        node.remove(a);
        graphPolicy.update(new PseudoSet(node), new SetOperation(SetOperation.OpType.del, a));
        edge.remove(edgeRA);
        graphPolicy.update(new PseudoSet(edge), new SetOperation(SetOperation.OpType.del, edgeRA));
        node.remove(k);
        graphPolicy.update(new PseudoSet(node), new SetOperation(SetOperation.OpType.del, k));
        edge.remove(edgeAK);
        graphPolicy.update(new PseudoSet(edge), new SetOperation(SetOperation.OpType.del, edgeAK));
        node.remove(x);
        graphPolicy.update(new PseudoSet(node), new SetOperation(SetOperation.OpType.del, x));
        edge.remove(edgeBX);
        graphPolicy.update(new PseudoSet(edge), new SetOperation(SetOperation.OpType.del, edgeBX));
        edge.remove(edgeXK);
        graphPolicy.update(new PseudoSet(edge), new SetOperation(SetOperation.OpType.del, edgeXK));
        
       HashMapSet<Character, Edge<T>> test = new HashMapSet();
        test.put(null, new Edge(null,k));
        test.put(null, edgeRB);test.put(b, edgeBY);
        test.put(b, new Edge(b,k));
        test.put(null, edgeRC);
        test.put(c, edgeCZ);test.put(a,edgeAZ);
        test.put(null, edgeRD);test.put(e,edgeED);
        test.put(d, edgeDE);
        test.put(k, new Edge(k, m));
        
        HashMapSet<Character, Edge<T>> look = graphPolicy.lookup();
        
        Iterator itr = test.keySet().iterator();
        while(itr.hasNext())
        {
            Character ch = (Character) itr.next();
            for(Edge<T> edg : look.getAll(ch))
            {
                assertTrue(look.containsValue(ch, edg));
            }
        }
        itr = look.keySet().iterator();
        while(itr.hasNext())
        {
            Character ch = (Character) itr.next();
            for(Edge<T> edg : test.getAll(ch))
            {
                assertTrue(test.containsValue(ch, edg));
            }
        }
    }
    
    void display(HashMapSet<Character, Edge<T>> look) {
        Iterator itr = look.keySet().iterator();
        while(itr.hasNext())
        {
            Character ch = (Character) itr.next();
            System.out.print(ch);
            for(Edge edg : look.getAll(ch))
            {
                System.out.println("("+edg.getFather()+"-->"+edg.getSon()+")");
            }
        }

    }
    
}
