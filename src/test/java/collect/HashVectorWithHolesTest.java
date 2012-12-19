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
