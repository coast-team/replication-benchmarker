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

import java.math.BigInteger;
import org.junit.Test;
import org.junit.AfterClass;
import org.junit.BeforeClass;
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

        assertEquals(3, P.plus(2, 20, Q, 100, 2, 50).length());
        assertTrue(P.compareTo(P.plus(1, 20, Q, 100, 2, 50)) < 0);
        assertTrue(P.plus(1, 20, Q, 100, 2, 50).compareTo(Q) > 0);
    }

    @Test
    public void TestplusSameDigit() {
        LogootIdentifier P = new LogootIdentifier(3);
        LogootIdentifier Q = new LogootIdentifier(3);

        P.addComponent(new Component(6, 4, 110));
        P.addComponent(new Component(9, 3, 110));
        Q.addComponent(new Component(6, 4, 110));
        Q.addComponent(new Component(9, 4, 110));

        assertEquals(3, P.plus(2, 20, Q, 100, 2, 50).length());
        assertTrue(P.compareTo(P.plus(1, 20, Q, 100, 2, 50)) < 0);
        assertTrue(P.plus(1, 20, Q, 100, 2, 50).compareTo(Q) > 0);
    }

    
    
}
