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

import crdt.simulator.CausalSimulator;
import crdt.simulator.Trace;
import crdt.simulator.random.RandomTrace;
import crdt.simulator.random.StandardDiffProfile;
import crdt.simulator.random.StandardSeqOpProfile;
import jbenchmarker.factories.LogootFactory;
import static jbenchmarker.sim.CausalDispatcherTest.assertConsistency;
import static jbenchmarker.sim.CausalDispatcherTest.assertGoodViewLength;
import jbenchmarker.trace.TraceGenerator;
import static org.junit.Assert.assertEquals;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author urso
 */
public class IntegrationLogoot {
    @Ignore
    @Test
    public void testLogootExempleRun() throws Exception {
        System.out.println("Integration test with logoot");
        Trace trace = TraceGenerator.traceFromXML(IntegrationLogoot.class.getResource("exemple.xml").getPath(), 1);
        CausalSimulator cd = new CausalSimulator(new LogootFactory<Character>());

        cd.run(trace);
        String r = "Salut Monsieurjour MehdiFin";
        assertEquals(r, cd.getReplicas().get(0).lookup());
        assertEquals(r, cd.getReplicas().get(2).lookup());
        assertEquals(r, cd.getReplicas().get(4).lookup());
    }
    
    @Ignore
    @Test
    public void testLogootG1Run() throws Exception {
        Trace trace = TraceGenerator.traceFromXML(IntegrationLogoot.class.getResource("G1.xml").getPath(), 1);
        CausalSimulator cd = new CausalSimulator(new LogootFactory<Character>());

        assertConsistency(cd, trace);
    }
    
    @Ignore
    @Test
    public void testLogootG2Run() throws Exception {
        Trace trace = TraceGenerator.traceFromXML(IntegrationLogoot.class.getResource("G2.xml").getPath(), 1);
        CausalSimulator cd = new CausalSimulator(new LogootFactory<Character>());

        assertConsistency(cd, trace);
    }
    
    @Ignore
    @Test
    public void testLogootG3Run() throws Exception {
        Trace trace = TraceGenerator.traceFromXML(IntegrationLogoot.class.getResource("G3.xml").getPath(), 1);
        CausalSimulator cd = new CausalSimulator(new LogootFactory<Character>());

        assertConsistency(cd, trace);
    }
    
    @Ignore
    @Test
    public void testLogootSerieRun() throws Exception {
        Trace trace = TraceGenerator.traceFromXML(IntegrationLogoot.class.getResource("Serie.xml").getPath(), 1);
        CausalSimulator cd = new CausalSimulator(new LogootFactory<Character>());

        assertConsistency(cd, trace);
    }
    
    
    //@Ignore
    @Test
    public void testLogootProfile() throws Exception {
        for (int i = 0; i < 20; ++i) {
            Trace trace = new RandomTrace(4200, RandomTrace.FLAT, new StandardSeqOpProfile(0.8, 0.1, 40, 5.0), 0.1, 10, 3.0, 13);
            CausalSimulator cd = new CausalSimulator(new LogootFactory<Character>());

            cd.run(trace);
        }    
    }
    
    //@Ignore
    @Test
    public void testLogootRandom() throws Exception {
        Trace trace = new RandomTrace(4200, RandomTrace.FLAT, new StandardSeqOpProfile(0.8, 0.1, 40, 5.0), 0.1, 10, 3.0, 13);
        CausalSimulator cd = new CausalSimulator(new LogootFactory<Character>());

        assertConsistency(cd, trace);  
        assertGoodViewLength(cd);
    }
    
    @Test
    public void testLogootRandomDiff() throws Exception {
        Trace trace = new RandomTrace(420, RandomTrace.FLAT, StandardDiffProfile.BASIC, 0.1, 10, 3.0, 13);
        CausalSimulator cd = new CausalSimulator(new LogootFactory<String>());

        assertConsistency(cd, trace);  
        //assertGoodViewLength(cd);
    }
}
