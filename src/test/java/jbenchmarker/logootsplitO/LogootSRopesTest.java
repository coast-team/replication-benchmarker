/*
 *  Replication Benchmarker
 *  https://github.com/score-team/replication-benchmarker/
 *  Copyright (C) 2012 LORIA / Inria / SCORE Team
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.

 */
package jbenchmarker.logootsplitO;

import crdt.CRDTMessage;
import crdt.OperationBasedOneMessage;
import crdt.PreconditionException;
import crdt.simulator.CausalSimulator;
import crdt.simulator.Trace;
import crdt.simulator.random.ProgressTrace;
import crdt.simulator.random.RandomTrace;
import crdt.simulator.random.StandardSeqOpProfile;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import jbenchmarker.core.MergeAlgorithm;
import jbenchmarker.core.SequenceOperation;
import jbenchmarker.factories.LogootSplitOFactory;
import jbenchmarker.logootsplitO.LogootSRopes.RopesNodes;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author Stephane Martin <stephane@stephanemartin.fr>
 */
public class LogootSRopesTest {

    static List<Character> getListFromString(String str) {
        List<Character> l = new LinkedList();
        for (int i = 0; i < str.length(); i++) {
            l.add(str.charAt(i));
        }
        return l;
    }
    LogootSAlgo alg1;
    LogootSAlgo alg2;
    LogootSAlgo alg3;
    LogootSAlgo alg4;

    @Before
    public void setup() {
        alg1 = new LogootSAlgo(new LogootSRopes(), 1);
        alg2 = new LogootSAlgo(new LogootSRopes(), 50);
        alg3 = new LogootSAlgo(new LogootSRopes(), 75);
        alg4 = new LogootSAlgo(new LogootSRopes(), 77);
    }

    public LogootSRopesTest() {
    }

    public static String convert(List r) {
        StringBuilder str = new StringBuilder(r.size());
        for (Object o : r) {
            str.append(o);
        }
        return str.toString();
    }

    @Test
    public void SimpleAddMergeCheck() throws PreconditionException {
        LinkedList<CRDTMessage> l = new LinkedList();
        l.add(alg1.insert(0, "Hello"));
        assertEquals("Hello", alg1.lookup());
        l.add(alg1.insert(5, " world"));

        assertEquals("Hello world", alg1.lookup());
        LogootSRopes doc = (LogootSRopes) alg1.getDoc();
        LogootSRopes.RopesNodes root = doc.root;
        assertNull(root.getLeft());
        assertNull(root.getRight());
        assertEquals("Hello world", convert(root.str));
        assertEquals(1, root.getNodesInSubtree());
        assertEquals(11, root.getSizeNodeAndChildren());

        l.add(alg1.insert(0, "---"));
        root = doc.root;
        assertNull(root.getLeft());
        assertNull(root.getRight());
        assertEquals("---Hello world", convert(root.str));
        assertEquals(1, root.getNodesInSubtree());
        assertEquals(14, root.getSizeNodeAndChildren());



        /**
         * Apply to another sites.
         *
         */
        alg2.applyRemote(l.pop());
        assertEquals("Hello", alg2.lookup());
        LogootSRopes doc2 = (LogootSRopes) alg2.getDoc();
        root = doc2.root;
        assertEquals(1, root.getNodesInSubtree());
        assertEquals(5, root.getSizeNodeAndChildren());
        assertEquals("Hello", convert(root.str));
        assertNull(root.getLeft());
        assertNull(root.getRight());

        alg2.applyRemote(l.pop());

        assertEquals("Hello world", alg2.lookup());
        doc2 = (LogootSRopes) alg2.getDoc();
        root = doc2.root;
        assertEquals(1, root.getNodesInSubtree());
        assertEquals(11, root.getSizeNodeAndChildren());
        assertEquals("Hello world", convert(root.str));
        assertNull(root.getLeft());
        assertNull(root.getRight());


        alg2.applyRemote(l.pop());
        assertEquals("---Hello world", alg2.lookup());
        doc2 = (LogootSRopes) alg2.getDoc();
        root = doc2.root;
        assertEquals(1, root.getNodesInSubtree());
        assertEquals(14, root.getSizeNodeAndChildren());
        assertEquals("---Hello world", convert(root.str));
        assertNull(root.getLeft());
        assertNull(root.getRight());

    }

    @Test
    public void testTest() throws PreconditionException {
        LinkedList<CRDTMessage> l = new LinkedList();
        l.add(alg3.insert(0, " world"));

        alg1.applyRemote(l.get(0));
        l.add(alg1.insert(0, "Hello"));

        l.add(alg1.insert(11, "Every ones ?"));
        LogootSRopes doc = (LogootSRopes) alg1.getDoc();
        LinkedList<RopesNodes> lp = new LinkedList<RopesNodes>();
        doc.searchFull(doc.root, ((LogootSOpAdd) MergeAlgorithm.CRDTMessage2SequenceMessage(l.getLast())).id, lp);
        assertTrue(lp.size() > 0);
        doc.searchFull(doc.root, ((LogootSOpAdd) MergeAlgorithm.CRDTMessage2SequenceMessage(l.getLast())).id, lp);
        lp.clear();
        assertFalse(doc.searchFull(doc.root, new Identifier(Arrays.asList(0, 0, 0, 0, 0), 42), lp));
        assertEquals(lp.size(), 0);
    }

    @Test
    public void SimpleAddCheck() throws PreconditionException {
        LinkedList<CRDTMessage> l = new LinkedList();
        l.add(alg3.insert(0, " world"));

        alg1.applyRemote(l.get(0));
        l.add(alg1.insert(0, "Hello"));
        assertEquals("Hello world", alg1.lookup());
        LogootSRopes doc = (LogootSRopes) alg1.getDoc();
        LogootSRopes.RopesNodes root = doc.root;
        LogootSRopes.RopesNodes prev = root.getLeft();
        assertEquals(" world", convert(root.str));
        assertEquals("Hello", convert(prev.str));

        assertEquals(2, root.getNodesInSubtree());
        assertEquals(1, prev.getNodesInSubtree());
        assertEquals(11, root.getSizeNodeAndChildren());
        assertEquals(5, prev.getSizeNodeAndChildren());
        assertEquals(1, prev.getNodesInSubtree());

        assertEquals(6, root.getSize());
        assertEquals(5, prev.getSize());

        l.add(alg1.insert(11, "Every ones ?"));
        root = doc.root;
        LogootSRopes.RopesNodes next = root.getRight();
        LogootSRopes.RopesNodes previous = root.getLeft();

        assertEquals(3, root.getNodesInSubtree());
        assertEquals(1, next.getNodesInSubtree());
        assertEquals(1, previous.getNodesInSubtree());
        assertEquals(23, root.getSizeNodeAndChildren());
        assertEquals(12, next.getSizeNodeAndChildren());
        assertEquals(5, previous.getSizeNodeAndChildren());
        assertEquals("Hello", convert(previous.str));
        assertEquals(" world", convert(root.str));
        assertEquals("Every ones ?", convert(next.str));
        alg2.applyRemote(l.pollLast());

        alg2.applyRemote(l.pollLast());
        System.out.println(((LogootSRopes) alg2.getLDoc()).root.toString());
        alg2.applyRemote(l.pollLast());
        root = ((LogootSRopes) alg2.getLDoc()).root;
        next = root.getRight();
        previous = root.getLeft();

        assertEquals(3, root.getNodesInSubtree());
        assertEquals(1, next.getNodesInSubtree());
        assertEquals(1, previous.getNodesInSubtree());
        assertEquals(23, root.getSizeNodeAndChildren());
        assertEquals(12, next.getSizeNodeAndChildren());
        assertEquals(5, previous.getSizeNodeAndChildren());
        assertEquals("Hello", convert(previous.str));
        assertEquals(" world", convert(root.str));
        assertEquals("Every ones ?", convert(next.str));
    }

    @Test
    public void adddel() throws PreconditionException {
        CRDTMessage p = alg3.insert(0, "abcd");
        alg2.applyRemote(p);
        alg1.applyRemote(p);
        assertEquals("abcd", alg2.lookup());
        CRDTMessage p2 = alg2.remove(0, 2);
        CRDTMessage p3 = alg2.remove(0, 2);

        alg1.applyRemote(p2);
        assertEquals("cd", alg1.lookup());
        alg1.applyRemote(p3);
        assertEquals("", alg1.lookup());


        alg3.applyRemote(p3);
        assertEquals("ab", alg3.lookup());
        alg3.applyRemote(p2);
        assertEquals("", alg3.lookup());

    }

    @Test
    public void SimpleAddDelTest() throws PreconditionException {




        CRDTMessage op1 = alg1.insert(0, "Test1234");
        assertEquals("Test1234", alg1.lookup());

        CRDTMessage op2 = alg1.insert(5, "haha");
        assertEquals("Test1haha234", alg1.lookup());
        assertEquals("[T, e, s, t, 1, 2, 3, 4]", ((LogootSOpAdd) MergeAlgorithm.CRDTMessage2SequenceMessage(op1)).l.toString());
        assertEquals("[h, a, h, a]", ((LogootSOpAdd) MergeAlgorithm.CRDTMessage2SequenceMessage(op2)).l.toString());

        alg2.applyRemote(op2);
        assertEquals("haha", alg2.lookup());

        alg2.applyRemote(op1);
        assertEquals("Test1haha234", alg2.lookup());

        alg3.applyRemote(op1);
        alg3.applyRemote(op2);
        assertEquals("Test1haha234", alg3.lookup());
        if (false) {
            return;
        }

        /**
         * Del
         */
        CRDTMessage op3 = alg3.remove(4, 6);

        assertEquals("Test34", alg3.lookup());
        alg2.applyRemote(op3);
        assertEquals("Test34", alg2.lookup());

        /**
         * Make another del
         */
        assertEquals("Test1haha234", alg1.lookup());
        CRDTMessage op4 = alg1.remove(3, 4);
        assertEquals("Tesha234", alg1.lookup());

        //assertEquals(2,op4.lid.size());


        /**
         * integration of del
         */
        alg1.applyRemote(op3);

        assertEquals("Tes34", alg1.lookup());

        alg2.applyRemote(op4);

        assertEquals("Tes34", alg2.lookup());


        alg3.applyRemote(op4);

        assertEquals("Tes34", alg3.lookup());
        CRDTMessage op5 = alg3.insert(2, "toto");

        LinkedList l1 = browse(alg1);
        LinkedList l2 = browse(alg2);
        LinkedList l3 = browse(alg3);
        scoreChecks(alg1, alg2, alg3);


        CRDTMessage op6 = alg2.insert(3, "jiji");

        alg1.applyRemote(op6);
        alg1.applyRemote(op5);

        alg2.applyRemote(op5);
        alg3.applyRemote(op6);
        System.out.println(((LogootSRopes) alg1.getLDoc()).root.viewRec());
        System.out.println(((LogootSRopes) alg2.getLDoc()).root.viewRec());
        System.out.println(((LogootSRopes) alg3.getLDoc()).root.viewRec());
        /*LinkedList l1=browse(alg1);
         LinkedList l2=browse(alg2);
         LinkedList l3=browse(alg3);*/
        scoreChecks(alg1, alg2, alg3);
        assertEquals(alg1.lookup(), alg2.lookup());
        assertEquals(alg2.lookup(), alg3.lookup());

    }

    static LinkedList<RopesNodes> browse(LogootSAlgo alg) {
        RopesNodes node = ((LogootSRopes) alg.getLDoc()).root;
        LinkedList<RopesNodes> ret = new LinkedList<RopesNodes>();
        browsT(node, ret);
        return ret;
    }

    static void browsT(RopesNodes node, LinkedList<RopesNodes> list) {
        if (node == null) {
            return;
        }

        browsT(node.getLeft(), list);
        list.add(node);
        browsT(node.getRight(), list);
    }

    static void scoreCheck(LogootSAlgo alg) {
        RopesNodes node = ((LogootSRopes) alg.getLDoc()).root;
        LinkedList<RopesNodes> ret = new LinkedList<RopesNodes>();
        scoreCheckT(node, ret);
    }

    static void scoreCheckT(RopesNodes node, LinkedList<RopesNodes> list) {
        if (node == null) {
            return;
        }
        list.add(node);
        scoreCheckT(node.getLeft(), list);
        scoreCheckT(node.getRight(), list);
        int nodeinsub = 1 + node.getNodesInSubtree(0) + node.getNodesInSubtree(1);
        if (node.getNodesInSubtree() != nodeinsub) {
            System.err.println("error number node : " + node.getNodesInSubtree() + "<>" + nodeinsub + " " + list);
        }
        nodeinsub = node.str.size() + node.getSizeNodeAndChildren(0) + node.getSizeNodeAndChildren(1);
        if (node.getSizeNodeAndChildren() != nodeinsub) {
            System.err.println("error lenght : " + node.getSizeNodeAndChildren() + "<>" + nodeinsub + " " + list);
        }
        list.removeLast();

    }

    static void scoreChecks(LogootSAlgo... a) {
        for (int i = 0; i < a.length; i++) {

            System.err.println("\n\n==alg" + (i + 1) + "==\n");
            System.out.println(((LogootSRopes) a[i].getLDoc()).root.viewRec());
            scoreCheck(a[i]);
        }
    }

    @Test
    public void testRnd() throws PreconditionException {
        CRDTMessage op1 = alg1.insert(0, "test");
        CRDTMessage op2 = alg2.insert(0, "jklm");
        alg3.applyRemote(op2);
        alg3.applyRemote(op1);
        alg3.insert(4, "now");

        System.out.println(alg3.lookup());
        System.out.println("");
    }

    @Test
    public void testEmpty() {
        assertEquals("", alg1.lookup());
    }

    @Test
    public void testInsert() throws PreconditionException {
        String content = "abcdejk", c2 = "fghi";
        int pos = 3;
        alg1.applyLocal(SequenceOperation.insert(0, content));
        assertEquals(content, alg1.lookup());
        alg1.applyLocal(SequenceOperation.insert(pos, c2));
        assertEquals(content.substring(0, pos) + c2 + content.substring(pos), alg1.lookup());
    }

    @Test
    public void testDelete() throws PreconditionException {
        String content = "abcdefghijk";
        int pos = 3, off = 4;
        alg1.applyLocal(SequenceOperation.insert(0, content));
        assertEquals(content, alg1.lookup());
        alg1.applyLocal(SequenceOperation.delete(pos, off));
        assertEquals(content.substring(0, pos) + content.substring(pos + off), alg1.lookup());
    }

    @Test
    public void testConcurrentDelete() throws PreconditionException {
        String content = "abcdefghij";
        CRDTMessage m1 = alg1.applyLocal(SequenceOperation.insert(0, content));
        assertEquals(content, alg1.lookup());
        alg1.applyLocal(SequenceOperation.insert(2, "2"));
        assertEquals("ab2cdefghij", alg1.lookup());
        alg1.applyLocal(SequenceOperation.insert(7, "7"));
        assertEquals("ab2cdef7ghij", alg1.lookup());


        alg2.applyRemote(m1);
        assertEquals(content, alg2.lookup());
        CRDTMessage m2 = alg2.applyLocal(SequenceOperation.delete(1, 8));
        assertEquals("aj", alg2.lookup());
        alg1.applyRemote(m2);
        assertEquals("a27j", alg1.lookup());
    }

    @Test
    public void testUpdate() throws PreconditionException {
        String content = "abcdefghijk", upd = "xy";
        int pos = 3, off = 5;
        alg1.applyLocal(SequenceOperation.insert(0, content));
        assertEquals(content, alg1.lookup());
        alg1.applyLocal(SequenceOperation.replace(pos, off, upd));
        assertEquals(content.substring(0, pos) + upd + content.substring(pos + off), alg1.lookup());
    }

    static LogootSOpAdd extractOpAdd(CRDTMessage mess) {
        return (LogootSOpAdd) ((OperationBasedOneMessage) mess).getOperation();
    }

    static LogootSOpDel extractOpDel(CRDTMessage mess) {
        return (LogootSOpDel) ((OperationBasedOneMessage) mess).getOperation();
    }

    @Test
    public void testAppending() throws PreconditionException {
        CRDTMessage op1 = alg1.insert(0, "Test1234");
        CRDTMessage op2 = alg1.insert(8, "la suite");
        CRDTMessage op3 = alg1.insert(0, "before");

        assertEquals("beforeTest1234la suite", alg1.lookup());

        alg2.applyRemote(op2);
        alg2.applyRemote(op1);
        alg2.applyRemote(op3);

        //System.out.println(((LogootSRopes)alg2.getLDoc()).root.viewRec());
        assertEquals("beforeTest1234la suite", alg2.lookup());
        // System.out.println(alg1.lookup());
        assertEquals(1, ((LogootSRopes) alg1.getLDoc()).getMapBaseToBlock().size());


    }

    @Test
    public void testMede() {
        IdentifierInterval id1 = new IdentifierInterval(Arrays.asList(1, 0, -2), 0, 4);
        IdentifierInterval id2 = new IdentifierInterval(Arrays.asList(1, 0), -1, -1);
        assertEquals(id1.getEndId().compareTo(id2.getBeginId()), -1);
    }
    
    @Test
    public void testConflictAppendInside() throws PreconditionException {
        CRDTMessage m1=alg1.insert(0, "toto");
        CRDTMessage m2=alg1.insert(4, "hihi");
        LinkedList base = new LinkedList(extractOpAdd(m1).id.base);
        base.add(5);
        base.add(5);
        Identifier id =  new Identifier(base,6);
        CRDTMessage m3=new OperationBasedOneMessage(new LogootSOpAdd(id, Arrays.asList('k','o','k','o')));
        alg1.applyRemote(m3);
        assertEquals("totohikokohi",alg1.lookup());
        alg2.applyRemote(m1);
        alg2.applyRemote(m3);
        alg2.applyRemote(m2);
        assertEquals("totohikokohi",alg2.lookup());
        
    }

    @Test
    public void testConflictAppendB2concatB1() throws PreconditionException {
        CRDTMessage m0=alg1.insert(0, "yoyo");
        CRDTMessage m1=alg3.insert(0, "toto");
        CRDTMessage m2=alg3.insert(4, "hihi");
        LinkedList base = new LinkedList(extractOpAdd(m1).id.base);
        base.add(5);
        base.add(5);
        Identifier id =  new Identifier(base,6);
        CRDTMessage m3=new OperationBasedOneMessage(new LogootSOpAdd(id, Arrays.asList('k','o','k','o')));
        alg3.applyRemote(m3);
        alg3.applyRemote(m0);
        assertEquals("yoyototohikokohi",alg3.lookup());
        alg2.applyRemote(m0);
        alg2.applyRemote(m1);
        alg2.applyRemote(m3);
        alg2.applyRemote(m2);
        assertEquals("yoyototohikokohi",alg2.lookup());
        
    }
    @Test
    public void testConflictAppendB2concatB12() throws PreconditionException {
        CRDTMessage m0=alg1.insert(0, "yoyo");
        CRDTMessage m1=alg3.insert(0, "toto");
        CRDTMessage m2=alg3.insert(4, "hihi");
        LinkedList base = new LinkedList(extractOpAdd(m1).id.base);
        base.add(4);
        base.add(5);
        Identifier id =  new Identifier(base,6);
        CRDTMessage m3=new OperationBasedOneMessage(new LogootSOpAdd(id, Arrays.asList('k','o','k','o')));
        alg3.applyRemote(m3);
        alg3.applyRemote(m0);
        assertEquals("yoyototohkokoihi",alg3.lookup());
        alg2.applyRemote(m0);
        alg2.applyRemote(m1);
        alg2.applyRemote(m3);
        alg2.applyRemote(m2);
        assertEquals("yoyototohkokoihi",alg2.lookup());
        
    }
    @Test
    public void testGC() throws Exception {
        Trace trace = new RandomTrace(4200, RandomTrace.FLAT, new StandardSeqOpProfile(0.8, 0.5, 4, 5.0), 0.1, 10, 3.0, 13);
        CausalSimulator cd = new CausalSimulator(new LogootSplitOFactory(LogootSplitOFactory.TypeDoc.Ropes));
        cd.run(new ProgressTrace(trace,4200L));
        alg1 = (LogootSAlgo) cd.getReplicas().get(new Integer(1));
        alg2 = (LogootSAlgo) cd.getReplicas().get(new Integer(2));
        alg3 = (LogootSAlgo) cd.getReplicas().get(new Integer(3));
        assertTrue("Doc is empty", alg1.getLDoc().viewLength() > 0);
        CRDTMessage m1 = alg1.remove(0, alg1.getLDoc().viewLength());
        alg2.applyRemote(m1);
        alg3.applyRemote(m1);
        //System.out.println(alg1.lookup());
        assertEquals(0, alg1.getLDoc().viewLength());
        assertEquals(0, alg2.getLDoc().viewLength());
        assertEquals(0, alg3.getLDoc().viewLength());
        assertEquals(0, ((LogootSRopes) alg1.getLDoc()).getMapBaseToBlock().size());
        assertEquals(0, ((LogootSRopes) alg2.getLDoc()).getMapBaseToBlock().size());
        assertEquals(0, ((LogootSRopes) alg3.getLDoc()).getMapBaseToBlock().size());
        assertNull(((LogootSRopes) alg1.getLDoc()).root);
        assertNull(((LogootSRopes) alg2.getLDoc()).root);
        assertNull(((LogootSRopes) alg3.getLDoc()).root);

    }

    @Test
    public void simpleRotateTest() {
        LogootSRopes doc = (LogootSRopes) alg1.getDoc();

        LogootSRopes.RopesNodes T1 = new LogootSRopes.RopesNodes(Arrays.asList("T1"), 0, null);
        LogootSRopes.RopesNodes T2 = new LogootSRopes.RopesNodes(Arrays.asList("T2"), 0, null);
        LogootSRopes.RopesNodes T3 = new LogootSRopes.RopesNodes(Arrays.asList("T3"), 0, null);
        LogootSRopes.RopesNodes a = new LogootSRopes.RopesNodes(Arrays.asList("a"), 0, null);
        LogootSRopes.RopesNodes s = new LogootSRopes.RopesNodes(Arrays.asList("s"), 0, null);
        a.setLeft(s);
        a.setRight(T3);
        s.setLeft(T1);
        s.setRight(T2);
        doc.root = a;

        doc.rotateRight(a, null);
        assertTrue("bad root", doc.root == s);
        assertTrue("bad T1", s.getLeft() == T1);
        assertTrue("bad a", s.getRight() == a);
        assertTrue("bad T2", a.getLeft() == T2);
        assertTrue("bad T3", a.getRight() == T3);
        doc.rotateLeft(s, null);
        assertTrue("bad root", doc.root == a);
        assertTrue("bad T1", s.getLeft() == T1);
        assertTrue("bad T2", s.getRight() == T2);
        assertTrue("bad s", a.getLeft() == s);
        assertTrue("bad T3", a.getRight() == T3);
    }

    @Test
    public void simpleRotateNodeTest() {
        LogootSRopes doc = (LogootSRopes) alg1.getDoc();

        LogootSRopes.RopesNodes T1 = new LogootSRopes.RopesNodes(Arrays.asList("T1"), 0, null);
        LogootSRopes.RopesNodes T2 = new LogootSRopes.RopesNodes(Arrays.asList("T2"), 0, null);
        LogootSRopes.RopesNodes T3 = new LogootSRopes.RopesNodes(Arrays.asList("T3"), 0, null);
        LogootSRopes.RopesNodes a = new LogootSRopes.RopesNodes(Arrays.asList("a"), 0, null);
        LogootSRopes.RopesNodes s = new LogootSRopes.RopesNodes(Arrays.asList("s"), 0, null);
        LogootSRopes.RopesNodes b = new LogootSRopes.RopesNodes(Arrays.asList("b"), 0, null);
        a.setLeft(s);
        a.setRight(T3);
        s.setLeft(T1);
        s.setRight(T2);
        doc.root = b;
        b.setRight(a);

        doc.rotateRight(a, b);
        assertTrue("bad root", doc.root == b);
        assertTrue("bad s", b.getRight() == s);
        assertTrue("bad T1", s.getLeft() == T1);
        assertTrue("bad a", s.getRight() == a);
        assertTrue("bad T2", a.getLeft() == T2);
        assertTrue("bad T3", a.getRight() == T3);
        doc.rotateLeft(s, b);
        assertTrue("bad root", doc.root == b);
        assertTrue("bad a", b.getRight() == a);
        assertTrue("bad T1", s.getLeft() == T1);
        assertTrue("bad T2", s.getRight() == T2);
        assertTrue("bad s", a.getLeft() == s);
        assertTrue("bad T3", a.getRight() == T3);
    }

    @Test
    public void doubleRotateLTest() {
        LogootSRopes doc = (LogootSRopes) alg1.getDoc();
        LogootSRopes.RopesNodes T1 = new LogootSRopes.RopesNodes(Arrays.asList("T1"), 0, null);
        LogootSRopes.RopesNodes T2 = new LogootSRopes.RopesNodes(Arrays.asList("T2"), 0, null);
        LogootSRopes.RopesNodes T3 = new LogootSRopes.RopesNodes(Arrays.asList("T3"), 0, null);
        LogootSRopes.RopesNodes T4 = new LogootSRopes.RopesNodes(Arrays.asList("T4"), 0, null);
        LogootSRopes.RopesNodes a = new LogootSRopes.RopesNodes(Arrays.asList("a"), 0, null);
        LogootSRopes.RopesNodes b = new LogootSRopes.RopesNodes(Arrays.asList("b"), 0, null);
        LogootSRopes.RopesNodes s = new LogootSRopes.RopesNodes(Arrays.asList("s"), 0, null);
        a.setLeft(T1);
        a.setRight(s);
        s.setLeft(b);
        s.setRight(T4);
        b.setLeft(T2);
        b.setRight(T3);
        doc.root = a;
        doc.rotateRL(a, null);
        assertTrue("bad root", doc.root == b);
        assertTrue("bad a", b.getLeft() == a);
        assertTrue("bad s", b.getRight() == s);
        assertTrue("bad T1", a.getLeft() == T1);
        assertTrue("bad T2", a.getRight() == T2);
        assertTrue("bad T3", s.getLeft() == T3);
        assertTrue("bad T4", s.getRight() == T4);
    }

    @Test
    public void doubleRotateLNodeTest() {
        LogootSRopes doc = (LogootSRopes) alg1.getDoc();
        LogootSRopes.RopesNodes T1 = new LogootSRopes.RopesNodes(Arrays.asList("T1"), 0, null);
        LogootSRopes.RopesNodes T2 = new LogootSRopes.RopesNodes(Arrays.asList("T2"), 0, null);
        LogootSRopes.RopesNodes T3 = new LogootSRopes.RopesNodes(Arrays.asList("T3"), 0, null);
        LogootSRopes.RopesNodes T4 = new LogootSRopes.RopesNodes(Arrays.asList("T4"), 0, null);
        LogootSRopes.RopesNodes a = new LogootSRopes.RopesNodes(Arrays.asList("a"), 0, null);
        LogootSRopes.RopesNodes b = new LogootSRopes.RopesNodes(Arrays.asList("b"), 0, null);
        LogootSRopes.RopesNodes s = new LogootSRopes.RopesNodes(Arrays.asList("s"), 0, null);
        LogootSRopes.RopesNodes r = new LogootSRopes.RopesNodes(Arrays.asList("r"), 0, null);
        a.setLeft(T1);
        a.setRight(s);
        s.setLeft(b);
        s.setRight(T4);
        b.setLeft(T2);
        b.setRight(T3);
        doc.root = r;
        r.setRight(a);
//        System.out.println(doc.root);
        doc.rotateRL(a, r);
        //      System.out.println(doc.root);
        assertTrue("bad root", doc.root == r);
        assertTrue("bad r", r.getRight() == b);
        assertTrue("bad a", b.getLeft() == a);
        assertTrue("bad s", b.getRight() == s);
        assertTrue("bad T1", a.getLeft() == T1);
        assertTrue("bad T2", a.getRight() == T2);
        assertTrue("bad T3", s.getLeft() == T3);
        assertTrue("bad T4", s.getRight() == T4);
    }

    @Test
    public void doubleRotateRTest() {
        LogootSRopes doc = (LogootSRopes) alg1.getDoc();
        LogootSRopes.RopesNodes T1 = new LogootSRopes.RopesNodes(Arrays.asList("T1"), 0, null);
        LogootSRopes.RopesNodes T2 = new LogootSRopes.RopesNodes(Arrays.asList("T2"), 0, null);
        LogootSRopes.RopesNodes T3 = new LogootSRopes.RopesNodes(Arrays.asList("T3"), 0, null);
        LogootSRopes.RopesNodes T4 = new LogootSRopes.RopesNodes(Arrays.asList("T4"), 0, null);
        LogootSRopes.RopesNodes a = new LogootSRopes.RopesNodes(Arrays.asList("a"), 0, null);
        LogootSRopes.RopesNodes b = new LogootSRopes.RopesNodes(Arrays.asList("b"), 0, null);
        LogootSRopes.RopesNodes s = new LogootSRopes.RopesNodes(Arrays.asList("s"), 0, null);
        a.setLeft(s);
        a.setRight(T4);
        s.setLeft(T1);
        s.setRight(b);
        b.setLeft(T2);
        b.setRight(T3);
        doc.root = a;
        doc.rotateLR(a, null);
        assertTrue("bad root", doc.root == b);
        assertTrue("bad s", b.getLeft() == s);
        assertTrue("bad a", b.getRight() == a);
        assertTrue("bad T1", s.getLeft() == T1);
        assertTrue("bad T2", s.getRight() == T2);
        assertTrue("bad T3", a.getLeft() == T3);
        assertTrue("bad T4", a.getRight() == T4);
    }

    @Test
    public void doubleRotateRNodeTest() {
        LogootSRopes doc = (LogootSRopes) alg1.getDoc();
        LogootSRopes.RopesNodes T1 = new LogootSRopes.RopesNodes(Arrays.asList("T1"), 0, null);
        LogootSRopes.RopesNodes T2 = new LogootSRopes.RopesNodes(Arrays.asList("T2"), 0, null);
        LogootSRopes.RopesNodes T3 = new LogootSRopes.RopesNodes(Arrays.asList("T3"), 0, null);
        LogootSRopes.RopesNodes T4 = new LogootSRopes.RopesNodes(Arrays.asList("T4"), 0, null);
        LogootSRopes.RopesNodes a = new LogootSRopes.RopesNodes(Arrays.asList("a"), 0, null);
        LogootSRopes.RopesNodes b = new LogootSRopes.RopesNodes(Arrays.asList("b"), 0, null);
        LogootSRopes.RopesNodes s = new LogootSRopes.RopesNodes(Arrays.asList("s"), 0, null);
        LogootSRopes.RopesNodes r = new LogootSRopes.RopesNodes(Arrays.asList("r"), 0, null);
        a.setLeft(s);
        a.setRight(T4);
        s.setLeft(T1);
        s.setRight(b);
        b.setLeft(T2);
        b.setRight(T3);
        doc.root = r;
        r.setRight(a);
        doc.rotateLR(a, r);
        assertTrue("bad root", doc.root == r);
        assertTrue("bad b", r.getRight() == b);
        assertTrue("bad s", b.getLeft() == s);
        assertTrue("bad a", b.getRight() == a);
        assertTrue("bad T1", s.getLeft() == T1);
        assertTrue("bad T2", s.getRight() == T2);
        assertTrue("bad T3", a.getLeft() == T3);
        assertTrue("bad T4", a.getRight() == T4);
    }

    @Test
    public void splitTest() {
        LogootSRopes doc = (LogootSRopes) alg1.getDoc();
        LogootSRopes.RopesNodes a = new LogootSRopes.RopesNodes(Arrays.asList('a', 'b', 'c', 'd', 'e', 'f'), 0, null);
        doc.root = a;
        assertEquals("abcdef", alg1.lookup());
        a.split(3);

        assertEquals("[a, b, c]", a.str.toString());
        assertTrue("bad split", a.getRight() != null);
        assertTrue("bad split", a.getLeft() == null);
        assertTrue("bad split", a.getRight().getLeft() == null);
        assertTrue("bad split", a.getRight().getRight() == null);
        assertEquals("[d, e, f]", a.getRight().str.toString());
    }

    @Test
    public void splitTest2() {
        LogootSRopes doc = (LogootSRopes) alg1.getDoc();
        LogootSRopes.RopesNodes a = new LogootSRopes.RopesNodes(Arrays.asList('a', 'b', 'c', 'd', 'e', 'f'), 0, null);
        LogootSRopes.RopesNodes b = new LogootSRopes.RopesNodes(Arrays.asList('1', '2', '3', '4', '5', '6'), 0, null);
        doc.root = a;
        assertEquals("abcdef", alg1.lookup());
        a.split(3, b);

        assertEquals("[a, b, c]", a.str.toString());
        assertTrue("bad split", a.getRight() != null);
        assertTrue("bad split", a.getLeft() == null);
        assertTrue("bad split", a.getRight().getLeft() == b);
        assertTrue("bad split", a.getRight().getRight() == null);
        assertTrue("bad split", b.getLeft() == null);
        assertTrue("bad split", b.getRight() == null);

        assertEquals("[d, e, f]", a.getRight().str.toString());
        assertEquals("abc123456def", alg1.lookup());
    }
}
