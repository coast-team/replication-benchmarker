/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker.logoot;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author urso
 */
public class LogootListPositionTest {
    
    public LogootListPositionTest() {
    }

    @Test
    public void testByte() {
        LogootListPosition p = new LogootListPosition(8, 7, 42, 54);
        assertEquals(42, p.replica());
        assertEquals(54, p.clock());

        LogootListPosition q = new LogootListPosition(8, 7, 4298573, 54);
        assertEquals(4298573, q.replica());
        assertEquals(54, q.clock());
    
        LogootListPosition r = new LogootListPosition(8, 7, 4298573, 54583425);
        assertEquals(4298573, r.replica());
        assertEquals(54583425, r.clock());
    }
    
    @Test
    public void testShort() {
        LogootListPosition p = new LogootListPosition(16, 7, 42, 54);
        assertEquals(42, p.replica());
        assertEquals(54, p.clock());

        LogootListPosition q = new LogootListPosition(16, 7, 4298573, 54);
        assertEquals(4298573, q.replica());
        assertEquals(54, q.clock());
    
        LogootListPosition r = new LogootListPosition(16, 7, 4298573, 54583425);
        assertEquals(4298573, r.replica());
        assertEquals(54583425, r.clock());
    }
            
    @Test
    public void testInt() {
        LogootListPosition p = new LogootListPosition(32, 7, 42, 54);
        assertEquals(42, p.replica());
        assertEquals(54, p.clock());

        LogootListPosition q = new LogootListPosition(32, 7, 4298573, 54);
        assertEquals(4298573, q.replica());
        assertEquals(54, q.clock());
    
        LogootListPosition r = new LogootListPosition(32, 7, 4298573, 54583425);
        assertEquals(4298573, r.replica());
        assertEquals(54583425, r.clock());
    }
}
