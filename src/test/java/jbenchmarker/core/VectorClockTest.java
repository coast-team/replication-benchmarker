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

package jbenchmarker.core;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author urso
 */
public class VectorClockTest {

    /**
     * Test of readyFor method, of class VectorClock.
     */
    @Test
    public void testReadyFor() {
        System.out.println("readyFor");
        VectorClock O = new VectorClock();
        VectorClock V = new VectorClock();
        
        O.put(3, 1);
        assertTrue(V.readyFor(3, O));
        
        O.put(3, 4);
        assertFalse(V.readyFor(3, O));

        V.put(3, 3);
        assertTrue(V.readyFor(3, O));

        O.put(7, 6);
        assertFalse(V.readyFor(3, O));        

        V.put(7, 5);
        assertFalse(V.readyFor(3, O));

        V.put(7, 6);
        assertTrue(V.readyFor(3, O));

        V.put(7, 8);
        assertTrue(V.readyFor(3, O));

        V.put(1, 3);
        assertTrue(V.readyFor(3, O));

        O.put(9, 1);
        assertFalse(V.readyFor(3, O));        
    }


    /**
     * Test of greaterThan method, of class VectorClock.
     */
    @Test
    public void testGreaterThan() {
        System.out.println("greaterThan");
        VectorClock O = new VectorClock();
        VectorClock V = new VectorClock();
        
        assertFalse(O.greaterThan(V));
        
        O.put(2, 0);
        assertFalse(O.greaterThan(V));
        assertFalse(V.greaterThan(O));
        
        O.put(3, 1);
        assertTrue(O.greaterThan(V));
        assertFalse(V.greaterThan(O));
        
        V.put(9, 0);
        assertTrue(O.greaterThan(V));
        assertFalse(V.greaterThan(O));

        V.put(3, 1);
        assertFalse(O.greaterThan(V));
        assertFalse(V.greaterThan(O));
        
        V.put(3, 2);
        assertFalse(O.greaterThan(V));
        assertTrue(V.greaterThan(O));        
        
        O.put(1, 1);
        assertFalse(O.greaterThan(V));
        assertFalse(V.greaterThan(O));
        
        V.put(6, 7);
        assertFalse(O.greaterThan(V));
        assertFalse(V.greaterThan(O));        
        
        V.put(1, 1);
        assertFalse(O.greaterThan(V));
        assertTrue(V.greaterThan(O));    
    }

}