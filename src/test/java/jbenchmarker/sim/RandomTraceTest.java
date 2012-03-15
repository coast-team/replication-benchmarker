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
package jbenchmarker.sim;

import jbenchmarker.trace.CausalCheckerFactory;
import jbenchmarker.trace.IncorrectTrace;
import jbenchmarker.trace.TraceOperation;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test of random trace
 * @author urso
 */
public class RandomTraceTest {
    

    /**
     * Test of next method, of class RandomTrace.
     */
    @Test
    public void testNext() {
        System.out.println("next");
        RandomTrace instance = new RandomTrace(42, RandomTrace.FLAT, new StandardOpProfile(0.5, 0.5, 10, 1.0), 1.0, 10, 1.0, 13);
        assertTrue(instance.hasNext());
        int n = 0;
        while (instance.hasNext()) {
            n++;
            instance.next();
        }
        assertEquals(42*13, n);
    }
    
    /**
     * Test of causality, of class RandomTrace.
     */
    @Test
    public void Causality() throws IncorrectTrace {
        System.out.println("causality");
        RandomTrace instance = new RandomTrace(240, RandomTrace.FLAT, new StandardOpProfile(0.5, 0.5, 10, 1.0), 0.2, 10, 3.0, 13);
        OldCausalDispatcher cd = new OldCausalDispatcher(new CausalCheckerFactory());
        cd.run(instance);
    }    
}
