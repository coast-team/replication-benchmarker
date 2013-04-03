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
