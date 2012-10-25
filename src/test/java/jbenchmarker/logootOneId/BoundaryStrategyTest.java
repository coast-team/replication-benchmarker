/**
 * Replication Benchmarker
 * https://github.com/score-team/replication-benchmarker/
 * Copyright (C) 2011 INRIA / LORIA / SCORE Team
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
package jbenchmarker.logootOneId;

import java.math.BigDecimal;
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

        LogootOneIdDocument LM = new LogootOneIdDocument(1, new BoundaryStrategy(100));
        BoundaryStrategy BS = new BoundaryStrategy(10);

        LogootOneIdentifier P = new LogootOneIdentifier(BigDecimal.valueOf(0.1));
        LogootOneIdentifier Q = new LogootOneIdentifier(BigDecimal.valueOf(0.2));

        ArrayList<LogootOneIdentifier> patch = BS.generateLineIdentifiers(LM, P, Q, 100);

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
        LogootOneIdDocument LM = new LogootOneIdDocument(1, new BoundaryStrategy(100));
        BoundaryStrategy BS = new BoundaryStrategy(50);

        LogootOneIdentifier P = new LogootOneIdentifier(BigDecimal.valueOf(0.21));
        LogootOneIdentifier Q = new LogootOneIdentifier(BigDecimal.valueOf(0.22));

        ArrayList<LogootOneIdentifier> patch = BS.generateLineIdentifiers(LM, P, Q, 200);
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
        LogootOneIdDocument LM = new LogootOneIdDocument(1, new BoundaryStrategy(100));
        BoundaryStrategy BS = new BoundaryStrategy(50);

        LogootOneIdentifier P = new LogootOneIdentifier(BigDecimal.valueOf(0.20012));
        LogootOneIdentifier Q = new LogootOneIdentifier(BigDecimal.valueOf(0.20013));


        ArrayList<LogootOneIdentifier> patch = BS.generateLineIdentifiers(LM, P, Q, 200);

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
        LogootOneIdDocument LM = new LogootOneIdDocument(1, new BoundaryStrategy(100));
        BoundaryStrategy BS = new BoundaryStrategy(50);

        LogootOneIdentifier P = new LogootOneIdentifier(BigDecimal.valueOf(0.21));
        LogootOneIdentifier Q = new LogootOneIdentifier(BigDecimal.valueOf(0.22));


        ArrayList<LogootOneIdentifier> patch = BS.generateLineIdentifiers(LM, P, Q, 200);

        assertEquals(200, patch.size());

        for (int i = 1; i < patch.size(); i++) {
            assertTrue(patch.get(i).compareTo(P) > 0);
            assertTrue(patch.get(i).compareTo(patch.get(i - 1)) > 0);
            assertTrue(patch.get(i).compareTo(Q) < 0);
        }
    }
    
    //@Ignore
    @Test
    public void testgenerateLineIdentifiersCas5() {
        LogootOneIdDocument LM = new LogootOneIdDocument(1, new BoundaryStrategy(100));
        BoundaryStrategy BS = new BoundaryStrategy(50);

        LogootOneIdentifier P = new LogootOneIdentifier(BigDecimal.valueOf(0.12));
        LogootOneIdentifier Q = new LogootOneIdentifier(BigDecimal.valueOf(0.14));


        ArrayList<LogootOneIdentifier> patch = BS.generateLineIdentifiers(LM, P, Q, 200);

        assertEquals(200, patch.size());

        for (int i = 1; i < patch.size(); i++) {
            assertTrue(patch.get(i).compareTo(P) > 0);
            assertTrue(patch.get(i).compareTo(patch.get(i - 1)) > 0);
            assertTrue(patch.get(i).compareTo(Q) < 0);
        }
    }
    
    //@Ignore
     @Test
    public void testgenerateLineIdentifiersCas7() {
        LogootOneIdDocument LM = new LogootOneIdDocument(1, new BoundaryStrategy(100));
        BoundaryStrategy BS = new BoundaryStrategy(50);

        LogootOneIdentifier P = new LogootOneIdentifier(BigDecimal.valueOf(0.12));
        LogootOneIdentifier Q = new LogootOneIdentifier(BigDecimal.valueOf(0.16));


        ArrayList<LogootOneIdentifier> patch = BS.generateLineIdentifiers(LM, P, Q, 3);

        assertEquals(3, patch.size());
        for (int i = 1; i < patch.size(); i++) {
            assertTrue(patch.get(i).compareTo(P) > 0);
            assertTrue(patch.get(i).compareTo(patch.get(i - 1)) > 0);
            assertTrue(patch.get(i).compareTo(Q) < 0);
        }
    }
    
    
//    
//    //@Ignore
//    @Test
//    public void testgenerateLineIdentifiersCas4() {
//        LogootMerge LM = new LogootMerge(new LogootDocument(Long.MAX_VALUE), 1, 64, new BoundaryStrategy(1000000000));
//        BoundaryStrategy BS = new BoundaryStrategy(50);
//
//        LogootIdentifier P = new LogootIdentifier(3);
//        LogootIdentifier Q = new LogootIdentifier(3);
//
//
//        P.addComponent(new Component(31256, 4, 50));
//        P.addComponent(new Component(31256, 6, 60));
//        P.addComponent(new Component(31256, 6, 60));
//        
//        Q.addComponent(new Component(31256, 4, 50));
//        Q.addComponent(new Component(31256, 6, 60));
//        Q.addComponent(new Component(31257, 6, 60));
//        Q.addComponent(new Component(31256, 6, 60));
//
//        ArrayList<LogootIdentifier> patch = BS.generateLineIdentifiers(LM, P, Q, 200);
//
//        assertEquals(200, patch.size());
//
//        for (int i = 1; i < patch.size(); i++) {
//            assertTrue(patch.get(i).compareTo(P) > 0);
//            assertTrue(patch.get(i).compareTo(patch.get(i - 1)) > 0);
//            assertFalse(patch.get(i).compareTo(Q) > 0);
//        }
//    }
//    
//    //@Ignore
//    @Test
//    public void testgenerateLineIdentifiersCas5() {
//        LogootMerge LM = new LogootMerge(new LogootDocument(Long.MAX_VALUE), 1, 64, new BoundaryStrategy(1000000000));
//        BoundaryStrategy BS = new BoundaryStrategy(50);
//
//        LogootIdentifier P = new LogootIdentifier(3);
//        LogootIdentifier Q = new LogootIdentifier(3);
//
//
//        P.addComponent(new Component(31256, 4, 50));
//        P.addComponent(new Component(31256, 6, 60));
//        P.addComponent(new Component(31256, 6, 60));
//        P.addComponent(new Component(31256, 6, 60));
//        
//        Q.addComponent(new Component(31256, 4, 50));
//        Q.addComponent(new Component(31256, 6, 60));
//        Q.addComponent(new Component(31256, 6, 60));
//        Q.addComponent(new Component(31256, 7, 60));
//
//        ArrayList<LogootIdentifier> patch = BS.generateLineIdentifiers(LM, P, Q, 200);
//
//        assertEquals(200, patch.size());
//
//        for (int i = 1; i < patch.size(); i++) {
//            assertTrue(patch.get(i).compareTo(P) > 0);
//            assertTrue(patch.get(i).compareTo(patch.get(i - 1)) > 0);
//            assertFalse(patch.get(i).compareTo(Q) > 0);
//        }
//    }
//    //@Ignore
//    @Test
//    public void testgenerateLineIdentifiersCas6() {
//        LogootMerge LM = new LogootMerge(new LogootDocument(Long.MAX_VALUE), 1, 64, new BoundaryStrategy(1000000000));
//        BoundaryStrategy BS = new BoundaryStrategy(50);
//
//        LogootIdentifier P = new LogootIdentifier(3);
//        LogootIdentifier Q = new LogootIdentifier(3);
//
//
//        P.addComponent(new Component(31256, 4, 50));
//        P.addComponent(new Component(31256, 6, 60));
//        P.addComponent(new Component(31256, 6, 60));
//        P.addComponent(new Component(31255, 8, 60));
//        
//        Q.addComponent(new Component(31256, 4, 50));
//        Q.addComponent(new Component(31256, 6, 60));
//        Q.addComponent(new Component(31256, 6, 60));
//        Q.addComponent(new Component(31256, 7, 60));
//
//        ArrayList<LogootIdentifier> patch = BS.generateLineIdentifiers(LM, P, Q, 200);
//
//        assertEquals(200, patch.size());
//
//        for (int i = 1; i < patch.size(); i++) {
//            assertTrue(patch.get(i).compareTo(P) > 0);
//            assertTrue(patch.get(i).compareTo(patch.get(i - 1)) > 0);
//            assertFalse(patch.get(i).compareTo(Q) > 0);
//        }
//    }
//     //@Ignore
//     @Test
//    public void testgenerateLineIdentifiersCas7() {
//        LogootMerge LM = new LogootMerge(new LogootDocument(Long.MAX_VALUE), 11, 64, new BoundaryStrategy(1000000000));
//        BoundaryStrategy BS = new BoundaryStrategy(50);
//
//        LogootIdentifier P = new LogootIdentifier(6);
//        LogootIdentifier Q = new LogootIdentifier(6);
//
//        
//        LM.setClock(101);
//        P.addComponent(new Component(12, 8, 60));
//        P.addComponent(new Component(7, 8, 60));
//        P.addComponent(new Component(22, 8, 60));
//        
//        Q.addComponent(new Component(12, 8, 60));
//        Q.addComponent(new Component(7, 8, 60));
//        Q.addComponent(new Component(22, 8, 60));
//        Q.addComponent(new Component(0, 1, 2));
//        Q.addComponent(new Component(0, 2, 10));
//        Q.addComponent(new Component(15, 11, 100));
//
//        ArrayList<LogootIdentifier> patch = BS.generateLineIdentifiers(LM, P, Q, 2);
//
//        assertEquals(2, patch.size());
//        for (int i = 1; i < patch.size(); i++) {
//            assertTrue(patch.get(i).compareTo(P) > 0);
//            assertTrue(patch.get(i).compareTo(patch.get(i - 1)) > 0);
//            assertFalse(patch.get(i).compareTo(Q) > 0);
//        }
//    }
    
}
