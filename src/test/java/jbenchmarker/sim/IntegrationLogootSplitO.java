/**
 * Replication Benchmarker
 * https://github.com/score-team/replication-benchmarker/ Copyright (C) 2013
 * LORIA / Inria / SCORE Team
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package jbenchmarker.sim;

import crdt.simulator.CausalSimulator;
import crdt.simulator.Trace;
import crdt.simulator.random.RandomTrace;
import crdt.simulator.random.StandardDiffProfile;
import crdt.simulator.random.StandardSeqOpProfile;
import jbenchmarker.factories.LogootSplitOFactory;
import static jbenchmarker.sim.CausalDispatcherTest.assertConsistency;
import static jbenchmarker.sim.CausalDispatcherTest.assertGoodViewLength;
import jbenchmarker.trace.TraceGenerator;
import org.junit.Ignore;
import org.junit.Test;

public class IntegrationLogootSplitO {

    @Test
    public void testLogootSplitOG1Run() throws Exception {
        Trace trace = TraceGenerator.traceFromXML(TracesExample.getExampleTraceMatch("G1.xml"), 1);
        CausalSimulator cd = new CausalSimulator(new LogootSplitOFactory());

        assertConsistency(cd, trace);
    }

    @Test
    public void testLogootSplitOG2Run() throws Exception {
        Trace trace = TraceGenerator.traceFromXML(TracesExample.getExampleTraceMatch("G2.xml"),1);
        CausalSimulator cd = new CausalSimulator(new LogootSplitOFactory());

        assertConsistency(cd, trace);
    }

    @Test
    public void testLogootSplitOG3Run() throws Exception {
        Trace trace = TraceGenerator.traceFromXML(TracesExample.getExampleTraceMatch("G3.xml"), 1);
        CausalSimulator cd = new CausalSimulator(new LogootSplitOFactory());

        assertConsistency(cd, trace);
    }

    @Test
    public void testLogootSplitOSerieRun() throws Exception {
        Trace trace = TraceGenerator.traceFromXML(TracesExample.getExampleTraceMatch("Serie.xml"), 1);
        CausalSimulator cd = new CausalSimulator(new LogootSplitOFactory());

        assertConsistency(cd, trace);
    }

    @Test
    public void testLogootSplitOProfile() throws Exception {
        for (int i = 0; i < 20; ++i) {
            Trace trace = new RandomTrace(4200, RandomTrace.FLAT, new StandardSeqOpProfile(0.8, 0.1, 40, 5.0), 0.1, 10, 3.0, 13);
            CausalSimulator cd = new CausalSimulator(new LogootSplitOFactory());

            cd.run(trace);
        }
    }

    @Ignore // only to find a bug
    @Test
    public void testLogootSplitORandomMini() throws Exception {
        for (int i = 0; i < 100000; i++) {
            Trace trace = new RandomTrace(10, RandomTrace.FLAT, new StandardSeqOpProfile(0.8, 0.1, 1, 5.0), 0.1, 10, 3.0, 13);
            CausalSimulator cd = new CausalSimulator(new LogootSplitOFactory());
            assertConsistency(cd, trace);
            assertGoodViewLength(cd);
        }

    }

    @Test
    public void testLogootSplitORandom() throws Exception {
        Trace trace = new RandomTrace(4200, RandomTrace.FLAT, new StandardSeqOpProfile(0.8, 0.1, 40, 5.0), 0.1, 10, 3.0, 13);
        CausalSimulator cd = new CausalSimulator(new LogootSplitOFactory());
        assertConsistency(cd, trace);
        assertGoodViewLength(cd);


    }

    @Ignore // not ready to integrate lines
    @Test
    public void testLogootSplitORandomDiff() throws Exception {
        Trace trace = new RandomTrace(420, RandomTrace.FLAT, StandardDiffProfile.BASIC, 0.1, 10, 3.0, 13);
        CausalSimulator cd = new CausalSimulator(new LogootSplitOFactory());

        assertConsistency(cd, trace);
        //assertGoodViewLength(cd);
    }
}
