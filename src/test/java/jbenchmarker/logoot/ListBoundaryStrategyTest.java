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
package jbenchmarker.logoot;

import org.junit.Ignore;
import java.util.ArrayList;
import jbenchmarker.factories.LogootFactory;
import jbenchmarker.factories.LogootListFactory;
import jbenchmarker.factories.TreedocFactory;
import jbenchmarker.treedoc.TreedocMerge;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author mehdi urso
 */
public class ListBoundaryStrategyTest {
    LogootDocument LD;
    
    @Before
    public void setUp() throws Exception {
        LD = LogootListFactory.createDoc(1, 50);
    }

    @Test
    public void testgenerateLineIdentifiersCas1() {
        System.out.println("Test Boundary Strategy...");

        BoundaryStrategy BS = new BoundaryStrategy(50);

        LogootListPosition P = new LogootListPosition((byte) -54);
        LogootListPosition Q = new LogootListPosition((byte) 105);

        ArrayList<ListIdentifier> patch = BS.generateLineIdentifiers(LD, P, Q, 100);

        assertEquals(100, patch.size());

        for (int i = 1; i < patch.size(); i++) {
            assertEquals(9, patch.get(i).length());
            assertTrue(patch.get(i).compareTo(P) > 0);
            assertTrue(patch.get(i).compareTo(patch.get(i - 1)) > 0);
            assertTrue(patch.get(i).compareTo(Q) < 0);
        }
    }
    
    @Test
    public void testgenerateLineIdentifiersCas2() {
        BoundaryStrategy BS = new BoundaryStrategy(50);
 
        LogootListPosition P = new LogootListPosition((byte) -54);
        LogootListPosition Q = new LogootListPosition((byte) 105);

        ArrayList<ListIdentifier> patch = BS.generateLineIdentifiers(LD, P, Q, 200);

        assertEquals(200, patch.size());

        for (int i = 1; i < patch.size(); i++) {
            assertEquals(10, patch.get(i).length());
            assertTrue(patch.get(i).compareTo(P) > 0);
            assertTrue(patch.get(i).compareTo(patch.get(i - 1)) > 0);
            assertTrue(patch.get(i).compareTo(Q) < 0);
        }
    }
    
    @Test
    public void testgenerateLineIdentifiersCas3() {
        BoundaryStrategy BS = new BoundaryStrategy(50);

        LogootListPosition P = new LogootListPosition((byte) 42);
        P.position.add((byte) 123);
        P.position.add((byte) -12);
        LogootListPosition Q = new LogootListPosition((byte) 42);
        Q.position.add((byte) 123);
        Q.position.add((byte) 1);
        
        ArrayList<ListIdentifier> patch = BS.generateLineIdentifiers(LD, P, Q, 200);

        assertEquals(200, patch.size());

        for (int i = 1; i < patch.size(); i++) {
            assertEquals(12, patch.get(i).length());
            assertTrue(patch.get(i).compareTo(P) > 0);
            assertTrue(patch.get(i).compareTo(patch.get(i - 1)) > 0);
            assertFalse(patch.get(i).compareTo(Q) > 0);
        }
    }
}
