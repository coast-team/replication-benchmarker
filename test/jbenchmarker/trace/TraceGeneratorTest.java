/**
 *   This file is part of ReplicationBenchmark.
 *
 *   ReplicationBenchmark is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   ReplicationBenchmark is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with ReplicationBenchmark.  If not, see <http://www.gnu.org/licenses/>.
 *
 **/

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jbenchmarker.trace;

import java.util.HashMap;
import java.util.Iterator;
import jbenchmarker.core.VectorClock;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author urso
 */
public class TraceGeneratorTest {


    private static List<TraceOperation> it2list(Iterator<TraceOperation> t) {
        List<TraceOperation> l = new ArrayList<TraceOperation>();
        while (t.hasNext()) l.add(t.next());
        return l;
    }
    
    /**
     * Helper to construct a 3 entries VC
     */
    public static VectorClock vc(int a, int b, int c) {
        VectorClock v = new VectorClock();
        v.put(1,a); v.put(2,b); v.put(3,c);
        return v;
    } 
    
    /**
     * Helper to construct an op with a 3 entries VC
     */
    public static TraceOperation op(int r, int a, int b, int c) {
        return TraceOperation.insert(r, 0, null, vc(a, b, c));
    } 

    /**
     * Tests of causalHistoryPerReplica method, of class TraceGenerator.
     * tests only exceptions.
     */
    @Test(expected=IncorrectTrace.class)
    public void testOrderMissing() throws Exception {
        System.out.println("causalHistoryPerReplica");
        Map<Integer, VectorClock> vcs = new HashMap<Integer, VectorClock>();
        vcs.put(1, vc(3, 1, 0)); vcs.put(2, vc(0, 1, 0)); vcs.put(3, vc(0, 0, 0));
        TraceGenerator.causalCheck(op(2,0,3,0), vcs);
        fail("Missing operation not detected.");
    }
    
    @Test(expected=IncorrectTrace.class)
    public void testOrderCausalMissing() throws Exception {
        Map<Integer, VectorClock> vcs = new HashMap<Integer, VectorClock>();
        vcs.put(1, vc(3, 1, 0)); vcs.put(2, vc(0, 1, 0)); vcs.put(3, vc(0, 0, 0));
        TraceGenerator.causalCheck(op(2,4,2,0), vcs);
        fail("Missing causal operation not detected.");
    }
    
    @Test
    public void testOrderOK() throws Exception {
        Map<Integer, VectorClock> vcs = new HashMap<Integer, VectorClock>();
        vcs.put(1, vc(3, 1, 0)); vcs.put(2, vc(0, 1, 0));  vcs.put(3, vc(0, 0, 0));
        TraceGenerator.causalCheck(op(2,2,2,0), vcs);
    }
    
    @Test
    public void testTraceFromXML() throws Exception {
        System.out.println("traceFromXML");
        List<TraceOperation> trace = it2list(TraceGenerator.traceFromXML("../../traces/xml/exemple.xml", 1));
        
        assertEquals(5, trace.size());
        assertEquals(TraceOperation.OpType.ins, trace.get(0).getType());
        assertEquals(14, trace.get(1).getPosition());
        assertEquals(" Mehdi", trace.get(2).getContent());
        assertEquals(2, (long)trace.get(2).getVC().get(2));
        assertEquals(TraceOperation.OpType.del, trace.get(3).getType());
        assertEquals(4, trace.get(3).getReplica());
        assertEquals(3, trace.get(3).getOffset());        
        assertEquals(1, (long)trace.get(3).getVC().get(4));
    }
}