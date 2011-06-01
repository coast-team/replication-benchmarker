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