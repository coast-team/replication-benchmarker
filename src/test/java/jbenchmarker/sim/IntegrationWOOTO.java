
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

import org.junit.Ignore;
import jbenchmarker.TraceSimul2XML;
import crdt.CRDT;
import crdt.simulator.Trace;
import crdt.simulator.random.RandomTrace;
import crdt.simulator.random.StandardSeqOpProfile;
import crdt.simulator.CausalSimulator;
import static jbenchmarker.woot.WootFactories.WootOFactory;
import jbenchmarker.trace.TraceGenerator;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author urso
 */
public class IntegrationWOOTO {

    //@Ignore
    @Test
    public void testWootOExempleRun() throws Exception {
        System.out.println("Integration test with wooto");
        Trace trace = TraceGenerator.traceFromXML("../../traces/xml/exemple.xml", 1);
        CausalSimulator cd = new CausalSimulator(new WootOFactory());

        cd.run(trace, false);
        String r = "Salut MonsierBonjou MehdirFin";

        assertEquals(r, cd.getReplicas().get(0).lookup());
        assertEquals(r, cd.getReplicas().get(2).lookup());
        assertEquals(r, cd.getReplicas().get(4).lookup());
    }
    
    //@Ignore
    @Test
    public void testWootOG1Run() throws Exception {
        Trace trace = TraceGenerator.traceFromXML("../../traces/xml/G1.xml", 1);
        CausalSimulator cd = new CausalSimulator(new WootOFactory());

        cd.run(trace, true);
        String r = (String) cd.getReplicas().get(0).lookup();
        for (CRDT m : cd.getReplicas().values()) {
            assertEquals(r, m.lookup());
        }
    }
    //@Ignore
    @Test
    public void testWootOG2Run() throws Exception {
        Trace trace = TraceGenerator.traceFromXML("../../traces/xml/G2.xml", 1);
        CausalSimulator cd = new CausalSimulator(new WootOFactory());

        cd.run(trace, true);
        String r = (String) cd.getReplicas().get(0).lookup();
        for (CRDT m : cd.getReplicas().values()) {
            assertEquals(r, m.lookup());
        }
    }
    //@Ignore
    @Test
    public void testWootOG3Run() throws Exception {
        Trace trace = TraceGenerator.traceFromXML("../../traces/xml/G3.xml", 1);
        CausalSimulator cd = new CausalSimulator(new WootOFactory());

        cd.run(trace, true);
        String r = (String) cd.getReplicas().get(0).lookup();
        for (CRDT m : cd.getReplicas().values()) {
            assertEquals(r, m.lookup());
        }
    }
    //@Ignore
    @Test
    public void testWootOSerieRun() throws Exception {
        Trace trace = TraceGenerator.traceFromXML("../../traces/xml/Serie.xml", 1);
        CausalSimulator cd = new CausalSimulator(new WootOFactory());

        cd.run(trace, false);
        String r = (String) cd.getReplicas().get(0).lookup();
        for (CRDT m : cd.getReplicas().values()) {
            assertEquals(r, m.lookup());
        }
    }
    //@Ignore
    @Test
    public void testWootORandom() throws Exception {
        Trace trace = new RandomTrace(2000, RandomTrace.FLAT, new StandardSeqOpProfile(0.8, 0.1, 40, 5.0), 0.1, 10, 3.0, 13);
        CausalSimulator cd = new CausalSimulator(new WootOFactory());

        cd.run(trace, false);
        String r = (String) cd.getReplicas().get(0).lookup();
        for (CRDT m : cd.getReplicas().values()) {
            assertEquals(r, m.lookup());
        }     
    }
    @Ignore
    @Test
    public void testWootOSimulXML() throws Exception {
        Trace trace = new RandomTrace(2000, RandomTrace.FLAT, new StandardSeqOpProfile(0.8, 0.1, 40, 5.0), 1, 10, 3.0, 5);
        CausalSimulator cdSim = new CausalSimulator(new WootOFactory());
        cdSim.setLogging("trace.log");
        cdSim.run(trace, false);
         
        TraceSimul2XML mn = new TraceSimul2XML();
        String[] args = new String[]{"trace.log", "trace.xml"};
        mn.main(args);
        
        Trace real = TraceGenerator.traceFromXML("trace.xml", 1);
        CausalSimulator cdReal = new CausalSimulator(new WootOFactory());
        cdReal.run(real, false);
        
        //compare all replica
        for (CRDT crdtSim : cdSim.getReplicas().values()) {
            for (CRDT crdtReal : cdReal.getReplicas().values()) {
                assertEquals(crdtSim.lookup(), crdtReal.lookup());
            }
        }
    }
    
}
