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
