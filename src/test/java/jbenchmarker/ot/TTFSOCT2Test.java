/**
 * Replication Benchmarker
 * https://github.com/score-team/replication-benchmarker/ Copyright (C) 2011
 * INRIA / LORIA / SCORE Team
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
package jbenchmarker.ot;

import jbenchmarker.ot.ttf.TTFSequenceMessage;
import jbenchmarker.ot.ttf.TTFMergeAlgorithm;
import jbenchmarker.ot.ttf.TTFOperation;
import jbenchmarker.ot.ttf.TTFDocument;
import jbenchmarker.ot.soct2.SOCT2Message;
import crdt.simulator.IncorrectTraceException;
import java.util.ArrayList;
import jbenchmarker.core.SequenceOperation.OpType;
import java.util.Map;
import java.util.Iterator;
import collect.VectorClock;
import java.util.List;
import jbenchmarker.core.SequenceMessage;
import jbenchmarker.core.SequenceOperation;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author oster
 */
public class TTFSOCT2Test {

    @Test
    public void testGenerateLocalInsertCharByChar() throws IncorrectTraceException {
        int siteId = 0;
        TTFMergeAlgorithm merger = new TTFMergeAlgorithm(new TTFDocument(), siteId);

        List<SequenceMessage> ops = merger.generateLocal(insert(siteId, 0, "b"));
        assertEquals(1, ops.size());
        SOCT2Message<TTFOperation> opg = ((TTFSequenceMessage) ops.get(0)).getSoct2Message();
        assertEquals(OpType.ins, opg.getOperation().getType());
        assertEquals('b', opg.getOperation().getChar());
        assertEquals(0, opg.getOperation().getPosition());
        assertEquals("[]", vcToString(opg.getClock()));
        assertEquals("b", merger.lookup());


        ops = merger.generateLocal(insert(siteId, 0, "a"));
        assertEquals(1, ops.size());
        opg = ((TTFSequenceMessage) ops.get(0)).getSoct2Message();
        assertEquals('a', opg.getOperation().getChar());
        assertEquals(0, opg.getOperation().getPosition());
        assertEquals("[<0,1>]", vcToString(opg.getClock()));
        assertEquals("ab", merger.lookup());

        ops = merger.generateLocal(insert(siteId, 2, "c"));
        assertEquals(1, ops.size());
        opg = ((TTFSequenceMessage) ops.get(0)).getSoct2Message();
        assertEquals('c', opg.getOperation().getChar());
        assertEquals("[<0,2>]", vcToString(opg.getClock()));
        assertEquals(2, opg.getOperation().getPosition());
        assertEquals("abc", merger.lookup());
    }

    @Test
    public void testGenerateLocalInsertString() throws IncorrectTraceException {
        int siteId = 0;
        TTFMergeAlgorithm merger = new TTFMergeAlgorithm(new TTFDocument(), siteId);

        List<SequenceMessage> ops = merger.generateLocal(insert(siteId, 0, "abc"));
        assertEquals(3, ops.size());

        SOCT2Message<TTFOperation> opg = ((TTFSequenceMessage) ops.get(0)).getSoct2Message();
        assertEquals(OpType.ins, opg.getOperation().getType());
        assertEquals('a', opg.getOperation().getChar());
        assertEquals(0, opg.getOperation().getPosition());
        assertEquals("[]", vcToString(opg.getClock()));

        opg = ((TTFSequenceMessage) ops.get(1)).getSoct2Message();
        assertEquals('b', opg.getOperation().getChar());
        assertEquals(1, opg.getOperation().getPosition());
        assertEquals("[<0,1>]", vcToString(opg.getClock()));

        opg = ((TTFSequenceMessage) ops.get(2)).getSoct2Message();
        assertEquals('c', opg.getOperation().getChar());
        assertEquals("[<0,2>]", vcToString(opg.getClock()));
        assertEquals(2, opg.getOperation().getPosition());

        assertEquals("abc", merger.lookup());
    }

    @Test
    public void testGenerateLocalDeleteCharByChar() throws IncorrectTraceException {
        int siteId = 0;
        TTFMergeAlgorithm merger = new TTFMergeAlgorithm(new TTFDocument(), siteId);
        merger.generateLocal(insert(siteId, 0, "abcd"));

        // remove 'a'
        List<SequenceMessage> ops = merger.generateLocal(delete(siteId, 0, 1));
        assertEquals(1, ops.size());
        SOCT2Message<TTFOperation> opg = ((TTFSequenceMessage) ops.get(0)).getSoct2Message();
        assertEquals(OpType.del, opg.getOperation().getType());
        assertEquals(0, opg.getOperation().getPosition());
        assertEquals("[<0,4>]", vcToString(opg.getClock()));
        assertEquals("bcd", merger.lookup());

        // remove 'd'
        ops = merger.generateLocal(delete(siteId, 2, 1));
        assertEquals(1, ops.size());
        opg = ((TTFSequenceMessage) ops.get(0)).getSoct2Message();
        assertEquals(OpType.del, opg.getOperation().getType());
        assertEquals(3, opg.getOperation().getPosition());
        assertEquals("[<0,5>]", vcToString(opg.getClock()));
        assertEquals("bc", merger.lookup());

        // remove 'c'
        ops = merger.generateLocal(delete(siteId, 1, 1));
        assertEquals(1, ops.size());
        opg = ((TTFSequenceMessage) ops.get(0)).getSoct2Message();
        assertEquals(OpType.del, opg.getOperation().getType());
        assertEquals(2, opg.getOperation().getPosition());
        assertEquals("[<0,6>]", vcToString(opg.getClock()));
        assertEquals("b", merger.lookup());

        // remove 'b'
        ops = merger.generateLocal(delete(siteId, 0, 1));
        assertEquals(1, ops.size());
        opg = ((TTFSequenceMessage) ops.get(0)).getSoct2Message();
        assertEquals(OpType.del, opg.getOperation().getType());
        assertEquals(1, opg.getOperation().getPosition());
        assertEquals("[<0,7>]", vcToString(opg.getClock()));
        assertEquals("", merger.lookup());
    }

    @Test
    public void testGenerateLocalDeleteString() throws IncorrectTraceException {
        int siteId = 0;
        TTFMergeAlgorithm merger = new TTFMergeAlgorithm(new TTFDocument(), siteId);
        merger.generateLocal(insert(siteId, 0, "abcd"));

        // remove "abcd"
        List<SequenceMessage> ops = merger.generateLocal(delete(siteId, 0, 4));

        assertEquals(4, ops.size());
        SOCT2Message<TTFOperation> opg = ((TTFSequenceMessage) ops.get(0)).getSoct2Message();
        assertEquals(OpType.del, opg.getOperation().getType());
        assertEquals(0, opg.getOperation().getPosition());
        assertEquals("[<0,4>]", vcToString(opg.getClock()));

        opg = ((TTFSequenceMessage) ops.get(1)).getSoct2Message();
        assertEquals(OpType.del, opg.getOperation().getType());
        assertEquals(1, opg.getOperation().getPosition());
        assertEquals("[<0,5>]", vcToString(opg.getClock()));

        opg = ((TTFSequenceMessage) ops.get(2)).getSoct2Message();
        assertEquals(OpType.del, opg.getOperation().getType());
        assertEquals(2, opg.getOperation().getPosition());
        assertEquals("[<0,6>]", vcToString(opg.getClock()));

        opg = ((TTFSequenceMessage) ops.get(3)).getSoct2Message();
        assertEquals(OpType.del, opg.getOperation().getType());
        assertEquals(3, opg.getOperation().getPosition());
        assertEquals("[<0,7>]", vcToString(opg.getClock()));

        assertEquals("", merger.lookup());
    }

    @Test
    public void testGenerateLocalDeleteStringWhichContainsDeletedChars() throws IncorrectTraceException {
        int siteId = 0;
        TTFMergeAlgorithm merger = new TTFMergeAlgorithm(new TTFDocument(), siteId);
        merger.generateLocal(insert(siteId, 0, "abcdefg"));

        merger.generateLocal(delete(siteId, 2, 2));
        assertEquals("abefg", merger.lookup());

        // remove "bef"
        List<SequenceMessage> ops = merger.generateLocal(delete(siteId, 1, 3));

        assertEquals(3, ops.size());
        SOCT2Message<TTFOperation> opg = ((TTFSequenceMessage) ops.get(0)).getSoct2Message();
        assertEquals(OpType.del, opg.getOperation().getType());
        assertEquals(1, opg.getOperation().getPosition());

        opg = ((TTFSequenceMessage) ops.get(1)).getSoct2Message();
        assertEquals(OpType.del, opg.getOperation().getType());
        assertEquals(4, opg.getOperation().getPosition());

        opg = ((TTFSequenceMessage) ops.get(2)).getSoct2Message();
        assertEquals(OpType.del, opg.getOperation().getType());
        assertEquals(5, opg.getOperation().getPosition());

        assertEquals("ag", merger.lookup());
    }

    @Test
    public void testVectorClockEvolution() throws IncorrectTraceException {
        int siteId = 0;
        TTFMergeAlgorithm merger = new TTFMergeAlgorithm(new TTFDocument(), siteId);
        assertEquals(vc(0), merger.getClock());

        SOCT2Message op1 = ((TTFSequenceMessage) merger.generateLocal(insert(siteId, 0, "a")).get(0)).getSoct2Message();
        assertEquals(vc(1), merger.getClock());

        SOCT2Message op2 = ((TTFSequenceMessage) merger.generateLocal(insert(siteId, 1, "b")).get(0)).getSoct2Message();
        assertEquals(vc(2), merger.getClock());

        assertEquals(vc(0), op1.getClock());
        assertEquals(vc(1), op2.getClock());
    }

    @Test
    public void testTP2PuzzleAtSite0() throws IncorrectTraceException {
        TTFMergeAlgorithm site0 = new TTFMergeAlgorithm(new TTFDocument(), 0);
        site0.generateLocal(insert(0, 0, "abc"));

        //site0.generateLocal(insert(0, 1, "x"));
        TTFSequenceMessage op1 = TTFSequenceMessageFrom(insert(0, 1, "x", vc(3, 0, 0)));
        TTFSequenceMessage op2 = TTFSequenceMessageFrom(insert(1, 2, "y", vc(3, 0, 0)));
        TTFSequenceMessage op3 = TTFSequenceMessageFrom(delete(2, 1, 1, vc(3, 0, 0)));

        site0.integrateRemote(op1);
        assertEquals("axbc", site0.lookup());
        site0.integrateRemote(op2);
        assertEquals("axbyc", site0.lookup());
        site0.integrateRemote(op3);
        assertEquals("axyc", site0.lookup());
    }

    @Test
    public void testTP2PuzzleAtSite0bis() throws IncorrectTraceException {
        TTFMergeAlgorithm site0 = new TTFMergeAlgorithm(new TTFDocument(), 0);
        site0.generateLocal(insert(0, 0, "abc"));

        TTFSequenceMessage op1 = TTFSequenceMessageFrom(insert(0, 1, "x", vc(3, 0, 0)));
        TTFSequenceMessage op2 = TTFSequenceMessageFrom(insert(1, 2, "y", vc(3, 0, 0)));
        TTFSequenceMessage op3 = TTFSequenceMessageFrom(delete(2, 1, 1, vc(3, 0, 0)));

        site0.integrateRemote(op1);
        assertEquals("axbc", site0.lookup());
        site0.integrateRemote(op3);
        assertEquals("axc", site0.lookup());
        site0.integrateRemote(op2);
        assertEquals("axyc", site0.lookup());
    }

    @Test
    public void testTP2PuzzleAtSite1() throws IncorrectTraceException {
        TTFMergeAlgorithm site0 = new TTFMergeAlgorithm(new TTFDocument(), 0);
        List<SequenceMessage> ops = site0.generateLocal(insert(0, 0, "abc"));

        TTFSequenceMessage op1 = TTFSequenceMessageFrom(insert(0, 1, "x", vc(3, 0, 0)));
        TTFSequenceMessage op2 = TTFSequenceMessageFrom(insert(1, 2, "y", vc(3, 0, 0)));
        TTFSequenceMessage op3 = TTFSequenceMessageFrom(delete(2, 1, 1, vc(3, 0, 0)));

        TTFMergeAlgorithm site1 = new TTFMergeAlgorithm(new TTFDocument(), 1);
        for (SequenceMessage op : ops) {
            site1.integrateRemote(op);
        }
        site1.integrateRemote(op2);
        assertEquals("abyc", site1.lookup());
        site1.integrateRemote(op1);
        assertEquals("axbyc", site1.lookup());
        site1.integrateRemote(op3);
        assertEquals("axyc", site1.lookup());
    }

    @Test
    public void testTP2PuzzleAtSite1bis() throws IncorrectTraceException {
        TTFMergeAlgorithm site0 = new TTFMergeAlgorithm(new TTFDocument(), 0);
        List<SequenceMessage> ops = site0.generateLocal(insert(0, 0, "abc"));

        TTFSequenceMessage op1 = TTFSequenceMessageFrom(insert(0, 1, "x", vc(3, 0, 0)));
        TTFSequenceMessage op2 = TTFSequenceMessageFrom(insert(1, 2, "y", vc(3, 0, 0)));
        TTFSequenceMessage op3 = TTFSequenceMessageFrom(delete(2, 1, 1, vc(3, 0, 0)));

        TTFMergeAlgorithm site1 = new TTFMergeAlgorithm(new TTFDocument(), 1);
        for (SequenceMessage op : ops) {
            site1.integrateRemote(op);
        }
        site1.integrateRemote(op2);
        assertEquals("abyc", site1.lookup());
        site1.integrateRemote(op3);
        assertEquals("ayc", site1.lookup());
        site1.integrateRemote(op1);
        assertEquals("axyc", site1.lookup());
    }

    @Test
    public void testTP2PuzzleAtSite2() throws IncorrectTraceException {
        TTFMergeAlgorithm site0 = new TTFMergeAlgorithm(new TTFDocument(), 0);
        List<SequenceMessage> ops = site0.generateLocal(insert(0, 0, "abc"));

        TTFSequenceMessage op1 = TTFSequenceMessageFrom(insert(0, 1, "x", vc(3, 0, 0)));
        TTFSequenceMessage op2 = TTFSequenceMessageFrom(insert(1, 2, "y", vc(3, 0, 0)));
        TTFSequenceMessage op3 = TTFSequenceMessageFrom(delete(2, 1, 1, vc(3, 0, 0)));

        TTFMergeAlgorithm site2 = new TTFMergeAlgorithm(new TTFDocument(), 2);
        for (SequenceMessage op : ops) {
            site2.integrateRemote(op);
        }
        site2.integrateRemote(op3);
        assertEquals("ac", site2.lookup());
        site2.integrateRemote(op1);
        assertEquals("axc", site2.lookup());
        site2.integrateRemote(op2);
        assertEquals("axyc", site2.lookup());
    }

    @Test
    public void testTP2PuzzleAtSite2bis() throws IncorrectTraceException {
        TTFMergeAlgorithm site0 = new TTFMergeAlgorithm(new TTFDocument(), 0);
        List<SequenceMessage> ops = site0.generateLocal(insert(0, 0, "abc"));

        TTFSequenceMessage op1 = TTFSequenceMessageFrom(insert(0, 1, "x", vc(3, 0, 0)));
        TTFSequenceMessage op2 = TTFSequenceMessageFrom(insert(1, 2, "y", vc(3, 0, 0)));
        TTFSequenceMessage op3 = TTFSequenceMessageFrom(delete(2, 1, 1, vc(3, 0, 0)));

        TTFMergeAlgorithm site2 = new TTFMergeAlgorithm(new TTFDocument(), 2);
        for (SequenceMessage op : ops) {
            site2.integrateRemote(op);
        }
        site2.integrateRemote(op3);
        assertEquals("ac", site2.lookup());
        site2.integrateRemote(op2);
        assertEquals("ayc", site2.lookup());
        site2.integrateRemote(op1);
        assertEquals("axyc", site2.lookup());
    }

    @Test
    public void testTP2Puzzle() throws IncorrectTraceException {
        TTFMergeAlgorithm site1 = new TTFMergeAlgorithm(new TTFDocument(), 1);
        TTFMergeAlgorithm site2 = new TTFMergeAlgorithm(new TTFDocument(), 2);
        TTFMergeAlgorithm site3 = new TTFMergeAlgorithm(new TTFDocument(), 3);

        List<SequenceMessage> ops0 = duplicate(site1.generateLocal(insert(1, 0, "abc")));
        assertEquals("abc", site1.lookup());
        integrateSeqAtSite(ops0, site2);
        assertEquals("abc", site2.lookup());
        integrateSeqAtSite(ops0, site3);
        assertEquals("abc", site3.lookup());
        List<SequenceMessage> ops1 = duplicate(site1.generateLocal(insert(1, 1, "x")));
        assertEquals("axbc", site1.lookup());
        List<SequenceMessage> ops2 = duplicate(site2.generateLocal(insert(2, 2, "y")));
        assertEquals("abyc", site2.lookup());
        List<SequenceMessage> ops3 = duplicate(site3.generateLocal(delete(3, 1, 1)));
        assertEquals("ac", site3.lookup());

        integrateSeqAtSite(ops2, site1);
        assertEquals("axbyc", site1.lookup());
        integrateSeqAtSite(ops3, site1);
        assertEquals("axyc", site1.lookup());

        integrateSeqAtSite(ops3, site2);
        assertEquals("ayc", site2.lookup());
        integrateSeqAtSite(ops1, site2);
        assertEquals("axyc", site2.lookup());

        integrateSeqAtSite(ops2, site3);
        integrateSeqAtSite(ops1, site3);
        assertEquals("axyc", site3.lookup());
    }

    @Test
    public void testBasicScenario() throws IncorrectTraceException {
        TTFMergeAlgorithm site0 = new TTFMergeAlgorithm(new TTFDocument(), 0);
        TTFMergeAlgorithm site2 = new TTFMergeAlgorithm(new TTFDocument(), 2);
        TTFMergeAlgorithm site4 = new TTFMergeAlgorithm(new TTFDocument(), 4);

        List<SequenceMessage> ops0 = duplicate(site0.generateLocal(insert(0, 0, "ABC")));
        assertEquals("ABC", site0.lookup());

        integrateSeqAtSite(ops0, site2);
        assertEquals("ABC", site2.lookup());

        List<SequenceMessage> ops2 = duplicate(site2.generateLocal(insert(2, 2, "vw")));
        assertEquals("ABvwC", site2.lookup());
        List<SequenceMessage> ops2b = duplicate(site2.generateLocal(insert(2, 4, "xyz")));
        assertEquals("ABvwxyzC", site2.lookup());

        integrateSeqAtSite(ops0, site4);
        assertEquals("ABC", site4.lookup());

        List<SequenceMessage> ops4 = duplicate(site4.generateLocal(delete(4, 1, 2)));
        assertEquals("A", site4.lookup());

        integrateSeqAtSite(ops4, site2);
        assertEquals("Avwxyz", site2.lookup());

        integrateSeqAtSite(ops2, site4);
        integrateSeqAtSite(ops2b, site4);
        assertEquals("Avwxyz", site4.lookup());

        assertEquals("ABC", site0.lookup()); //
        integrateSeqAtSite(ops2, site0);
        assertEquals("ABvwC", site0.lookup()); //
        integrateSeqAtSite(ops2b, site0);
        assertEquals("ABvwxyzC", site0.lookup());

        integrateSeqAtSite(ops4, site0);
        assertEquals("Avwxyz", site0.lookup());
    }

    @Test
    public void testBasicScenario2() throws IncorrectTraceException {
        TTFMergeAlgorithm site0 = new TTFMergeAlgorithm(new TTFDocument(), 0);
        TTFMergeAlgorithm site2 = new TTFMergeAlgorithm(new TTFDocument(), 2);
        TTFMergeAlgorithm site4 = new TTFMergeAlgorithm(new TTFDocument(), 4);

        /*
         * Ins('Salut Monsieur \nFin', 0, 1297672411625, 1, 0, [<0,1>])
         * Ins('Bonjour', 14, 1297672411625, 1, 2 [<2,1><0,1>]) Ins(' Mehdi',
         * 21, 1297672411625, 1, 2, [<2,2><0,1>]) Del(14, 3, 1297672512653, 1,
         * 4, [<0,1><4,1>])
         */
        List<SequenceMessage> ops0 = duplicate(site0.generateLocal(insert(0, 0, "Salut Monsieur  \nFin"))); // [<0,1>]        

        integrateSeqAtSite(ops0, site2);
        List<SequenceMessage> ops2 = site2.generateLocal(insert(2, 14, "Bonjour")); // [<2,1><0,1>]        
        List<SequenceMessage> ops2b = site2.generateLocal(insert(2, 21, " Mehdi")); // [<2,2><0,1>]
        assertEquals("Salut MonsieurBonjour Mehdi  \nFin", site2.lookup());

        integrateSeqAtSite(ops0, site4);
        List<SequenceMessage> ops4 = duplicate(site4.generateLocal(delete(4, 14, 3))); // [<0,1><4,1>]

        integrateSeqAtSite(ops4, site2);
        assertEquals("Salut MonsieurBonjour MehdiFin", site2.lookup());

        integrateSeqAtSite(ops2, site4);
        integrateSeqAtSite(ops2b, site4);
        assertEquals("Salut MonsieurBonjour MehdiFin", site4.lookup());

        integrateSeqAtSite(ops2, site0);
        integrateSeqAtSite(ops2b, site0);
        integrateSeqAtSite(ops4, site0);
        assertEquals("Salut MonsieurBonjour MehdiFin", site0.lookup());
    }

    @Test
    public void testBuggyScenario1() throws IncorrectTraceException {
        TTFMergeAlgorithm site1 = new TTFMergeAlgorithm(new TTFDocument(), 1);
        TTFMergeAlgorithm site2 = new TTFMergeAlgorithm(new TTFDocument(), 2);
        TTFMergeAlgorithm site3 = new TTFMergeAlgorithm(new TTFDocument(), 3);

//        List<SequenceMessage> ops2 = duplicate(site2.generateLocal(insert(2, 0, "ed")));
//        List<SequenceMessage> ops2 = duplicate(site2.generateLocal(insert(2, 0, "ed", vc(0,0,1,0))));
        VectorClock vc2 = new VectorClock();
        vc2.inc(2);
        List<SequenceMessage> ops2 = duplicate(site2.generateLocal(insert(2, 0, "ed", vc2)));

        assertEquals("ed", site2.lookup());
//        List<SequenceMessage> ops3 = duplicate(site3.generateLocal(insert(3, 0, "h")));
//        List<SequenceMessage> ops3 = duplicate(site3.generateLocal(insert(3, 0, "h", vc(0,0,0,1))));
        VectorClock vc3 = new VectorClock();
        vc3.inc(3);
        List<SequenceMessage> ops3 = duplicate(site3.generateLocal(insert(3, 0, "h", vc3)));
        assertEquals("h", site3.lookup());
//        List<SequenceMessage> ops1 = duplicate(site1.generateLocal(insert(1, 0, "q")));
//        List<SequenceMessage> ops1 = duplicate(site1.generateLocal(insert(1, 0, "q", vc(0,1,0,0))));
        VectorClock vc1 = new VectorClock();
        vc1.inc(1);
        List<SequenceMessage> ops1 = duplicate(site1.generateLocal(insert(1, 0, "q", vc1)));
        assertEquals("q", site1.lookup());

        integrateSeqAtSite(ops2, site1);
        assertEquals("qed", site1.lookup());
        integrateSeqAtSite(ops3, site1);
        assertEquals("qedh", site1.lookup());

        integrateSeqAtSite(ops1, site2);
        assertEquals("qed", site2.lookup());
        integrateSeqAtSite(ops3, site2);
        assertEquals("qedh", site2.lookup());

        integrateSeqAtSite(ops1, site3);
        assertEquals("qh", site3.lookup());
        integrateSeqAtSite(ops2, site3);
        assertEquals("qedh", site3.lookup());
    }

    @Test
    public void testPartialConcurrencyScenarioWith3Insert() throws IncorrectTraceException {
        TTFMergeAlgorithm site1 = new TTFMergeAlgorithm(new TTFDocument(), 1);
        TTFMergeAlgorithm site2 = new TTFMergeAlgorithm(new TTFDocument(), 2);

        List<SequenceMessage> ops0 = duplicate(site1.generateLocal(insert(1, 0, "ABC")));
        integrateSeqAtSite(ops0, site2);

        List<SequenceMessage> ops1 = duplicate(site1.generateLocal(insert(1, 2, "X")));
        assertEquals("ABXC", site1.lookup());

        List<SequenceMessage> ops2 = duplicate(site2.generateLocal(insert(2, 1, "12")));
        List<SequenceMessage> ops2b = duplicate(site2.generateLocal(insert(2, 3, "34")));
        assertEquals("A1234BC", site2.lookup());

        integrateSeqAtSite(ops1, site2);
        assertEquals("A1234BXC", site2.lookup());

        integrateSeqAtSite(ops2, site1);
        assertEquals("A12BXC", site1.lookup());
        integrateSeqAtSite(ops2b, site1);
    }

    // helpers
    private static SequenceOperation insert(int r, int p, String s) {
        return insert(r, p, s, null);
    }

    private static SequenceOperation insert(int r, int p, String s, VectorClock vc) {
        return SequenceOperation.insert(r, p, s, vc);
    }

    private static SequenceOperation delete(int r, int p, int o) {
        return delete(r, p, o, null);
    }

    private static SequenceOperation delete(int r, int p, int o, VectorClock vc) {
        return SequenceOperation.delete(r, p, o, vc);
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
    
      public static TTFSequenceMessage TTFSequenceMessageFrom(SequenceOperation opt) {
        TTFOperation op;
        if (opt.getType() == OpType.ins) {
            op = new TTFOperation(opt.getType(), opt.getPosition(), opt.getContent().get(0), opt.getReplica());
        } else {
            op = new TTFOperation(opt.getType(), opt.getPosition(), opt.getReplica());

        }

        SOCT2Message smess = new SOCT2Message(opt.getVectorClock(), opt.getReplica(), op);
        TTFSequenceMessage mess = new TTFSequenceMessage(smess, opt);
        return mess;



    }

    public static List<SequenceMessage> duplicate(List<SequenceMessage> list) {
        ArrayList<SequenceMessage> res = new ArrayList<SequenceMessage>();
        for (SequenceMessage elt : list) {
            res.add(((TTFSequenceMessage) elt).copy());
        }
        return res;
    }

    private static void integrateSeqAtSite(List<SequenceMessage> seq, TTFMergeAlgorithm site) throws IncorrectTraceException {
        for (SequenceMessage op : duplicate(seq)) {
            site.integrateRemote(op);
        }
    }
}
