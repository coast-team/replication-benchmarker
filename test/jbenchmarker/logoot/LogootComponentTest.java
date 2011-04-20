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
public class LogootComponentTest {

    @Test
    public void testEquals() {
        assertEquals(new Component(4, 5, 99999), new Component(4, 5, 99999));
    }

    @Test
    public void testComponent() {
        System.out.println("Test Component ...");

        //Creation
        Component c1 = new Component(20, 4, 50);
        assertEquals(20, c1.getDigit());
        assertEquals(4, c1.getPeerID());
        assertEquals(50, c1.getClock());

    }

    @Test
    public void testEqualsTo() {
        Component c1 = new Component(20, 4, 50);
        Component c2 = new Component(20, 4, 50);

        assertEquals(true, c1.equals(c2));
    }

    @Test
    public void testCompareTo() {
        Component c1 = new Component(20, 4, 50);
        Component c2 = new Component(20, 6, 50);

        assertTrue(c1.compareTo(c2) < 0);

    }
}
