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

import java.util.List;
import jbenchmarker.factories.LogootListFactory;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author mehdi urso
 */
public class BoundaryListStrategyTest {
    LogootDocument LD;
    
    LogootListPosition newPos(int v) {
        return new LogootListPosition(8, v);
    }

    LogootListPosition newPos(byte[] v) {
        ByteContent c = new ByteContent(v);
        return new LogootListPosition(c);
    }
    
    @Before
    public void setUp() throws Exception {
        LD = LogootListFactory.createDoc(1, 8);
        LD.setClock(1);
    }

    @Test
    public void testgenerateLineIdentifiersCas1() {
        System.out.println("Test Boundary Strategy...");

        RandomLogootStrategy BS = new BoundaryListStrategy(8);

        LogootListPosition P = newPos(-54);
        LogootListPosition Q = newPos(105);

        List<ListIdentifier> patch = BS.generateLineIdentifiers(LD, P, Q, 100);

        assertEquals(100, patch.size());

        for (int i = 1; i < patch.size(); i++) {
            assertEquals(4, patch.get(i).length());
            assertTrue(patch.get(i).compareTo(P) > 0);
            assertTrue(patch.get(i).compareTo(patch.get(i - 1)) > 0);
            assertTrue(patch.get(i).compareTo(Q) < 0);
        }
    }
    
    @Test
    public void testgenerateLineIdentifiersCas2() {
        RandomLogootStrategy BS = new BoundaryListStrategy(8);
 
        LogootListPosition P = newPos(-54);
        LogootListPosition Q = newPos(105);

        List<ListIdentifier> patch = BS.generateLineIdentifiers(LD, P, Q, 300);

        assertEquals(300, patch.size());

        for (int i = 1; i < patch.size(); i++) {
            assertEquals("" + i, 5 + (i>=255 ? 1 : 0), patch.get(i).length());
            assertTrue(patch.get(i).compareTo(P) > 0);
            assertTrue(patch.get(i).compareTo(patch.get(i - 1)) > 0);
            assertTrue(patch.get(i).compareTo(Q) < 0);
        }
    }
    
    @Test
    public void testgenerateLineIdentifiersCas3() {
        RandomLogootStrategy BS = new BoundaryListStrategy(8);

        LogootListPosition P = newPos(new byte[] { 42, 123, -12 });
        LogootListPosition Q = newPos(new byte[] { 42, 123, 1 });
        
        List<ListIdentifier> patch = BS.generateLineIdentifiers(LD, P, Q, 200);

        assertEquals(200, patch.size());

        for (int i = 1; i < patch.size(); i++) {
            assertEquals(7, patch.get(i).length());
            assertTrue(patch.get(i).compareTo(P) > 0);
            assertTrue(patch.get(i).compareTo(patch.get(i - 1)) > 0);
            assertTrue(patch.get(i).compareTo(Q) < 0);
        }
    }
    
    @Test
    public void testgenerateLineIdentifiersCas4() {
        RandomLogootStrategy BS = new BoundaryListStrategy(8);

        LogootListPosition P = newPos(42);
        LogootListPosition Q = newPos(new byte[] { 42, -13 });
        
        List<ListIdentifier> patch = BS.generateLineIdentifiers(LD, P, Q, 100);

        assertEquals(100, patch.size());

        for (int i = 1; i < patch.size(); i++) {
            assertEquals(5, patch.get(i).length());
            assertTrue(patch.get(i).compareTo(P) > 0);
            assertTrue(patch.get(i).compareTo(patch.get(i - 1)) > 0);
            assertTrue(patch.get(i).compareTo(Q) < 0);
        }
    }
        
    @Test
    public void testgenerateLineIdentifiersCas5() {
        RandomLogootStrategy BS = new BoundaryListStrategy(8);

        LogootListPosition P = newPos(42);
        LogootListPosition Q = newPos(new byte[] { 42, -103 });
        
        List<ListIdentifier> patch = BS.generateLineIdentifiers(LD, P, Q, 100);

        assertEquals(100, patch.size());

        for (int i = 1; i < patch.size(); i++) {
            assertEquals(6, patch.get(i).length());
            assertTrue(patch.get(i).compareTo(P) > 0);
            assertTrue(patch.get(i).compareTo(patch.get(i - 1)) > 0);
            assertTrue(patch.get(i).compareTo(Q) < 0);
        }
    }
    
    @Test
    public void testgenerateLineIdentifiersCas6() {
        System.out.println("Test Boundary Strategy...");

        RandomLogootStrategy BS = new BoundaryListStrategy(8);

        LogootListPosition P = newPos(-54);
        LogootListPosition Q = newPos(-53);

        List<ListIdentifier> patch = BS.generateLineIdentifiers(LD, P, Q, 100);

        assertEquals(100, patch.size());

        for (int i = 1; i < patch.size(); i++) {
            assertEquals(5, patch.get(i).length());
            assertTrue(patch.get(i).compareTo(P) > 0);
            assertTrue(patch.get(i).compareTo(patch.get(i - 1)) > 0);
            assertTrue(patch.get(i).compareTo(Q) < 0);
        }
    }
    
    @Test
    public void testgenerateLineIdentifiersCas7() {
        RandomLogootStrategy BS = new BoundaryListStrategy(8);

        LogootListPosition P = newPos(42);
        LogootListPosition Q = newPos(new byte[] { 43, -13 });
        
        List<ListIdentifier> patch = BS.generateLineIdentifiers(LD, P, Q, 100);

        assertEquals(100, patch.size());

        for (int i = 1; i < patch.size(); i++) {
            assertEquals(5, patch.get(i).length());
            assertTrue(patch.get(i).compareTo(P) > 0);
            assertTrue(patch.get(i).compareTo(patch.get(i - 1)) > 0);
            assertTrue(patch.get(i).compareTo(Q) < 0);
        }
    }
    
    @Test
    public void testgenerateLineIdentifiersCas8() {
        RandomLogootStrategy BS = new BoundaryListStrategy(8);

        LogootListPosition P = newPos(new byte[] { 42, 12 });
        LogootListPosition Q = newPos(new byte[] { 43 });
        
        List<ListIdentifier> patch = BS.generateLineIdentifiers(LD, P, Q, 100);

        assertEquals(100, patch.size());

        for (int i = 1; i < patch.size(); i++) {
            assertEquals(5, patch.get(i).length());
            assertTrue(patch.get(i).compareTo(P) > 0);
            assertTrue(patch.get(i).compareTo(patch.get(i - 1)) > 0);
            assertTrue(patch.get(i).compareTo(Q) < 0);
        }
    }
        
    @Test
    public void testgenerateLineIdentifiersCas9() {
        RandomLogootStrategy BS = new BoundaryListStrategy(8);

        LogootListPosition P = newPos(new byte[] { 42, 120 });
        LogootListPosition Q = newPos(new byte[] { 43, -8 });
        
        List<ListIdentifier> patch = BS.generateLineIdentifiers(LD, P, Q, 1);

        assertEquals(1, patch.size());

        assertEquals(5, patch.get(0).length());
        assertTrue(patch.get(0).compareTo(P) > 0);
        assertTrue(patch.get(0).compareTo(Q) < 0);
    }
    
    @Test
    public void testgenerateLineIdentifiersCas10() {
        RandomLogootStrategy BS = new BoundaryListStrategy(8);

        LogootListPosition P = newPos(new byte[] { 42, 127 });
        LogootListPosition Q = newPos(new byte[] { 43, -128 });
        
        List<ListIdentifier> patch = BS.generateLineIdentifiers(LD, P, Q, 1);

        assertEquals(1, patch.size());

        assertEquals(6, patch.get(0).length());
        assertTrue(patch.get(0).compareTo(P) > 0);
        assertTrue(patch.get(0).compareTo(Q) < 0);
    }
}
