/**
 *   This file is part of ReplicationBenchmark.
 *
 *   ReplicationBenchmark is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   ReplicationBenchmark is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with ReplicationBenchmark.  If not, see <http://www.gnu.org/licenses/>.
 *
 **/

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jbenchmarker.logoot;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author mehdi
 */
public class LogootIdentifierTest {
    
    Component c42 = new Component(42, 4, 50);
    Component c70 = new Component(70, 2, 100);
    Component c42b = new Component(42, 4, 60);
    Component c6 = new Component(6, 4, 110);

    @Test
    public void testIsLessThan2e2() {
        System.out.println("Test LogootIdentifier");
        LogootIdentifier P = new LogootIdentifier(3);
        LogootIdentifier Q = new LogootIdentifier(3);

        P.addComponent(c42);
        P.addComponent(c70);
        Q.addComponent(c42b);
        Q.addComponent(c6);
        
        assertTrue(P.compareTo(Q)<0);
        assertTrue(Q.compareTo(P)>0);
        assertFalse(Q.equals(P));
        assertFalse(P.equals(Q));
    }
    
    @Test
    public void testIsLessThan12() {
        LogootIdentifier P = new LogootIdentifier(3);
        LogootIdentifier Q = new LogootIdentifier(3);

        P.addComponent(c42);
        Q.addComponent(c70);
        Q.addComponent(c6);
        
        assertTrue(P.compareTo(Q)<0);
        assertTrue(Q.compareTo(P)>0);
        assertFalse(Q.equals(P));
        assertFalse(P.equals(Q));
    }
    
    @Test
    public void testIsLessThan21() {
        LogootIdentifier P = new LogootIdentifier(3);
        LogootIdentifier Q = new LogootIdentifier(3);

        P.addComponent(c6);
        P.addComponent(c70);
        Q.addComponent(c42b);
        
        assertTrue(P.compareTo(Q)<0);
        assertTrue(Q.compareTo(P)>0);
        assertFalse(Q.equals(P));
        assertFalse(P.equals(Q));
    }    
    
    @Test
    public void testIsLessThan1e2() {
        LogootIdentifier P = new LogootIdentifier(3);
        LogootIdentifier Q = new LogootIdentifier(3);

        P.addComponent(c42);
        Q.addComponent(c42);
        Q.addComponent(c42b);
        
        assertTrue(P.compareTo(Q)<0);
        assertTrue(Q.compareTo(P)>0);
        assertFalse(Q.equals(P));
        assertFalse(P.equals(Q));
    }
    
    @Test
    public void testIsLessThan1e1() {
        LogootIdentifier P = new LogootIdentifier(3);
        LogootIdentifier Q = new LogootIdentifier(3);

        P.addComponent(c42);
        Q.addComponent(c42b);
        
        assertTrue(P.compareTo(Q)<0);
        assertTrue(Q.compareTo(P)>0);
        assertFalse(Q.equals(P));
        assertFalse(P.equals(Q));
    }
    
    @Test
    public void testIsLessThan11() {
        LogootIdentifier P = new LogootIdentifier(3);
        LogootIdentifier Q = new LogootIdentifier(3);

        P.addComponent(c6);
        Q.addComponent(c70);
        
        assertTrue(P.compareTo(Q)<0);
        assertTrue(Q.compareTo(P)>0);
        assertFalse(Q.equals(P));
        assertFalse(P.equals(Q));
    }
    
    @Test
    public void testEquals() {
        LogootIdentifier P = new LogootIdentifier(3);
        LogootIdentifier Q = new LogootIdentifier(3);

        P.addComponent(new Component(6, 4, 110));
        Q.addComponent(new Component(6, 4, 110));
        
        assertSame(0, P.compareTo(Q));
        assertSame(0, Q.compareTo(P));
        assertTrue(Q.equals(P));
        assertTrue(P.equals(Q));
    }
    
    
    @Test
    public void testIsLessThan3e2() {
        LogootIdentifier P = new LogootIdentifier(3);
        LogootIdentifier Q = new LogootIdentifier(3);

        P.addComponent(new Component(6, 4, 110));P.addComponent(new Component(8, 4, 110));
        Q.addComponent(new Component(6, 4, 110));Q.addComponent(new Component(8, 4, 110));
        Q.addComponent(new Component(20, 2, 50)); 
        
        assertTrue(P.compareTo(Q)<0);
        assertTrue(Q.compareTo(P)>0);
        assertFalse(Q.equals(P));
        assertFalse(P.equals(Q));
    }

    @Test
    public void TestplusSimple() {
        LogootIdentifier P = new LogootIdentifier(3);
        LogootIdentifier Q = new LogootIdentifier(3);

        P.addComponent(new Component(6, 4, 110));
        P.addComponent(new Component(8, 4, 110));
        Q.addComponent(new Component(6, 4, 110));
        Q.addComponent(new Component(9, 4, 110));
        
        LogootIdentifier R = P.plus(2, 20, Q, 100, 2, 50);

        assertEquals(3, R.length());
        assertTrue(P.compareTo(R) < 0);
        assertTrue(R.compareTo(Q) < 0);
    }

    @Test
    public void TestplusSameDigit() {
        LogootIdentifier P = new LogootIdentifier(3);
        LogootIdentifier Q = new LogootIdentifier(3);

        P.addComponent(new Component(6, 4, 110));
        P.addComponent(new Component(9, 3, 10));
        Q.addComponent(new Component(6, 4, 110));
        Q.addComponent(new Component(9, 4, 112));

        LogootIdentifier R = P.plus(2, 42, Q, 100, 2, 50);
        
        assertEquals(3, R.length());
        assertTrue(P.compareTo(R) < 0);
        assertTrue(R.compareTo(Q) < 0);
    }

    @Test
    public void TestPlusShift() {
        LogootIdentifier P = new LogootIdentifier(3);
        LogootIdentifier Q = new LogootIdentifier(3);

        P.addComponent(new Component(61, 4, 110));
        P.addComponent(new Component(95, 3, 10));
        Q.addComponent(new Component(61, 5, 110));
        Q.addComponent(new Component(42, 4, 112));

        LogootIdentifier R = P.plus(1, 42, Q, 100, 2, 50);

        assertEquals(2, R.length());
        assertEquals(Q.getComponentAt(0), R.getComponentAt(0));
        assertTrue(P.compareTo(R) < 0);
        assertTrue(R.compareTo(Q) < 0);
    }

    @Test
    public void TestPlusShiftB() {
        LogootIdentifier P = new LogootIdentifier(3);
        LogootIdentifier Q = new LogootIdentifier(3);

        P.addComponent(new Component(12, 2, 11));
        P.addComponent(new Component(61, 4, 110));
        P.addComponent(new Component(95, 3, 10));
        Q.addComponent(new Component(12, 2, 11));
        Q.addComponent(new Component(63, 5, 110));
        Q.addComponent(new Component(42, 4, 112));

        LogootIdentifier R = P.plus(2, 72, Q, 100, 2, 50);

        assertEquals(3, R.length());
        assertEquals(P.getComponentAt(0), R.getComponentAt(0));
        assertEquals(62,R.getComponentAt(1).getDigit());
        assertTrue(P.compareTo(R) < 0);
        assertTrue(R.compareTo(Q) < 0);
    }

    @Test
    public void TestPlusShortest() {
        LogootIdentifier P = new LogootIdentifier(3);
        LogootIdentifier Q = new LogootIdentifier(3);

        P.addComponent(new Component(64, 4, 110));
        Q.addComponent(new Component(64, 4, 110));
        Q.addComponent(new Component(9, 4, 112));

        LogootIdentifier R = P.plus(1, 6, Q, 100, 2, 50);

        assertEquals(2, R.length());
        assertEquals(Q.getComponentAt(0), R.getComponentAt(0));
        assertTrue(P.compareTo(R) < 0);
        assertTrue(R.compareTo(Q) < 0);
    }

    @Test
    public void TestPlusShort0() {
        LogootIdentifier P = new LogootIdentifier(3);
        LogootIdentifier Q = new LogootIdentifier(3);

        P.addComponent(new Component(72, 4, 110));
        Q.addComponent(new Component(72, 4, 110));
        Q.addComponent(new Component(0, 0, 1));
        Q.addComponent(new Component(97, 4, 111));

        LogootIdentifier R = P.plus(2, 57, Q, 100, 2, 50);

        assertEquals(3, R.length());
        assertEquals(P.getComponentAt(0), R.getComponentAt(0));
        assertEquals(Q.getComponentAt(1), R.getComponentAt(1));
        assertTrue(P.compareTo(R) < 0);
        assertTrue(R.compareTo(Q) < 0);
    }
}
