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
package jbenchmarker.sim;

import jbenchmarker.core.MergeAlgorithm;
import jbenchmarker.TraceSimul2XML;
import org.junit.Ignore;
import crdt.CRDT;
import crdt.simulator.Trace;
import crdt.simulator.random.RandomTrace;
import crdt.simulator.random.StandardSeqOpProfile;
import crdt.simulator.CausalSimulator;
import crdt.simulator.random.StandardDiffProfile;
import jbenchmarker.factories.RGAFactory;
import jbenchmarker.factories.WootFactories;
import jbenchmarker.trace.TraceGenerator;
import org.junit.Test;
import static org.junit.Assert.*;
import static jbenchmarker.sim.CausalDispatcherTest.*;

/**
 *
 * @author Roh
 */
public class IntegrationRGA {

    @Test
    public void testRGAExempleRun() throws Exception {
        System.out.println("Integration test with RGA");
        Trace trace = TraceGenerator.traceFromXML("../../traces/xml/exemple.xml", 1);

        CausalSimulator cd = new CausalSimulator(new RGAFactory());

        cd.run(trace);
        String r = "Salut Monsieurjour MehdiFin";
        assertEquals(r, cd.getReplicas().get(0).lookup());
        assertEquals(r, cd.getReplicas().get(2).lookup());
        assertEquals(r, cd.getReplicas().get(4).lookup());
    }

////     @Ignore
//    @Test
//    public void testRGAG1Run() throws Exception {
//        Trace trace = TraceGenerator.traceFromXML("../../traces/xml/G1.xml", 1);
//        CausalSimulator cd = new CausalSimulator(new RGAFactory());
//
//        cd.run(trace, true);
//        assertConsistency(cd, trace);
//    }
//    
//    @Test
//    public void testRGAG2Run() throws Exception {
//        Trace trace = TraceGenerator.traceFromXML("../../traces/xml/G2.xml", 1,16);
//        CausalSimulator cd = new CausalSimulator(new RGAFactory());
//
//        cd.run(trace, false);
//        assertConsistency(cd, trace);
//    }
//    
//    @Test
//    public void testRGAG3Run() throws Exception {
//        Trace trace = TraceGenerator.traceFromXML("../../traces/xml/G3.xml", 1);
//        CausalSimulator cd = new CausalSimulator(new RGAFactory());
//
//        cd.run(trace, false);
//        assertConsistency(cd, trace);
//    }
//    

    
//    @Ignore
    @Test
    public void testRGARandom() throws Exception {
        Trace trace = new RandomTrace(1000, RandomTrace.FLAT, new StandardSeqOpProfile(0.8, 0.1, 40, 5.0), 0.1, 4, 3.0, 3);
        CausalSimulator cd = new CausalSimulator(new RGAFactory());
        
        assertConsistency(cd, trace);
        assertGoodViewLength(cd);
    }
//    @Ignore
    @Test
    public void testRGASimulXML() throws Exception {
        Trace trace = new RandomTrace(2000, RandomTrace.FLAT, new StandardSeqOpProfile(0.8, 0.1, 40, 5.0), 0.1, 10, 3.0, 5);
        CausalSimulator cdSim = new CausalSimulator(new RGAFactory());
        cdSim.setLogging("trace.log");
        cdSim.run(trace);

        TraceSimul2XML mn = new TraceSimul2XML();
        String[] args = new String[]{"trace.log", "trace.xml"};
        mn.main(args);

        Trace real = TraceGenerator.traceFromXML("trace.xml", 1);
        CausalSimulator cdReal = new CausalSimulator(new RGAFactory());
        cdReal.run(real);

        //compare all replica
        for (CRDT crdtSim : cdSim.getReplicas().values()) {
            for (CRDT crdtReal : cdReal.getReplicas().values()) {
                assertEquals(crdtSim.lookup(), crdtReal.lookup());
            }
        }
    }
    
    @Test
    public void testRGARandomDiff() throws Exception {
        Trace trace = new RandomTrace(2000, RandomTrace.FLAT, StandardDiffProfile.BASIC, 0.1, 10, 3.0, 13);
        CausalSimulator cd = new CausalSimulator(new RGAFactory());

        assertConsistency(cd, trace);  
        //assertGoodViewLength(cd);
    }
}
