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
package jbenchmarker.logoot;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author mehdi
 */
public class LogootIdentifierTest {
    
    LogootComponent c42 = new LogootComponent(42, 4, 50);
    LogootComponent c70 = new LogootComponent(70, 2, 100);
    LogootComponent c42b = new LogootComponent(42, 4, 60);
    LogootComponent c6 = new LogootComponent(6, 4, 110);

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

        P.addComponent(new LogootComponent(6, 4, 110));
        Q.addComponent(new LogootComponent(6, 4, 110));
        
        assertSame(0, P.compareTo(Q));
        assertSame(0, Q.compareTo(P));
        assertTrue(Q.equals(P));
        assertTrue(P.equals(Q));
    }
    
    
    @Test
    public void testIsLessThan3e2() {
        LogootIdentifier P = new LogootIdentifier(3);
        LogootIdentifier Q = new LogootIdentifier(3);

        P.addComponent(new LogootComponent(6, 4, 110));P.addComponent(new LogootComponent(8, 4, 110));
        Q.addComponent(new LogootComponent(6, 4, 110));Q.addComponent(new LogootComponent(8, 4, 110));
        Q.addComponent(new LogootComponent(20, 2, 50)); 
        
        assertTrue(P.compareTo(Q)<0);
        assertTrue(Q.compareTo(P)>0);
        assertFalse(Q.equals(P));
        assertFalse(P.equals(Q));
    }
}
