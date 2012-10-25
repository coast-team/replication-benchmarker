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
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author mehdi
 */
public class LogootIdentifierTest {
    
    @Test
    public void testDegit() {
        System.out.println("Test LogootIdentifier");
        LogootOneIdentifier P = new LogootOneIdentifier(new BigDecimal("0.1"), 4, 50);
        LogootOneIdentifier Q = new LogootOneIdentifier(new BigDecimal("0.110"), 2, 100);
        
        assertTrue(P.compareTo(Q)>0);
        assertTrue(Q.compareTo(P)<0);
        assertFalse(Q.equals(P));
        assertFalse(P.equals(Q));
    }
    
    @Test
    public void testClock() {
        LogootOneIdentifier P = new LogootOneIdentifier(BigDecimal.valueOf(0.111), 4, 60);
        LogootOneIdentifier Q = new LogootOneIdentifier(BigDecimal.valueOf(0.111), 4, 110);
        
        assertTrue(P.compareTo(Q)<0);
        assertTrue(Q.compareTo(P)>0);
        assertFalse(Q.equals(P));
        assertFalse(P.equals(Q));
    }
    
    @Test
    public void testReplica() {
        LogootOneIdentifier P = new LogootOneIdentifier(BigDecimal.valueOf(0.111), 4, 60);
        LogootOneIdentifier Q = new LogootOneIdentifier(BigDecimal.valueOf(0.111), 5, 60);
        assertTrue(P.compareTo(Q)<0);
        assertTrue(Q.compareTo(P)>0);
        assertFalse(Q.equals(P));
        assertFalse(P.equals(Q));
    }    
}
