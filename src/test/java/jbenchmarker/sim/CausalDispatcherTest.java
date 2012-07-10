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

import crdt.simulator.Simulator;
import java.util.Iterator;
import crdt.simulator.Trace;
import crdt.simulator.CausalSimulator;
import crdt.CRDT;
import crdt.CRDTMessage;
import crdt.OperationBasedOneMessage;
import crdt.simulator.TraceOperation;
import java.util.Enumeration;
import jbenchmarker.core.ReplicaFactory;
import jbenchmarker.core.Document;
import jbenchmarker.core.MergeAlgorithm;
import java.util.ArrayList;
import jbenchmarker.core.SequenceMessage;
import java.util.List;
import jbenchmarker.core.*;
import org.junit.Test;
import static org.junit.Assert.*;
import static jbenchmarker.trace.TraceGeneratorTest.op;

/**
 *
 * @author urso
 */
public class CausalDispatcherTest {

    static public void assertConsistency(Simulator sim, Trace trace) throws Exception {
        sim.run(trace, false);
        Object referenceView = null;
	for (final CRDT replica : sim.getReplicas().values()) {
            final Object view = replica.lookup();
            if (referenceView == null)
		referenceView = view;
            else
		assertEquals(referenceView, view);
	}
	assertNotNull(referenceView);
    }
     static public void assertGoodViewLength(Simulator sim) throws Exception {
	for (final CRDT replica : sim.getReplicas().values()) {
            final Document view = ((MergeAlgorithm) replica).getDoc();
            assertEquals(view.view().length(), view.viewLength());
	}
    }
    
     static private class SequenceMock extends SequenceMessage {

        public SequenceMock(SequenceOperation o, int replica) {
            super(o, replica);
        }

        public SequenceMock(SequenceOperation originalOp) {
            super(originalOp);
        }
         
         @Override
        public boolean equals(Object obj) {
            return this.getOriginalOp().equals(obj);
        }
        @Override
        public SequenceMessage clone() {
            return this;
        }
         
     }
     
     
    // SequenceMessage mock
        static private class TraceMock extends TraceOperation {
            SequenceOperation opt;

        public TraceMock(TraceOperation top) {
            super(top.getReplica(),top.getVectorClock());
            this.opt=(SequenceOperation)top.getOperation();
        }
        

            
       

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof TraceOperation))
                return false;
            TraceOperation to=(TraceOperation)obj;
            return opt.equals(to.getOperation()) 
                    && this.getVectorClock().equals(to.getVectorClock()) 
                    && this.getReplica()== to.getReplica();
        }
        
        @Override
        public LocalOperation getOperation() {
            return opt;
        }

        @Override
        public String toString() {
            return "TraceMock{" + "opt=" + opt + super.toString() +'}';
        }
        
    }

    static public class RFMock extends ReplicaFactory {

        @Override
        public MergeAlgorithm create(int r) {
            return new MergeAlgorithm(new Document() {

                @Override
                public String view() {
                    return "";
                }

                @Override
                public void apply(Operation op) {
                }

                @Override
                public int viewLength() {
                    return 0;
                }
            }, r) {

                @Override
                protected void integrateRemote(SequenceMessage op) {
                    this.getDoc().apply(op);
                }

                @Override
                protected List<SequenceMessage> generateLocal(SequenceOperation opt) {
                    List<SequenceMessage> l = new ArrayList<SequenceMessage>();
                    SequenceMock op = new SequenceMock(opt);
//                this.getDoc().apply(op);
                    l.add(op);
                    return l;
                }

                @Override
                public CRDT<String> create() {
                    throw new UnsupportedOperationException("Not supported yet.");
                }
            };
        }
    }
    
    static class ListTrace implements Trace {
        private final List l;
        ListTrace(List l) {
            this.l = l;
        }

        @Override
        public Enumeration<TraceOperation> enumeration() {
            return new Enumeration<TraceOperation>() {
                Iterator<TraceOperation> it = l.iterator();
                @Override
                public boolean hasMoreElements() {
                    return it.hasNext();
                }

                @Override
                public TraceOperation nextElement() {
                    return it.next();
                }          
            };
        }
    }
    
    /**
     * Test of run method, of class CausalSimulator.
     */
    @Test
    public void testRun() throws Exception {
        System.out.println("run");
        List<TraceOperation> lop = new ArrayList<TraceOperation>();
        Trace trace = new ListTrace(lop);
        
        CausalSimulator cd = new CausalSimulator(new RFMock());
        
        TraceOperation op1 = op(2,0,1,0);
        lop.add(op1);        
        List<TraceOperation> o1 = new ArrayList<TraceOperation>();
        o1.add(new TraceMock(op1));  

        cd.run(trace, false);
        assertEquals(cd.getHistory().get(2), lop);
        assertEquals(o1,cd.getHistory().get(2));        
        cd.reset();
        TraceOperation op2 = op(1,1,0,0);
        lop.add(op2);  
        List<TraceOperation> o2 = new ArrayList<TraceOperation>();
        o2.add(new TraceMock(op2));
        
        cd.run(trace, false);
        assertEquals(cd.getHistory().get(2), lop.subList(0, 1));
        assertEquals(cd.getHistory().get(1), lop.subList(1, 2));
        assertEquals(o1, cd.getHistory().get(2));        
        assertEquals(o2, cd.getHistory().get(1));

        cd.reset();
        TraceOperation op3 = op(1,2,1,0);
        lop.add(op3);
        o2.add(new TraceMock(op3));
        
        cd.run(trace, false);
        assertEquals(cd.getHistory().get(2), lop.subList(0, 1));
        assertEquals(cd.getHistory().get(1), lop.subList(1, 3));
        assertEquals(o1, cd.getHistory().get(2));        
        assertEquals(o2, cd.getHistory().get(1));
    
        cd.reset();
        TraceOperation op4 = op(3,1,1,1);
        lop.add(op4);
        List<TraceOperation> o3 = new ArrayList<TraceOperation>();
        o3.add(new TraceMock(op4));
        
        cd.run(trace, false);
        assertEquals(cd.getHistory().get(2), lop.subList(0, 1));
        assertEquals(cd.getHistory().get(1), lop.subList(1, 3));
        assertEquals(cd.getHistory().get(3), lop.subList(3, 4));        
        assertEquals(o1, cd.getHistory().get(2));        
        assertEquals(o2, cd.getHistory().get(1));
        assertTrue(o3.equals(cd.getHistory().get(3)) ); // ||  o3b.equals(cd.getHistory().get(3)));
        
   }
}
