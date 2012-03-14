/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package collect;

import org.junit.Test;
import static org.junit.Assert.*;


/**
 *
 * @author score
 */
public class NodeImplTest<T> {

    @Test
    public void testNode() {
        NodeImpl root = new NodeImpl();

        NodeImpl A = new NodeImpl(root, 'a');
        NodeImpl B = new NodeImpl(root, 'b');

        assertEquals(A.getFather(), root);
        assertEquals(B.getFather(), root);

        assertEquals(A.getValue(), 'a');
        assertEquals(B.getValue(), 'b');
    }

    @Test
    public void testAddChildren() {
        NodeImpl root = new NodeImpl();

        NodeImpl B = new NodeImpl(root, 'b');

        NodeImpl C = new NodeImpl(B, 'c');
        NodeImpl D = new NodeImpl(B, 'd');

        assertEquals(2, B.getChildrenNumber());
    }

    @Test
    public void testNotEquals() {
        NodeImpl<Character> rA = new NodeImpl(),
                rB = new NodeImpl(),
                aA = new NodeImpl(rA, 'a'),
                aB = new NodeImpl(rB, 'a'),
                bA = new NodeImpl(rA, 'a'),
                bB = new NodeImpl(aB, 'a');

        assertFalse(rA.sameTree(rB));
    }

    @Test
    public void testEquals() {
        NodeImpl<Character> rA = new NodeImpl(), rB = new NodeImpl(),
                aA = new NodeImpl(rA, 'a'), aB = new NodeImpl(rB, 'a'),
                bA = new NodeImpl(rA, 'b'), bB = new NodeImpl(rB, 'b'),
                cA = new NodeImpl(aA, 'c'), cB = new NodeImpl(aB, 'c');
        assertEquals(rA, rB);
        assertEquals(aA, aB);
        assertEquals(bA, bB);
        assertEquals(cA, cB);
        assertTrue(rA.sameTree(rB));
        assertTrue(aA.sameTree(aB));
        assertTrue(bA.sameTree(bB));
        assertTrue(cA.sameTree(cB));

        NodeImpl<Character> dA = new NodeImpl(cA, 'd'),
                dB = new NodeImpl(cB, 'e');
        assertEquals(rA, rB);
        assertEquals(aA, aB);
        assertEquals(bA, bB);
        assertEquals(cA, cB);
        assertFalse(rA.sameTree(rB));
        assertFalse(aA.sameTree(aB));
        assertFalse(cA.sameTree(cB));
        assertFalse(dA.sameTree(dB));
        assertFalse(dA.equals(dB));
    }
}
