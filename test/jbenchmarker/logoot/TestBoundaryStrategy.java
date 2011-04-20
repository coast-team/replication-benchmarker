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

import java.util.ArrayList;



import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author mehdi
 */
public class TestBoundaryStrategy {

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

        for (int i = 1; i < patch.size() - 1; i++) {
            assertTrue(patch.get(i).compareTo(P) > 0);
            assertTrue(patch.get(i).compareTo(patch.get(i - 1)) > 0);
            assertTrue(patch.get(i).compareTo(Q) < 0);
        }
    }

    @Test
    public void testgenerateLineIdentifiersCas2() {
        LogootMerge LM = new LogootMerge(new LogootDocument(Long.MAX_VALUE), 1, 64, new BoundaryStrategy(1000000000));
        BoundaryStrategy BS = new BoundaryStrategy(50);

        LogootIdentifier P = new LogootIdentifier(2);
        LogootIdentifier Q = new LogootIdentifier(2);
        LogootIdentifier R = new LogootIdentifier(2);

        P.addComponent(new Component(20, 4, 50));
        P.addComponent(new Component(30, 4, 60));
        P.addComponent(new Component(40, 4, 70));
        Q.addComponent(new Component(20, 6, 60));
        Q.addComponent(new Component(30, 6, 60));
        Q.addComponent(new Component(40, 6, 60));

        ArrayList<LogootIdentifier> patch = BS.generateLineIdentifiers(LM, P, Q, 100);

        assertEquals(100, patch.size());

        for (int i = 1; i < patch.size() - 1; i++) {
            assertTrue(patch.get(i).compareTo(P) > 0);
            assertTrue(patch.get(i).compareTo(patch.get(i - 1)) > 0);
            assertTrue(patch.get(i).compareTo(Q) < 0);
        }
    }

    @Test
    public void testgenerateLineIdentifiersCas3() {
        LogootMerge LM = new LogootMerge(new LogootDocument(Long.MAX_VALUE), 1, 64, new BoundaryStrategy(1000000000));
        BoundaryStrategy BS = new BoundaryStrategy(50);

        LogootIdentifier P = new LogootIdentifier(2);
        LogootIdentifier Q = new LogootIdentifier(2);
        LogootIdentifier R = new LogootIdentifier(2);


        P.addComponent(new Component(20, 4, 50));
        P.addComponent(new Component(10, 6, 60));
        Q.addComponent(new Component(20, 6, 60));
        Q.addComponent(new Component(30, 4, 60));
        Q.addComponent(new Component(40, 4, 70));

        ArrayList<LogootIdentifier> patch = BS.generateLineIdentifiers(LM, P, Q, 100);

        assertEquals(100, patch.size());

        for (int i = 1; i < patch.size() - 1; i++) {
            assertTrue(patch.get(i).compareTo(P) > 0);
            assertTrue(patch.get(i).compareTo(patch.get(i - 1)) > 0);
            assertFalse(patch.get(i).compareTo(Q) > 0);
        }
    }
}
