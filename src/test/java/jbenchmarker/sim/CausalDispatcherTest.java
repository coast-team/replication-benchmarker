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

import crdt.CRDT;
import jbenchmarker.core.ReplicaFactory;
import jbenchmarker.trace.IncorrectTrace;
import jbenchmarker.core.Document;
import jbenchmarker.core.MergeAlgorithm;
import java.util.ArrayList;
import jbenchmarker.core.SequenceMessage;
import java.util.List;
import jbenchmarker.trace.TraceOperation;
import org.junit.Test;
import static org.junit.Assert.*;
import static jbenchmarker.trace.TraceGeneratorTest.op;

/**
 *
 * @author urso
 */
public class CausalDispatcherTest {

    // SequenceMessage mock
    static private class OpMock extends SequenceMessage {
        OpMock(TraceOperation opt) {
            super(opt);
        }
            
        @Override
        public SequenceMessage copy() {
            return this;
        }
    }

    static public class RFMock extends ReplicaFactory {

        public MergeAlgorithm create(int r) {
            return new MergeAlgorithm(new Document() {

                public String view() {
                    return null;
                }

                public void apply(SequenceMessage op) {
                }
            }, r) {

                protected void integrateLocal(SequenceMessage op) {
                    this.getDoc().apply(op);
                }

                protected List<SequenceMessage> generateLocal(TraceOperation opt) {
                    List<SequenceMessage> l = new ArrayList<SequenceMessage>();
                    OpMock op = new OpMock(opt);
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
    
    /**
     * Test of run method, of class OldCausalDispatcher.
     */
    @Test
    public void testRun() throws IncorrectTrace, Exception {
        System.out.println("run");
        List<TraceOperation> trace = new ArrayList<TraceOperation>();
        
        OldCausalDispatcher cd = new OldCausalDispatcher(new RFMock());
        
        TraceOperation op1 = op(2,0,1,0);
        trace.add(op1);        
        List<SequenceMessage> o1 = new ArrayList<SequenceMessage>();
        o1.add(new OpMock(op1));  

        cd.run(trace.iterator());
        assertEquals(trace, cd.getReplicas().get(2).getLocalHistory());        
        assertEquals(o1, cd.getReplicas().get(2).getHistory());        
        
        cd.reset();
        TraceOperation op2 = op(1,1,0,0);
        trace.add(op2);  
        List<SequenceMessage> o2 = new ArrayList<SequenceMessage>();
        o2.add(new OpMock(op2)); o2.add(new OpMock(op1));
        o1.add(new OpMock(op2));
        
        cd.run(trace.iterator());
        assertEquals(trace.subList(0, 1), cd.getReplicas().get(2).getLocalHistory());
        assertEquals(trace.subList(1, 2), cd.getReplicas().get(1).getLocalHistory());
        assertEquals(o1, cd.getReplicas().get(2).getHistory());        
        assertEquals(o2, cd.getReplicas().get(1).getHistory());

        cd.reset();
        TraceOperation op3 = op(1,2,1,0);
        trace.add(op3);
        o1.add(new OpMock(op3));
        o2.add(new OpMock(op3));
        
        cd.run(trace.iterator());
        assertEquals(trace.subList(0, 1), cd.getReplicas().get(2).getLocalHistory());
        assertEquals(trace.subList(1, 3), cd.getReplicas().get(1).getLocalHistory());
        assertEquals(o1, cd.getReplicas().get(2).getHistory());        
        assertEquals(o2, cd.getReplicas().get(1).getHistory());
    
        cd.reset();
        TraceOperation op4 = op(3,1,1,1);
        trace.add(op4);
        List<SequenceMessage> o3 = new ArrayList<SequenceMessage>();
        o3.add(new OpMock(op1)); o3.add(new OpMock(op2)); o3.add(new OpMock(op4)); o3.add(new OpMock(op3));
        List<SequenceMessage> o3b = new ArrayList<SequenceMessage>();
        o3b.add(new OpMock(op2)); o3b.add(new OpMock(op1)); o3b.add(new OpMock(op4)); o3b.add(new OpMock(op3));
        o1.add(new OpMock(op4));
        o2.add(new OpMock(op4));
        
        cd.run(trace.iterator());
        assertEquals(trace.subList(0, 1), cd.getReplicas().get(2).getLocalHistory());
        assertEquals(trace.subList(1, 3), cd.getReplicas().get(1).getLocalHistory());
        assertEquals(trace.subList(3, 4), cd.getReplicas().get(3).getLocalHistory());        
        assertEquals(o1, cd.getReplicas().get(2).getHistory());        
        assertEquals(o2, cd.getReplicas().get(1).getHistory());
        assertTrue(o3.equals(cd.getReplicas().get(3).getHistory()) || 
                o3b.equals(cd.getReplicas().get(3).getHistory()));
        
   }
}