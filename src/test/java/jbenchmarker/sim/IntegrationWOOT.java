/**
 * Replication Benchmarker
 * https://github.com/score-team/replication-benchmarker/
 * Copyright (C) 2013 LORIA / Inria / SCORE Team
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

import crdt.CRDT;
import crdt.simulator.Trace;
import crdt.simulator.CausalSimulator;
import static jbenchmarker.factories.WootFactories.WootOFactory;
import static jbenchmarker.factories.WootFactories.WootFactory;
import org.junit.Ignore;
import jbenchmarker.trace.TraceGenerator;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author urso
 */
public class IntegrationWOOT {

    /**
     * Test of run method, WOOT class CausalSimulator; exemple.xml.
     */
    @Test
    public void testWootExempleRun() throws Exception {
        System.out.println("Integration test with causal + WOOT");
        Trace trace = TraceGenerator.traceFromXML(IntegrationWOOT.class.getResource("exemple.xml").getPath(), 1, 100);
        CausalSimulator cd = new CausalSimulator(new WootFactory());

        cd.run(trace);
        String r = "Salut MonsierBonjou MehdirFin";
        System.out.println(cd.getReplicas().get(0).lookup());
        assertEquals(r, cd.getReplicas().get(0).lookup());
        assertEquals(r, cd.getReplicas().get(2).lookup());
        assertEquals(r, cd.getReplicas().get(4).lookup());
    }
    
    /**
     * Test of run method on WOOT , of class CausalSimulator; whole traces
     */
    @Ignore // Too long -- Passes on revision 96 -- 1800s
    @Test
    public void testWootG1Run() throws Exception {
        Trace trace = TraceGenerator.traceFromXML(IntegrationWOOT.class.getResource("G1.xml").getPath(), 1, 2000);         
        CausalSimulator cd = new CausalSimulator(new WootFactory());

        cd.run(trace);
        String r = (String) cd.getReplicas().get(0).lookup();
        for (CRDT m : cd.getReplicas().values()) {
            assertEquals(r, m.lookup());
        }
    }
    
    @Ignore // Too long -- Passess on revision 98 -- 9500s !
    @Test
    public void testWootG2Run() throws Exception {
        Trace trace = TraceGenerator.traceFromXML(IntegrationWOOT.class.getResource("G2.xml").getPath(), 1);
        CausalSimulator cd = new CausalSimulator(new WootFactory());

        cd.run(trace);
        String r = (String) cd.getReplicas().get(0).lookup();
        for (CRDT m : cd.getReplicas().values()) {
            assertEquals(r, m.lookup());
        }
    }

    @Ignore // Too long -- G3 pass on revision 86 -- 100s
    @Test
    public void testWootG3Run() throws Exception {
        Trace trace = TraceGenerator.traceFromXML(IntegrationWOOT.class.getResource("G3.xml").getPath(), 1);         
        CausalSimulator cd = new CausalSimulator(new WootFactory());
        
        cd.run(trace);
        String r = (String) cd.getReplicas().get(0).lookup();
        for (CRDT m : cd.getReplicas().values())
            assertEquals(r, m.lookup());
    }

    /**
     * Test of run method on WOOTO, CausalSimulator; whole traces
     */
     @Ignore // (Only) 11 s 
    @Test
    public void testWootOG1Run() throws Exception {        
        System.out.println("Integration test with causal + WOOTO");
        Trace trace = TraceGenerator.traceFromXML(IntegrationWOOT.class.getResource("G1.xml").getPath(), 1);         
        CausalSimulator cd = new CausalSimulator(new WootOFactory());

        cd.run(trace);
        String r = (String) cd.getReplicas().get(0).lookup();
        for (CRDT m : cd.getReplicas().values()) {
            assertEquals(r, m.lookup());
        }
    }
        
    /**
     * Test of run method, of class CausalSimulator; WOOTO tail lines of G1.xml 
     */
    @Ignore
    @Test
    public void testWootOG1RunSubset() throws Exception {
        Trace trace = TraceGenerator.traceFromXML(IntegrationWOOT.class.getResource("G1.xml").getPath(), 1, 2000);
        CausalSimulator cd = new CausalSimulator(new WootOFactory());

        cd.run(trace);
        String r = (String) cd.getReplicas().get(0).lookup();
        for (CRDT m : cd.getReplicas().values()) {
            assertEquals(r, m.lookup());
        }
    }


}
