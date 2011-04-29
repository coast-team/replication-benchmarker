/*
 * Copyright (C) 2011 mehdi
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



import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author mehdi
 */
public class BoundaryStrategyTest {
    
    //@Ignore
    @Test
    public void testgenerateLineIdentifiersCas1() {
        System.out.println("Test Boundary Strategy...");

        LogootMerge LM = new LogootMerge(new LogootDocument(Long.MAX_VALUE), 1, 64, new BoundaryStrategy(1000000000));
        BoundaryStrategy BS = new BoundaryStrategy(50);

        LogootIdentifier P = new LogootIdentifier(2);
        LogootIdentifier Q = new LogootIdentifier(2);


        P.addComponent(new Component(100, 2, 105));
        P.addComponent(new Component(980, 3, 107));
        Q.addComponent(new Component(100, 4, 150));
        Q.addComponent(new Component(990, 5, 152));

        ArrayList<LogootIdentifier> patch = BS.generateLineIdentifiers(LM, P, Q, 100);

        assertEquals(100, patch.size());

        for (int i = 1; i < patch.size(); i++) {
            assertTrue(patch.get(i).compareTo(P) > 0);
            assertTrue(patch.get(i).compareTo(patch.get(i - 1)) > 0);
            assertTrue(patch.get(i).compareTo(Q) < 0);
        }
    }
    
    //@Ignore
    @Test
    public void testgenerateLineIdentifiersCas2() {
        LogootMerge LM = new LogootMerge(new LogootDocument(Long.MAX_VALUE), 1, 64, new BoundaryStrategy(1000000000));
        BoundaryStrategy BS = new BoundaryStrategy(50);

        LogootIdentifier P = new LogootIdentifier(2);
        LogootIdentifier Q = new LogootIdentifier(2);

        P.addComponent(new Component(20, 4, 50));
        P.addComponent(new Component(30, 4, 60));
        P.addComponent(new Component(40, 4, 70));
        Q.addComponent(new Component(20, 6, 60));
        Q.addComponent(new Component(30, 6, 60));
        Q.addComponent(new Component(40, 6, 60));

        ArrayList<LogootIdentifier> patch = BS.generateLineIdentifiers(LM, P, Q, 200);

        assertEquals(200, patch.size());

        for (int i = 1; i < patch.size(); i++) {
            assertTrue(patch.get(i).compareTo(P) > 0);
            assertTrue(patch.get(i).compareTo(patch.get(i - 1)) > 0);
            assertTrue(patch.get(i).compareTo(Q) < 0);
        }
    }
    
    //@Ignore
    @Test
    public void testgenerateLineIdentifiersCas3() {
        LogootMerge LM = new LogootMerge(new LogootDocument(Long.MAX_VALUE), 1, 64, new BoundaryStrategy(1000000000));
        BoundaryStrategy BS = new BoundaryStrategy(50);

        LogootIdentifier P = new LogootIdentifier(2);
        LogootIdentifier Q = new LogootIdentifier(2);

        P.addComponent(new Component(20, 4, 50));
        P.addComponent(new Component(10, 6, 60));
        Q.addComponent(new Component(20, 6, 60));
        Q.addComponent(new Component(30, 4, 60));
        Q.addComponent(new Component(40, 4, 70));

        ArrayList<LogootIdentifier> patch = BS.generateLineIdentifiers(LM, P, Q, 200);

        assertEquals(200, patch.size());

        for (int i = 1; i < patch.size(); i++) {
            assertTrue(patch.get(i).compareTo(P) > 0);
            assertTrue(patch.get(i).compareTo(patch.get(i - 1)) > 0);
            assertFalse(patch.get(i).compareTo(Q) > 0);
        }
    }
    
    //@Ignore
    @Test
    public void testgenerateLineIdentifiersCas4() {
        LogootMerge LM = new LogootMerge(new LogootDocument(Long.MAX_VALUE), 1, 64, new BoundaryStrategy(1000000000));
        BoundaryStrategy BS = new BoundaryStrategy(50);

        LogootIdentifier P = new LogootIdentifier(3);
        LogootIdentifier Q = new LogootIdentifier(3);


        P.addComponent(new Component(31256, 4, 50));
        P.addComponent(new Component(31256, 6, 60));
        P.addComponent(new Component(31256, 6, 60));
        
        Q.addComponent(new Component(31256, 4, 50));
        Q.addComponent(new Component(31256, 6, 60));
        Q.addComponent(new Component(31257, 6, 60));
        Q.addComponent(new Component(31256, 6, 60));

        ArrayList<LogootIdentifier> patch = BS.generateLineIdentifiers(LM, P, Q, 200);

        assertEquals(200, patch.size());

        for (int i = 1; i < patch.size(); i++) {
            assertTrue(patch.get(i).compareTo(P) > 0);
            assertTrue(patch.get(i).compareTo(patch.get(i - 1)) > 0);
            assertFalse(patch.get(i).compareTo(Q) > 0);
        }
    }
    
    //@Ignore
    @Test
    public void testgenerateLineIdentifiersCas5() {
        LogootMerge LM = new LogootMerge(new LogootDocument(Long.MAX_VALUE), 1, 64, new BoundaryStrategy(1000000000));
        BoundaryStrategy BS = new BoundaryStrategy(50);

        LogootIdentifier P = new LogootIdentifier(3);
        LogootIdentifier Q = new LogootIdentifier(3);


        P.addComponent(new Component(31256, 4, 50));
        P.addComponent(new Component(31256, 6, 60));
        P.addComponent(new Component(31256, 6, 60));
        P.addComponent(new Component(31256, 6, 60));
        
        Q.addComponent(new Component(31256, 4, 50));
        Q.addComponent(new Component(31256, 6, 60));
        Q.addComponent(new Component(31256, 6, 60));
        Q.addComponent(new Component(31256, 7, 60));

        ArrayList<LogootIdentifier> patch = BS.generateLineIdentifiers(LM, P, Q, 200);

        assertEquals(200, patch.size());

        for (int i = 1; i < patch.size(); i++) {
            assertTrue(patch.get(i).compareTo(P) > 0);
            assertTrue(patch.get(i).compareTo(patch.get(i - 1)) > 0);
            assertFalse(patch.get(i).compareTo(Q) > 0);
        }
    }
    //@Ignore
    @Test
    public void testgenerateLineIdentifiersCas6() {
        LogootMerge LM = new LogootMerge(new LogootDocument(Long.MAX_VALUE), 1, 64, new BoundaryStrategy(1000000000));
        BoundaryStrategy BS = new BoundaryStrategy(50);

        LogootIdentifier P = new LogootIdentifier(3);
        LogootIdentifier Q = new LogootIdentifier(3);


        P.addComponent(new Component(31256, 4, 50));
        P.addComponent(new Component(31256, 6, 60));
        P.addComponent(new Component(31256, 6, 60));
        P.addComponent(new Component(31255, 8, 60));
        
        Q.addComponent(new Component(31256, 4, 50));
        Q.addComponent(new Component(31256, 6, 60));
        Q.addComponent(new Component(31256, 6, 60));
        Q.addComponent(new Component(31256, 7, 60));

        ArrayList<LogootIdentifier> patch = BS.generateLineIdentifiers(LM, P, Q, 200);

        assertEquals(200, patch.size());

        for (int i = 1; i < patch.size(); i++) {
            assertTrue(patch.get(i).compareTo(P) > 0);
            assertTrue(patch.get(i).compareTo(patch.get(i - 1)) > 0);
            assertFalse(patch.get(i).compareTo(Q) > 0);
        }
    }
     //@Ignore
     @Test
    public void testgenerateLineIdentifiersCas7() {
        LogootMerge LM = new LogootMerge(new LogootDocument(Long.MAX_VALUE), 11, 64, new BoundaryStrategy(1000000000));
        BoundaryStrategy BS = new BoundaryStrategy(50);

        LogootIdentifier P = new LogootIdentifier(6);
        LogootIdentifier Q = new LogootIdentifier(6);

        
        LM.setClock(101);
        P.addComponent(new Component(12, 8, 60));
        P.addComponent(new Component(7, 8, 60));
        P.addComponent(new Component(22, 8, 60));
        
        Q.addComponent(new Component(12, 8, 60));
        Q.addComponent(new Component(7, 8, 60));
        Q.addComponent(new Component(22, 8, 60));
        Q.addComponent(new Component(0, 1, 2));
        Q.addComponent(new Component(0, 2, 10));
        Q.addComponent(new Component(15, 11, 100));

        ArrayList<LogootIdentifier> patch = BS.generateLineIdentifiers(LM, P, Q, 2);

        assertEquals(2, patch.size());
        for (int i = 1; i < patch.size(); i++) {
            assertTrue(patch.get(i).compareTo(P) > 0);
            assertTrue(patch.get(i).compareTo(patch.get(i - 1)) > 0);
            assertFalse(patch.get(i).compareTo(Q) > 0);
        }
    }
    
}
