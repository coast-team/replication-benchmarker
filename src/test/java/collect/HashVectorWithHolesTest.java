/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package collect;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author urso
 */
public class HashVectorWithHolesTest {
    
    HashVectorWithHoles vch;
    
    public HashVectorWithHolesTest() {
        vch = new HashVectorWithHoles();
    }

    /**
     * Test of belongs method, of class HashVectorWithHoles.
     */
    @Test
    public void tests() {
        assertFalse(vch.contains(0, 0));
        assertFalse(vch.contains(7, 0));
        assertFalse(vch.contains(7, 7));
        vch.add(7, 0);
        assertFalse(vch.contains(0, 0));
        assertTrue(vch.contains(7, 0));
        assertFalse(vch.contains(7, 1));
        assertFalse(vch.contains(7, 7));
        vch.add(7, 1);
        assertFalse(vch.contains(0, 0));
        assertTrue(vch.contains(7, 0));
        assertTrue(vch.contains(7, 1));
        assertFalse(vch.contains(7, 7));
        assertTrue(vch.map.get(7).holes.isEmpty());
        vch.add(7, 4);
        assertFalse(vch.contains(0, 0));
        assertTrue(vch.contains(7, 0));
        assertTrue(vch.contains(7, 1));
        assertFalse(vch.contains(7, 2));
        assertFalse(vch.contains(7, 3));
        assertTrue(vch.contains(7, 4));
        assertFalse(vch.contains(7, 7));
        assertEquals(2, vch.map.get(7).holes.size());
        vch.add(7, 2);
        assertFalse(vch.contains(0, 0));
        assertTrue(vch.contains(7, 0));
        assertTrue(vch.contains(7, 1));
        assertTrue(vch.contains(7, 2));
        assertFalse(vch.contains(7, 3));
        assertTrue(vch.contains(7, 4));
        assertFalse(vch.contains(7, 7));
        assertEquals(1, vch.map.get(7).holes.size());
        vch.add(7, 3);
        assertFalse(vch.contains(0, 0));
        assertTrue(vch.contains(7, 0));
        assertTrue(vch.contains(7, 1));
        assertTrue(vch.contains(7, 2));
        assertTrue(vch.contains(7, 3));
        assertTrue(vch.contains(7, 4));
        assertFalse(vch.contains(7, 7));
        assertEquals(0, vch.map.get(7).holes.size());
    }

}
