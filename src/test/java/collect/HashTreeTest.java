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
package collect;

import org.junit.Ignore;
import collect.NodeImpl;
import java.util.*;
import collect.HashTree;
import crdt.PreconditionException;
import collect.Node;
import collect.Tree;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author score
 */
public class HashTreeTest<T> {

     <T> List<T> createList(T... nodes) {
        List<T> list = new ArrayList<T>();
        for (T t : nodes) {
            list.add(t);
        }
        return list;
    }

    @Test
    public void testContains() throws Exception {  
        Tree t = new HashTree();
        Node a = t.add(null, 'a'), b = t.add(a, 'b');
        assertTrue("" + t, t.contains(a));
        assertTrue("" + t, t.contains(b));        
    }
    
    
    @Test
    public void testNotContains() throws Exception {  
        Tree t = new HashTree();
        Node a = t.add(null, 'a'), b = t.add(a, 'b');
        t.remove(a);
        assertFalse("" + t, t.contains(a));
        assertFalse("" + t, t.contains(b));        
    }
     
    @Test
    public void testAddNode() {
        Tree t = new HashTree();

        Node A = t.add(t.getRoot(), 'a');//Under root
        Node B = t.add(t.getRoot(), 'b');//Under root
        Node C = t.add(t.getRoot(), 'c');//Under root

        Node<T> X = t.add(B, 'x');
        Node<T> Y = t.add(B, 'y');
        Node<T> Z = t.add(C, 'z');

        assertEquals(A.getChildrenNumber(), 0);
        assertEquals(B.getChildrenNumber(), 2);
        assertEquals(C.getChildrenNumber(), 1);
        assertEquals(A.getLevel(), 1);
        assertEquals(B.getLevel(), 1);
        assertEquals(C.getLevel(), 1);
        assertEquals(X.getLevel(), 2);
        assertEquals(Y.getLevel(), 2);
        assertEquals(Z.getLevel(), 2);
    }

    @Test
    public void testDelNode() {
        Tree t = new HashTree();
        Node root = t.getRoot(), 
                A = t.add(root, 'a'),
                B = t.add(root, 'b'),
                C = t.add(root, 'c'),
                X = t.add(B, 'x'),
                Y = t.add(B, 'y'),
                D = t.add(A, 'd'),
                Z = t.add(X, 'z');
        Tree t2 = new HashTree();
        Node root2 = t2.getRoot(), 
                A2 = t2.add(root2, 'a'),
                C2 = t2.add(root2, 'c'),
                D2 = t2.add(A2, 'd');
        
        t.remove(B);
        
        assertEquals(t2, t);
        assertFalse(t.contains(B));
        assertFalse(t.contains(X));
        assertFalse(t.contains(Y));
        assertFalse(t.contains(Z));
        assertSame(2, root.getChildrenNumber());
    }    
    
    @Test
    public void testgetChildrenIterator() {
        Tree t = new HashTree();

        Node A = t.add(t.getRoot(), 'a');//Under root
        Node B = t.add(t.getRoot(), 'b');//Under root
        Node C = t.add(t.getRoot(), 'c');//Under root

        Node<T> X = t.add(B, 'x');
        Node<T> Y = t.add(B, 'y');
        Node<T> Z = t.add(C, 'z');

        Iterator it1 = B.getChildrenIterator();
        Set s1 = new HashSet() {

            {
                add('x');
                add('y');
            }
        };
        while (it1.hasNext()) {
            assertTrue(s1.contains(((NodeImpl<T>) it1.next()).getValue()));
        }

        Iterator it2 = C.getChildrenIterator();
        assertEquals('z', (((NodeImpl<T>) it2.next()).getValue()));
    }

    @Test
    public void testIteratorDFSOK() {
        Tree<Character> t = new HashTree<Character>();

        Node root = t.getRoot();
        Node A = t.add(root, 'A');//Under root
        Node B = t.add(root, 'B');//Under root

        Node X = t.add(A, 'X');
        Node Y = t.add(A, 'Y');
        Node C = t.add(B, 'C');

        Node Z = t.add(Y, 'Z');

        List<Node> l1 = createList(root, A, X, Y, Z, B, C),
                l2 = createList(root, A, Y, Z, X, B, C),
                l3 = createList(root, B, C, A, Y, Z, X),
                l4 = createList(root, B, C, A, X, Y, Z);

        Iterator<? extends Node<Character>> it = t.getDFSIterator(null);
        List<Node> result = new ArrayList<Node>();
        while (it.hasNext()) {
            result.add(it.next());
        }

        assertTrue(l1.equals(result) || l2.equals(result) || l3.equals(result) || l4.equals(result));
    }

    @Test
    public void testIteratorBFSOK() {
        Tree<Character> t = new HashTree<Character>();
        Node root = t.getRoot();
        Node A = t.add(root, 'A');//Under root
        Node B = t.add(root, 'B');//Under root
        Node X = t.add(A, 'X');
        Node Y = t.add(A, 'Y');
        Node C = t.add(B, 'C');
        Node Z = t.add(Y, 'Z');
        List l = new ArrayList();
        Collections.addAll(l, createList(root, A, B, X, Y, C, Z),
                createList(root, B, A, X, Y, C, Z),
                createList(root, A, B, X, C, Y, Z),
                createList(root, B, A, X, C, Y, Z),
                createList(root, A, B, Y, X, C, Z),
                createList(root, B, A, Y, X, C, Z),
                createList(root, A, B, Y, C, X, Z),
                createList(root, B, A, Y, C, X, Z),
                createList(root, A, B, C, X, Y, Z),
                createList(root, B, A, C, X, Y, Z),
                createList(root, A, B, C, Y, X, Z),
                createList(root, B, A, C, Y, X, Z));
        Iterator<? extends Node<Character>> it = t.getBFSIterator(null);
        List<Node> result = new ArrayList<Node>();
        
        while (it.hasNext()) {
            result.add(it.next());
        }
        
        assertTrue(""+result, l.contains(result));
    }
    
    @Test
    public void testIteratorSubDFS() {
        Tree<Character> t = new HashTree<Character>();

        Node root = t.getRoot();
        Node A = t.add(root, 'A');//Under root
        Node B = t.add(root, 'B');//Under root
        Node X = t.add(A, 'X');
        Node Y = t.add(A, 'Y');
        Node C = t.add(B, 'C');
        Node Z = t.add(Y, 'Z');
        List<Node> l1 = createList(A, X, Y, Z),
                l2 = createList(A, Y, Z, X);
        Iterator<? extends Node<Character>> it = t.getDFSIterator(A);
        List<Node> result = new ArrayList<Node>();
        
        while (it.hasNext()) {
            result.add(it.next());
        }
        
        assertTrue(l1.equals(result) || l2.equals(result));
    }

    @Test
    public void testIteratorSubBFS() {
        Tree<Character> t = new HashTree<Character>();

        Node root = t.getRoot();
        Node A = t.add(root, 'A');//Under root
        Node B = t.add(root, 'B');//Under root
        Node X = t.add(A, 'X');
        Node Y = t.add(A, 'Y');
        Node C = t.add(B, 'C');
        Node Z = t.add(Y, 'Z');
        List l = new ArrayList();
        Collections.addAll(l, createList(A, X, Y, Z),
                createList(A, Y, X, Z));
        Iterator<? extends Node<Character>> it = t.getBFSIterator(A);
        List<Node> result = new ArrayList<Node>();
        
        while (it.hasNext()) {
            result.add(it.next());
        }
        
        assertTrue(l.contains(result));
    }

    @Ignore  // bad test
    @Test
    public void testIteratorDFS() {
        Stack<Character> s = new Stack<Character>();
        Tree<Character> t = new HashTree<Character>();

        Node B = t.add(t.getRoot(), 'b');//Under root
        Node C = t.add(t.getRoot(), 'c');//Under root
        Node D = t.add(t.getRoot(), 'd');//Under root

        Node E = t.add(C, 'e');
        Node F = t.add(C, 'f');
        Node G = t.add(D, 'g');

        Node H = t.add(F, 'h');
        Node I = t.add(F, 'i');
        Node J = t.add(G, 'j');
        /*   ABCDEFGHIJ Src A est la racine
         * A 0000000000
         * B 1000000000
         * C 1000000000
         * D 1000000000
         * E 0010000000
         * F 0010000000
         * G 0001000000
         * H 0000010000
         * I 0000010000
         * J 0000001000
         *DST */
        int matrix[][] = {{0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 1, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 1, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 1, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 1, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 1, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 1, 0, 0, 0}};


        /*HashSet childB = new HashSet<Node<T>>(){{add(X); add(Y);}};
        HashSet childY = new HashSet<Node<T>>(){{add(M); add(N);}};*/

        Iterator<? extends Node<Character>> it = t.getDFSIterator(null);
        int nbNode = 0;

        Node<Character> n;
        s.push('a'); /*On met la racine dans la pile*/
        boolean exist;
        while (it.hasNext()) {
            nbNode++;
            n = it.next();

            assertEquals(matrix[n.getValue() - 'a'][s.peek() - 'a'], 1); /* On vérifie que la liaison existe et non visité*/
            matrix[n.getValue() - 'a'][s.peek() - 'a'] = 2; /*on marque la liaison */


            s.push(n.getValue());/* On ajoute la dernière lettre*/

            /*on pop les sources sans fils ou déjà vérifié*/
            do {
                exist = false;
                for (int i = 0; i < matrix.length; i++) {/*On parcours les fils de n*/
                    if (matrix[i][s.peek() - 'a'] == 1) {/*S'il existe un fils non marqué */
                        exist = true;                 /* on dit qu'il existe*/
                        break;
                    }
                }
                if (!exist) {/*S'il n'existe pas de fils non marké on pop*/
                    s.pop();
                }
            } while (!exist && !s.empty());/*Tant qu'on pop et qu'on peut poper on recommence.*/

            /*------------------------*/


            /*  if(n.equals(B))
            {
            Iterator itChildB = n.getChildrenIterator();
            assertTrue(childB.contains(itChildB.next()));
            assertTrue(childB.contains(itChildB.next()));
            }
            if(n.equals(Y))
            {
            Iterator itChildY = n.getChildrenIterator();
            assertTrue(childY.contains(itChildY.next()));
            assertTrue(childY.contains(itChildY.next()));
            }
            if(n.equals(C))
            {
            assertTrue(it.next().equals(Z));
            assertTrue(it.next().equals(K));
            }*/

        }
        assertEquals(nbNode, 9);/*On vérrifie qu'il y a bien 9 noeuds.*/

    }

    /** Test simple disant que le parcours en largeur 
     * ne doit voir son niveau croissant
     * 
     */
    @Test
    public void testIteratorBFS() {
        Tree t = new HashTree();

        Node A = t.add(t.getRoot(), 'a');//Under root
        Node B = t.add(t.getRoot(), 'b');//Under root
        Node C = t.add(t.getRoot(), 'c');//Under root

        Node X = t.add(B, 'x');
        Node Y = t.add(B, 'y');
        Node Z = t.add(C, 'z');

        Node M = t.add(Y, 'm');
        Node N = t.add(Y, 'n');
        Node K = t.add(Z, 'k');

        Iterator<Node<T>> it = t.getBFSIterator(null);

        Node last = t.getRoot();
        int level = 0;
        Node tmp;
        int nbNode = 0;
        while (it.hasNext()) {
            nbNode++;
            tmp = it.next();
            assert (tmp.getLevel() >= level);

            if (tmp.getLevel() > level) {
                level = tmp.getLevel();
            }
        }
        assertEquals(nbNode, 10);
    }

    private boolean degree(Node current, Node last, Tree t) {
        Node n = current;
        Node l = last;
        int degCurr = 0;
        int deglast = 0;

        while (!n.getFather().equals(t.getRoot())) {
            degCurr++;
            n = n.getFather();
        }

        if (l != t.getRoot()) {
            while (!l.getFather().equals(t.getRoot())) {
                deglast++;
                l = l.getFather();
            }
        }

        if (deglast > degCurr) {
            return false;
        }

        last = current;
        return true;
    }

    @Test
    public void testEqualsEmpty() {
        assertEquals(new HashTree(), new HashTree());
    }

    @Test
    public void testEquals() {
        Tree<Character> treeA = new HashTree<Character>(), treeB = new HashTree<Character>();
        Node<Character> na = treeA.add(null, 'b'),
                nb = treeB.add(null, 'b');
        treeA.add(null, 'a');
        treeA.add(na, 'c');
        treeB.add(null, 'a');
        treeB.add(nb, 'c');

        assertEquals(treeA, treeB);
    }
}
