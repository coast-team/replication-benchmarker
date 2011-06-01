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
    long max = 99L;

    @Test
    public void TestConstructSame() {
        LogootIdentifier P = new LogootIdentifier(3);
        LogootIdentifier Q = new LogootIdentifier(3);

        P.addComponent(new Component(6, 4, 110));
        P.addComponent(new Component(8, 4, 110));
        Q.addComponent(new Component(6, 4, 110));
        Q.addComponent(new Component(9, 4, 110));
        
        LogootIdentifier R = constructIdentifier(plus(P.digits(2), 20L, base, max), P, Q, 2, 50);

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

        LogootIdentifier R = constructIdentifier(plus(P.digits(2), 42L, base, max), P, Q, 2, 50);

        assertEquals(3, R.length());
        assertEquals(P.getComponentAt(0), R.getComponentAt(0));
        assertEquals(P.getComponentAt(1), R.getComponentAt(1));
        assertTrue(P.compareTo(R) < 0);
        assertTrue(R.compareTo(Q) < 0);
    }

    @Test
    public void TestConstructShift() {
        LogootIdentifier P = new LogootIdentifier(3);
        LogootIdentifier Q = new LogootIdentifier(3);

        P.addComponent(new Component(61, 4, 110));
        P.addComponent(new Component(95, 3, 10));
        Q.addComponent(new Component(61, 5, 110));
        Q.addComponent(new Component(42, 4, 112));

        LogootIdentifier R = constructIdentifier(plus(P.digits(1), 42L, base, max), P, Q, 2, 50);

        assertEquals(2, R.length());
        assertEquals(Q.getComponentAt(0), R.getComponentAt(0));
        assertTrue(P.compareTo(R) < 0);
        assertTrue(R.compareTo(Q) < 0);
    }

    @Test
    public void TestConstructShiftB() {
        LogootIdentifier P = new LogootIdentifier(3);
        LogootIdentifier Q = new LogootIdentifier(3);

        P.addComponent(new Component(12, 2, 11));
        P.addComponent(new Component(61, 4, 110));
        P.addComponent(new Component(95, 3, 10));
        Q.addComponent(new Component(12, 2, 11));
        Q.addComponent(new Component(63, 5, 110));
        Q.addComponent(new Component(42, 4, 112));

        LogootIdentifier R = constructIdentifier(plus(P.digits(2), 72L, base, max), P, Q, 2, 50);

        assertEquals(3, R.length());
        assertEquals(P.getComponentAt(0), R.getComponentAt(0));
        assertEquals(62,R.getComponentAt(1).getDigit());
        assertTrue(P.compareTo(R) < 0);
        assertTrue(R.compareTo(Q) < 0);
    }
    
    @Test
    public void TestConstructShiftC() {
        LogootIdentifier P = new LogootIdentifier(3);
        LogootIdentifier Q = new LogootIdentifier(3);

        P.addComponent(new Component(12, 2, 11));
        P.addComponent(new Component(61, 4, 110));
        P.addComponent(new Component(95, 3, 10));
        Q.addComponent(new Component(14, 2, 11));
        Q.addComponent(new Component(75, 5, 110));
        Q.addComponent(new Component(42, 4, 112));

        LogootIdentifier R = constructIdentifier(plus(P.digits(2), 10000L, base, max), P, Q, 2, 50);

        assertEquals(3, R.length());
        assertEquals(13,R.getComponentAt(0).getDigit());
        assertTrue(P.compareTo(R) < 0);
        assertTrue(R.compareTo(Q) < 0);
    }
    
    @Test
    public void TestConstructShortest() {
        LogootIdentifier P = new LogootIdentifier(3);
        LogootIdentifier Q = new LogootIdentifier(3);

        P.addComponent(new Component(64, 4, 110));
        Q.addComponent(new Component(64, 4, 110));
        Q.addComponent(new Component(9, 4, 112));

        LogootIdentifier R = constructIdentifier(plus(P.digits(1), 6L, base, max), P, Q, 2, 50);

        assertEquals(2, R.length());
        assertEquals(Q.getComponentAt(0), R.getComponentAt(0));
        assertTrue(P.compareTo(R) < 0);
        assertTrue(R.compareTo(Q) < 0);
    }

    @Test
    public void TestConstructShort0() {
        LogootIdentifier P = new LogootIdentifier(3);
        LogootIdentifier Q = new LogootIdentifier(3);

        P.addComponent(new Component(72, 4, 110));
        Q.addComponent(new Component(72, 4, 110));
        Q.addComponent(new Component(0, 0, 1));
        Q.addComponent(new Component(97, 4, 111));

        LogootIdentifier R = constructIdentifier(plus(P.digits(2), 57L, base, max), P, Q, 2, 50);

        assertEquals(3, R.length());
        assertEquals(P.getComponentAt(0), R.getComponentAt(0));
        assertEquals(Q.getComponentAt(1), R.getComponentAt(1));
        assertTrue(P.compareTo(R) < 0);
        assertTrue(R.compareTo(Q) < 0);
    }
}