/*
 *  Copyright (C) 2011 urso
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package jbenchmarker.logoot;

import java.math.BigInteger;
import org.junit.Test;
import static org.junit.Assert.*;
import static jbenchmarker.logoot.LogootStrategy.*;

/**
 *
 * @author urso
 */
public class LogootStrategyTest {
    BigInteger base = BigInteger.valueOf(100);

    @Test
    public void TestPlusSame() {
        LogootIdentifier P = new LogootIdentifier(3);
        LogootIdentifier Q = new LogootIdentifier(3);

        P.addComponent(new Component(6, 4, 110));
        P.addComponent(new Component(8, 4, 110));
        Q.addComponent(new Component(6, 4, 110));
        Q.addComponent(new Component(9, 4, 110));

        LogootIdentifier R = plus(2, 20, base, P, Q, 2, 50);

        assertEquals(3, R.length());
        assertEquals(P.getComponentAt(0), R.getComponentAt(0));
        assertEquals(P.getComponentAt(1), R.getComponentAt(1));
        assertTrue(P.compareTo(R) < 0);
        assertTrue(R.compareTo(Q) < 0);
    }

    @Test
    public void TestplusSameB() {
        LogootIdentifier P = new LogootIdentifier(3);
        LogootIdentifier Q = new LogootIdentifier(3);

        P.addComponent(new Component(6, 4, 110));
        P.addComponent(new Component(9, 3, 10));
        Q.addComponent(new Component(6, 4, 110));
        Q.addComponent(new Component(9, 4, 112));

        LogootIdentifier R = plus(2, 42, base, P, Q, 2, 50);

        assertEquals(3, R.length());
        assertEquals(P.getComponentAt(0), R.getComponentAt(0));
        assertEquals(P.getComponentAt(1), R.getComponentAt(1));
        assertTrue(P.compareTo(R) < 0);
        assertTrue(R.compareTo(Q) < 0);
    }

    @Test
    public void TestPlusShift() {
        LogootIdentifier P = new LogootIdentifier(3);
        LogootIdentifier Q = new LogootIdentifier(3);

        P.addComponent(new Component(61, 4, 110));
        P.addComponent(new Component(95, 3, 10));
        Q.addComponent(new Component(61, 5, 110));
        Q.addComponent(new Component(42, 4, 112));

        LogootIdentifier R = plus(1, 42, base, P, Q, 2, 50);

        assertEquals(2, R.length());
        assertEquals(Q.getComponentAt(0), R.getComponentAt(0));
        assertTrue(P.compareTo(R) < 0);
        assertTrue(R.compareTo(Q) < 0);
    }

    @Test
    public void TestPlusShiftB() {
        LogootIdentifier P = new LogootIdentifier(3);
        LogootIdentifier Q = new LogootIdentifier(3);

        P.addComponent(new Component(12, 2, 11));
        P.addComponent(new Component(61, 4, 110));
        P.addComponent(new Component(95, 3, 10));
        Q.addComponent(new Component(12, 2, 11));
        Q.addComponent(new Component(63, 5, 110));
        Q.addComponent(new Component(42, 4, 112));

        LogootIdentifier R = plus(2, 72, base, P, Q, 2, 50);

        assertEquals(3, R.length());
        assertEquals(P.getComponentAt(0), R.getComponentAt(0));
        assertEquals(62,R.getComponentAt(1).getDigit());
        assertTrue(P.compareTo(R) < 0);
        assertTrue(R.compareTo(Q) < 0);
    }

    @Test
    public void TestPlusShortest() {
        LogootIdentifier P = new LogootIdentifier(3);
        LogootIdentifier Q = new LogootIdentifier(3);

        P.addComponent(new Component(64, 4, 110));
        Q.addComponent(new Component(64, 4, 110));
        Q.addComponent(new Component(9, 4, 112));

        LogootIdentifier R = plus(1, 6, base, P, Q, 2, 50);

        assertEquals(2, R.length());
        assertEquals(Q.getComponentAt(0), R.getComponentAt(0));
        assertTrue(P.compareTo(R) < 0);
        assertTrue(R.compareTo(Q) < 0);
    }

    @Test
    public void TestPlusShort0() {
        LogootIdentifier P = new LogootIdentifier(3);
        LogootIdentifier Q = new LogootIdentifier(3);

        P.addComponent(new Component(72, 4, 110));
        Q.addComponent(new Component(72, 4, 110));
        Q.addComponent(new Component(0, 0, 1));
        Q.addComponent(new Component(97, 4, 111));

        LogootIdentifier R = plus(2, 57, base, P, Q, 2, 50);

        assertEquals(3, R.length());
        assertEquals(P.getComponentAt(0), R.getComponentAt(0));
        assertEquals(Q.getComponentAt(1), R.getComponentAt(1));
        assertTrue(P.compareTo(R) < 0);
        assertTrue(R.compareTo(Q) < 0);
    }
}