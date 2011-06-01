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

package jbenchmarker.ot;

import java.util.ArrayList;
import jbenchmarker.trace.TraceOperation.OpType;
import java.util.Map;
import java.util.Iterator;
import jbenchmarker.core.VectorClock;
import java.util.List;
import jbenchmarker.core.Operation;
import jbenchmarker.trace.IncorrectTrace;
import jbenchmarker.trace.TraceOperation;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author oster
 */
public class SOCT2Test {

    @Test
    public void testGenerateLocalInsertCharByChar() throws IncorrectTrace {
        int siteId = 0;
        SOCT2MergeAlgorithm merger = new SOCT2MergeAlgorithm(new TTFDocument(), siteId);

        List<Operation> ops = merger.generateLocal(insert(siteId, 0, "b"));
        assertEquals(1, ops.size());
        TTFOperation opg = (TTFOperation) ops.get(0);
        assertEquals(OpType.ins, opg.getType());
        assertEquals('b', opg.getChar());
        assertEquals(0, opg.getPosition());
        assertEquals("[]", vcToString(opg.getClock()));
        assertEquals("b", merger.getDoc().view());


        ops = merger.generateLocal(insert(siteId, 0, "a"));
        assertEquals(1, ops.size());
        opg = (TTFOperation) ops.get(0);
        assertEquals('a', opg.getChar());
        assertEquals(0, opg.getPosition());
        assertEquals("[<0,1>]", vcToString(opg.getClock()));
        assertEquals("ab", merger.getDoc().view());

        ops = merger.generateLocal(insert(siteId, 2, "c"));
        assertEquals(1, ops.size());
        opg = (TTFOperation) ops.get(0);
        assertEquals('c', opg.getChar());
        assertEquals("[<0,2>]", vcToString(opg.getClock()));
        assertEquals(2, opg.getPosition());
        assertEquals("abc", merger.getDoc().view());
    }

    @Test
    public void testGenerateLocalInsertString() throws IncorrectTrace {
        int siteId = 0;
        SOCT2MergeAlgorithm merger = new SOCT2MergeAlgorithm(new TTFDocument(), siteId);

        List<Operation> ops = merger.generateLocal(insert(siteId, 0, "abc"));
        assertEquals(3, ops.size());

        TTFOperation opg = (TTFOperation) ops.get(0);
        assertEquals(OpType.ins, opg.getType());
        assertEquals('a', opg.getChar());
        assertEquals(0, opg.getPosition());
        assertEquals("[]", vcToString(opg.getClock()));

        opg = (TTFOperation) ops.get(1);
        assertEquals('b', opg.getChar());
        assertEquals(1, opg.getPosition());
        assertEquals("[<0,1>]", vcToString(opg.getClock()));

        opg = (TTFOperation) ops.get(2);
        assertEquals('c', opg.getChar());
        assertEquals("[<0,2>]", vcToString(opg.getClock()));
        assertEquals(2, opg.getPosition());

        assertEquals("abc", merger.getDoc().view());
    }

    @Test
    public void testGenerateLocalDeleteCharByChar() throws IncorrectTrace {
        int siteId = 0;
        SOCT2MergeAlgorithm merger = new SOCT2MergeAlgorithm(new TTFDocument(), siteId);
        merger.generateLocal(insert(siteId, 0, "abcd"));

        // remove 'a'
        List<Operation> ops = merger.generateLocal(delete(siteId, 0, 1));
        assertEquals(1, ops.size());
        TTFOperation opg = (TTFOperation) ops.get(0);
        assertEquals(OpType.del, opg.getType());
        assertEquals(0, opg.getPosition());
        assertEquals("[<0,4>]", vcToString(opg.getClock()));
        assertEquals("bcd", merger.getDoc().view());

        // remove 'd'
        ops = merger.generateLocal(delete(siteId, 2, 1));
        assertEquals(1, ops.size());
        opg = (TTFOperation) ops.get(0);
        assertEquals(OpType.del, opg.getType());
        assertEquals(3, opg.getPosition());
        assertEquals("[<0,5>]", vcToString(opg.getClock()));
        assertEquals("bc", merger.getDoc().view());

        // remove 'c'
        ops = merger.generateLocal(delete(siteId, 1, 1));
        assertEquals(1, ops.size());
        opg = (TTFOperation) ops.get(0);
        assertEquals(OpType.del, opg.getType());
        assertEquals(2, opg.getPosition());
        assertEquals("[<0,6>]", vcToString(opg.getClock()));
        assertEquals("b", merger.getDoc().view());

        // remove 'b'
        ops = merger.generateLocal(delete(siteId, 0, 1));
        assertEquals(1, ops.size());
        opg = (TTFOperation) ops.get(0);
        assertEquals(OpType.del, opg.getType());
        assertEquals(1, opg.getPosition());
        assertEquals("[<0,7>]", vcToString(opg.getClock()));
        assertEquals("", merger.getDoc().view());
    }

    @Test
    public void testGenerateLocalDeleteString() throws IncorrectTrace {
        int siteId = 0;
        SOCT2MergeAlgorithm merger = new SOCT2MergeAlgorithm(new TTFDocument(), siteId);
        merger.generateLocal(insert(siteId, 0, "abcd"));

        // remove "abcd"
        List<Operation> ops = merger.generateLocal(delete(siteId, 0, 4));

        assertEquals(4, ops.size());
        TTFOperation opg = (TTFOperation) ops.get(0);
        assertEquals(OpType.del, opg.getType());
        assertEquals(0, opg.getPosition());
        assertEquals("[<0,4>]", vcToString(opg.getClock()));

        opg = (TTFOperation) ops.get(1);
        assertEquals(OpType.del, opg.getType());
        assertEquals(1, opg.getPosition());
        assertEquals("[<0,5>]", vcToString(opg.getClock()));

        opg = (TTFOperation) ops.get(2);
        assertEquals(OpType.del, opg.getType());
        assertEquals(2, opg.getPosition());
        assertEquals("[<0,6>]", vcToString(opg.getClock()));

        opg = (TTFOperation) ops.get(3);
        assertEquals(OpType.del, opg.getType());
        assertEquals(3, opg.getPosition());
        assertEquals("[<0,7>]", vcToString(opg.getClock()));

        assertEquals("", merger.getDoc().view());
    }

    @Test
    public void testVectorClockEvolution() throws IncorrectTrace {
        int siteId = 0;
        SOCT2MergeAlgorithm merger = new SOCT2MergeAlgorithm(new TTFDocument(), siteId);
        assertEquals(vc(0), merger.getClock());

        TTFOperation op1 = (TTFOperation) merger.generateLocal(insert(siteId, 0, "a")).get(0);
        assertEquals(vc(1), merger.getClock());

        TTFOperation op2 = (TTFOperation) merger.generateLocal(insert(siteId, 1, "b")).get(0);
        assertEquals(vc(2), merger.getClock());

        assertEquals(vc(0), op1.getClock());
        assertEquals(vc(1), op2.getClock());
    }

    @Test
    public void testTP2PuzzleAtSite0() throws IncorrectTrace {
        SOCT2MergeAlgorithm site0 = new SOCT2MergeAlgorithm(new TTFDocument(), 0);
        site0.generateLocal(insert(0, 0, "abc"));

        //site0.generateLocal(insert(0, 1, "x"));
        TTFOperation op1 = TTFOperation.from(insert(0, 1, "x", vc(3, 0, 0)));
        TTFOperation op2 = TTFOperation.from(insert(1, 2, "y", vc(3, 0, 0)));
        TTFOperation op3 = TTFOperation.from(delete(2, 1, 1, vc(3, 0, 0)));

        site0.integrate(op1);
        assertEquals("axbc", site0.getDoc().view());
        site0.integrate(op2);
        assertEquals("axbyc", site0.getDoc().view());
        site0.integrate(op3);
        assertEquals("axyc", site0.getDoc().view());
    }

    @Test
    public void testTP2PuzzleAtSite0bis() throws IncorrectTrace {
        SOCT2MergeAlgorithm site0 = new SOCT2MergeAlgorithm(new TTFDocument(), 0);
        site0.generateLocal(insert(0, 0, "abc"));

        TTFOperation op1 = TTFOperation.from(insert(0, 1, "x", vc(3, 0, 0)));
        TTFOperation op2 = TTFOperation.from(insert(1, 2, "y", vc(3, 0, 0)));
        TTFOperation op3 = TTFOperation.from(delete(2, 1, 1, vc(3, 0, 0)));

        site0.integrate(op1);
        assertEquals("axbc", site0.getDoc().view());
        site0.integrate(op3);
        assertEquals("axc", site0.getDoc().view());
        site0.integrate(op2);
        assertEquals("axyc", site0.getDoc().view());
    }

    @Test
    public void testTP2PuzzleAtSite1() throws IncorrectTrace {
        SOCT2MergeAlgorithm site0 = new SOCT2MergeAlgorithm(new TTFDocument(), 0);
        List<Operation> ops = site0.generateLocal(insert(0, 0, "abc"));

        TTFOperation op1 = TTFOperation.from(insert(0, 1, "x", vc(3, 0, 0)));
        TTFOperation op2 = TTFOperation.from(insert(1, 2, "y", vc(3, 0, 0)));
        TTFOperation op3 = TTFOperation.from(delete(2, 1, 1, vc(3, 0, 0)));

        SOCT2MergeAlgorithm site1 = new SOCT2MergeAlgorithm(new TTFDocument(), 1);
        for (Operation op : ops) {
            site1.integrate(op);
        }
        site1.integrate(op2);
        assertEquals("abyc", site1.getDoc().view());
        site1.integrate(op1);
        assertEquals("axbyc", site1.getDoc().view());
        site1.integrate(op3);
        assertEquals("axyc", site1.getDoc().view());
    }

    @Test
    public void testTP2PuzzleAtSite1bis() throws IncorrectTrace {
        SOCT2MergeAlgorithm site0 = new SOCT2MergeAlgorithm(new TTFDocument(), 0);
        List<Operation> ops = site0.generateLocal(insert(0, 0, "abc"));

        TTFOperation op1 = TTFOperation.from(insert(0, 1, "x", vc(3, 0, 0)));
        TTFOperation op2 = TTFOperation.from(insert(1, 2, "y", vc(3, 0, 0)));
        TTFOperation op3 = TTFOperation.from(delete(2, 1, 1, vc(3, 0, 0)));

        SOCT2MergeAlgorithm site1 = new SOCT2MergeAlgorithm(new TTFDocument(), 1);
        for (Operation op : ops) {
            site1.integrate(op);
        }
        site1.integrate(op2);
        assertEquals("abyc", site1.getDoc().view());
        site1.integrate(op3);
        assertEquals("ayc", site1.getDoc().view());
        site1.integrate(op1);
        assertEquals("axyc", site1.getDoc().view());
    }

    @Test
    public void testTP2PuzzleAtSite2() throws IncorrectTrace {
        SOCT2MergeAlgorithm site0 = new SOCT2MergeAlgorithm(new TTFDocument(), 0);
        List<Operation> ops = site0.generateLocal(insert(0, 0, "abc"));

        TTFOperation op1 = TTFOperation.from(insert(0, 1, "x", vc(3, 0, 0)));
        TTFOperation op2 = TTFOperation.from(insert(1, 2, "y", vc(3, 0, 0)));
        TTFOperation op3 = TTFOperation.from(delete(2, 1, 1, vc(3, 0, 0)));

        SOCT2MergeAlgorithm site2 = new SOCT2MergeAlgorithm(new TTFDocument(), 2);
        for (Operation op : ops) {
            site2.integrate(op);
        }
        site2.integrate(op3);
        assertEquals("ac", site2.getDoc().view());
        site2.integrate(op1);
        assertEquals("axc", site2.getDoc().view());
        site2.integrate(op2);
        assertEquals("axyc", site2.getDoc().view());
    }

    @Test
    public void testTP2PuzzleAtSite2bis() throws IncorrectTrace {
        SOCT2MergeAlgorithm site0 = new SOCT2MergeAlgorithm(new TTFDocument(), 0);
        List<Operation> ops = site0.generateLocal(insert(0, 0, "abc"));

        TTFOperation op1 = TTFOperation.from(insert(0, 1, "x", vc(3, 0, 0)));
        TTFOperation op2 = TTFOperation.from(insert(1, 2, "y", vc(3, 0, 0)));
        TTFOperation op3 = TTFOperation.from(delete(2, 1, 1, vc(3, 0, 0)));

        SOCT2MergeAlgorithm site2 = new SOCT2MergeAlgorithm(new TTFDocument(), 2);
        for (Operation op : ops) {
            site2.integrate(op);
        }
        site2.integrate(op3);
        assertEquals("ac", site2.getDoc().view());
        site2.integrate(op2);
        assertEquals("ayc", site2.getDoc().view());
        site2.integrate(op1);
        assertEquals("axyc", site2.getDoc().view());
    }

    @Test
    public void testTP2Puzzle() throws IncorrectTrace {
        SOCT2MergeAlgorithm site1 = new SOCT2MergeAlgorithm(new TTFDocument(), 1);
        SOCT2MergeAlgorithm site2 = new SOCT2MergeAlgorithm(new TTFDocument(), 2);
        SOCT2MergeAlgorithm site3 = new SOCT2MergeAlgorithm(new TTFDocument(), 3);

        List<Operation> ops0 = duplicate(site1.generateLocal(insert(1, 0, "abc")));
        integrateSeqAtSite(ops0, site2);
        integrateSeqAtSite(ops0, site3);
        List<Operation> ops1 = duplicate(site1.generateLocal(insert(1, 1, "x")));
        List<Operation> ops2 = duplicate(site2.generateLocal(insert(2, 2, "y")));
        List<Operation> ops3 = duplicate(site3.generateLocal(delete(3, 1, 1)));
        
        integrateSeqAtSite(ops2, site1);
        integrateSeqAtSite(ops3, site1);        
        assertEquals("axyc", site1.getDoc().view());

        integrateSeqAtSite(ops3, site2);
        integrateSeqAtSite(ops1, site2);
        assertEquals("axyc", site2.getDoc().view());

        integrateSeqAtSite(ops2, site3);
        integrateSeqAtSite(ops1, site3);
        assertEquals("axyc", site3.getDoc().view());
    }

    @Test
    public void testBasicScenario() throws IncorrectTrace {
        SOCT2MergeAlgorithm site0 = new SOCT2MergeAlgorithm(new TTFDocument(), 0);
        SOCT2MergeAlgorithm site2 = new SOCT2MergeAlgorithm(new TTFDocument(), 2);
        SOCT2MergeAlgorithm site4 = new SOCT2MergeAlgorithm(new TTFDocument(), 4);

        List<Operation> ops0 = duplicate(site0.generateLocal(insert(0, 0, "ABC")));
        assertEquals("ABC", site0.getDoc().view());
        
        integrateSeqAtSite(ops0, site2);
        assertEquals("ABC", site2.getDoc().view());
        
        List<Operation> ops2 = duplicate(site2.generateLocal(insert(2, 2, "vw")));
        assertEquals("ABvwC", site2.getDoc().view());
        List<Operation> ops2b = duplicate(site2.generateLocal(insert(2, 4, "xyz")));
        assertEquals("ABvwxyzC", site2.getDoc().view());

        integrateSeqAtSite(ops0, site4);
        assertEquals("ABC", site4.getDoc().view());

        List<Operation> ops4 = duplicate(site4.generateLocal(delete(4, 1, 2)));
        assertEquals("A", site4.getDoc().view());

        integrateSeqAtSite(ops4, site2);
        assertEquals("Avwxyz", site2.getDoc().view());

        integrateSeqAtSite(ops2, site4);
        integrateSeqAtSite(ops2b, site4);
        assertEquals("Avwxyz", site4.getDoc().view());

        assertEquals("ABC", site0.getDoc().view()); //
        integrateSeqAtSite(ops2, site0);
        assertEquals("ABvwC", site0.getDoc().view()); //
        integrateSeqAtSite(ops2b, site0);        
        assertEquals("ABvwxyzC", site0.getDoc().view());

        integrateSeqAtSite(ops4, site0);
        assertEquals("Avwxyz", site0.getDoc().view());
    }

    @Test
    public void testBasicScenario2() throws IncorrectTrace {
        SOCT2MergeAlgorithm site0 = new SOCT2MergeAlgorithm(new TTFDocument(), 0);
        SOCT2MergeAlgorithm site2 = new SOCT2MergeAlgorithm(new TTFDocument(), 2);
        SOCT2MergeAlgorithm site4 = new SOCT2MergeAlgorithm(new TTFDocument(), 4);

        /*
        Ins('Salut Monsieur  \nFin', 0, 1297672411625, 1, 0, [<0,1>])
        Ins('Bonjour', 14, 1297672411625, 1, 2 [<2,1><0,1>])
        Ins(' Mehdi', 21, 1297672411625, 1, 2, [<2,2><0,1>])
        Del(14, 3, 1297672512653, 1, 4, [<0,1><4,1>])    
         */
        List<Operation> ops0 = duplicate(site0.generateLocal(insert(0, 0, "Salut Monsieur  \nFin"))); // [<0,1>]        
        
        integrateSeqAtSite(ops0, site2);
        List<Operation> ops2 = site2.generateLocal(insert(2, 14, "Bonjour")); // [<2,1><0,1>]        
        List<Operation> ops2b = site2.generateLocal(insert(2, 21, " Mehdi")); // [<2,2><0,1>]
        assertEquals("Salut MonsieurBonjour Mehdi  \nFin", site2.getDoc().view());
        
        integrateSeqAtSite(ops0, site4);
        List<Operation> ops4 = duplicate(site4.generateLocal(delete(4, 14, 3))); // [<0,1><4,1>]

        integrateSeqAtSite(ops4, site2);
        assertEquals("Salut MonsieurBonjour MehdiFin", site2.getDoc().view());
        
        integrateSeqAtSite(ops2, site4);        
        integrateSeqAtSite(ops2b, site4);
        assertEquals("Salut MonsieurBonjour MehdiFin", site4.getDoc().view());
        
        integrateSeqAtSite(ops2, site0);        
        integrateSeqAtSite(ops2b, site0);
        integrateSeqAtSite(ops4, site0);
        assertEquals("Salut MonsieurBonjour MehdiFin", site0.getDoc().view());
    }

    // helpers
    private static TraceOperation insert(int r, int p, String s) {
        return insert(r, p, s, null);
    }

    private static TraceOperation insert(int r, int p, String s, VectorClock vc) {
        return TraceOperation.insert(r, p, s, vc);
    }

    private static TraceOperation delete(int r, int p, int o) {
        return delete(r, p, o, null);
    }

    private static TraceOperation delete(int r, int p, int o, VectorClock vc) {
        return TraceOperation.delete(r, p, o, vc);
    }

    private static VectorClock vc(int... v) {
        VectorClock vc = new VectorClock();
        for (int i = 0; i < v.length; i++) {
            vc.put(i, v[i]);
        }
        return vc;
    }

    private static String vcToString(VectorClock vc) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        Iterator<Map.Entry<Integer, Integer>> it = vc.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, Integer> e = it.next();
            sb.append("<");
            sb.append(e.getKey());
            sb.append(",");
            sb.append(e.getValue());
            sb.append(">");
        }
        sb.append("]");
        return sb.toString();
    }

    public static List<Operation> duplicate(List<Operation> list) {
        ArrayList<Operation> res = new ArrayList<Operation>();
        for (Operation elt : list) {
            res.add(((TTFOperation) elt).clone());
        }
        return res;
    }

    private static void integrateSeqAtSite(List<Operation> seq, SOCT2MergeAlgorithm site) throws IncorrectTrace {
        for (Operation op : duplicate(seq)) {
            site.integrate(op);
        }
    }
}
