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
package crdt.tree.wordtree;

import collect.HashTree;
import collect.Node;
import collect.Tree;
import collect.UnorderedNode;
import crdt.CRDTMessage;
import crdt.Factory;
import crdt.PreconditionException;
import crdt.set.CRDTSet;
import crdt.set.counter.CommutativeCounterSet;
import crdt.set.counter.ConvergentCounterSet;
import crdt.set.lastwriterwins.CommutativeLwwSet;
import crdt.set.lastwriterwins.ConvergentLwwSet;
import crdt.set.observedremove.CommutativeOrSet;
import crdt.set.observedremove.ConvergentOrSet;
import crdt.tree.CRDTTree;
import crdt.tree.CrdtTreeGeneric;
import crdt.tree.wordtree.policy.WordCompact;
import crdt.tree.wordtree.policy.WordIncrementalCompact;
import crdt.tree.wordtree.policy.WordIncrementalReappear;
import crdt.tree.wordtree.policy.WordIncrementalRoot;
import crdt.tree.wordtree.policy.WordIncrementalSkip;
import crdt.tree.wordtree.policy.WordIncrementalSkipOpti;
import crdt.tree.wordtree.policy.WordReappear;
import crdt.tree.wordtree.policy.WordRoot;
import crdt.tree.wordtree.policy.WordSkip;
import jbenchmarker.ot.otset.AddWinTransformation;
import jbenchmarker.ot.otset.DelWinTransformation;
import jbenchmarker.ot.otset.OTSet;
import jbenchmarker.ot.soct2.SOCT2;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author urso
 */
public class WordTreeTest {

    /**
     *
     */
    public WordTreeTest() {
    }

    /**
     *
     * @throws Exception
     */
    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    /**
     *
     * @throws Exception
     */
    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    /**
     *
     * @throws Exception
     */
    @Test(expected = PreconditionException.class)
    public void testNotIn() throws Exception {
        WordTree t = new WordTree(new CommutativeCounterSet(), new WordSkip());
        t.add(t.getRoot(), 'a');
        UnorderedNode a = t.getRoot().getChild('a');
        t.remove(a);
        t.add(a, 'b');
    }

    /**
     * Create test for wordtree
     *
     * @param sf setFactory
     * @throws Exception
     */
    public void testWordBasic(Factory<CRDTSet> sf) throws Exception {
        CrdtTreeGeneric test = new CrdtTreeGeneric();
        test.runAllBasic(new WordTree(sf.create(), new WordSkip()));
        test.runAllBasic(new WordTree(sf.create(), new WordIncrementalSkip()));
        test.runAllBasic(new WordTree(sf.create(), new WordIncrementalSkipOpti()));
        test.runAllBasic(new WordTree(sf.create(), new WordReappear()));
        test.runAllBasic(new WordTree(sf.create(), new WordIncrementalReappear()));
        test.runAllBasic(new WordTree(sf.create(), new WordRoot()));
        test.runAllBasic(new WordTree(sf.create(), new WordIncrementalRoot()));
        test.runAllBasic(new WordTree(sf.create(), new WordCompact()));
        test.runAllBasic(new WordTree(sf.create(), new WordIncrementalCompact()));
        testRoot(sf, new WordRoot());
        testRootDouble(sf, new WordRoot());
//        testRoot(sf, new WordIncrementalRoot());
//        testRootDouble(sf, new WordIncrementalRoot());
        testCompact(sf, new WordCompact());
        testCompact(sf, new WordIncrementalCompact());
    }

    /**
     *
     * @throws Exception
     */
    @Test
    public void testCmCWS() throws Exception {
        Tree tr, resultTree;
        Node a, b, c, x, y, z, k;
        CrdtTreeGeneric test = new CrdtTreeGeneric();
        Factory<CRDTTree> tf = new WordTree(new CommutativeCounterSet(), new WordSkip());
        test.testAdopt(tf.create(), tf.create());

        tr = test.testConcurAddRmvFather(tf.create(), tf.create());
        resultTree = new HashTree();
        a = resultTree.add(null, 'a');
        c = resultTree.add(null, 'c');
        z = resultTree.add(c, 'z');

        assertEquals(resultTree, tr);

        tr = test.testConcurAddRmvSameElement(tf.create(), tf.create());
        resultTree = new HashTree();
        a = resultTree.add(null, 'a');
        b = resultTree.add(null, 'b');
        c = resultTree.add(null, 'c');
        x = resultTree.add(b, 'x');
        y = resultTree.add(b, 'y');
        z = resultTree.add(c, 'z');

        assertEquals(resultTree, tr);

        tr = test.testTwoPath(tf.create(), tf.create());
        resultTree = new HashTree();
        a = resultTree.add(null, 'a');
        b = resultTree.add(null, 'b');
        c = resultTree.add(null, 'c');
        x = resultTree.add(a, 'x');
        y = resultTree.add(b, 'y');
        z = resultTree.add(c, 'z');
        resultTree.add(y, 'x');

        assertEquals(resultTree, tr);

        tr = test.testCycle(tf.create(), tf.create());
        resultTree = new HashTree();
        a = resultTree.add(null, 'a');
        b = resultTree.add(null, 'b');
        c = resultTree.add(null, 'c');
        x = resultTree.add(b, 'x');
        y = resultTree.add(b, 'y');
        resultTree.add(x, 'y');
        resultTree.add(y, 'x');

        assertEquals(resultTree, tr);
    }

    /**
     *
     * @throws Exception
     */
    @Test
    public void testCmOWS() throws Exception {
        Tree tr, resultTree;
        Node a, b, c, x, y, z, k;
        CrdtTreeGeneric test = new CrdtTreeGeneric();
        Factory<CRDTTree> tf = new WordTree(new CommutativeOrSet(), new WordSkip());
        test.testAdopt(tf.create(), tf.create());

        tr = test.testConcurAddRmvFather(tf.create(), tf.create());
        resultTree = new HashTree();
        a = resultTree.add(null, 'a');
        c = resultTree.add(null, 'c');
        z = resultTree.add(c, 'z');

        assertEquals(resultTree, tr);

        tr = test.testConcurAddRmvSameElement(tf.create(), tf.create());
        resultTree = new HashTree();
        a = resultTree.add(null, 'a');
        b = resultTree.add(null, 'b');
        c = resultTree.add(null, 'c');
        x = resultTree.add(b, 'x');
        y = resultTree.add(b, 'y');
        z = resultTree.add(c, 'z');

        assertEquals(resultTree, tr);

        tr = test.testTwoPath(tf.create(), tf.create());
        resultTree = new HashTree();
        a = resultTree.add(null, 'a');
        b = resultTree.add(null, 'b');
        c = resultTree.add(null, 'c');
        x = resultTree.add(a, 'x');
        y = resultTree.add(b, 'y');
        z = resultTree.add(c, 'z');
        resultTree.add(y, 'x');

        assertEquals(resultTree, tr);

        tr = test.testCycle(tf.create(), tf.create());
        resultTree = new HashTree();
        a = resultTree.add(null, 'a');
        b = resultTree.add(null, 'b');
        c = resultTree.add(null, 'c');
        x = resultTree.add(b, 'x');
        y = resultTree.add(b, 'y');
        resultTree.add(x, 'y');
        resultTree.add(y, 'x');

        assertEquals(resultTree, tr);
    }

    /*
     * a = resultTree.innerAdd(null, 'a'); b = resultTree.innerAdd(null, 'b'); c
     * = resultTree.innerAdd(null, 'c'); x = resultTree.innerAdd(b, 'x'); y =
     * resultTree.innerAdd(b, 'y'); z = resultTree.innerAdd(c, 'z');
     */
    /**
     *
     * @throws Exception
     */
    @Test
    public void testCmCWR() throws Exception {
        Tree tr, resultTree;
        Node a, b, c, x, y, z, k;
        CrdtTreeGeneric test = new CrdtTreeGeneric();
        CRDTSet s = new CommutativeCounterSet();
        Factory<CRDTTree> tf = new WordTree(s, new WordReappear());

        tr = test.testConcurAddRmvFather(tf.create(), tf.create());
        resultTree = new HashTree();

        a = resultTree.add(null, 'a');
        b = resultTree.add(null, 'b');
        c = resultTree.add(null, 'c');
        x = resultTree.add(b, 'x');
        k = resultTree.add(x, 'k');
        z = resultTree.add(c, 'z');

        assertEquals(resultTree, tr);
    }

    /**
     *
     * @throws Exception
     */
    @Test
    public void testCmLWR() throws Exception {
        Tree tr, resultTree;
        Node a, b, c, x, y, z, k;
        CrdtTreeGeneric test = new CrdtTreeGeneric();
        Factory<CRDTTree> tf = new WordTree(new CommutativeLwwSet(), new WordReappear());

        tr = test.testConcurAddRmvSameElement(tf.create(), tf.create());
        resultTree = new HashTree();

        a = resultTree.add(null, 'a');
        b = resultTree.add(null, 'b');
        c = resultTree.add(null, 'c');
//        x = resultTree.innerAdd(b, 'x');
        y = resultTree.add(b, 'y');
        z = resultTree.add(c, 'z');

        assertEquals(resultTree, tr);
    }

    /**
     *
     * @throws Exception
     */
    @Test
    public void testBasicCmCounter() throws Exception {
        testWordBasic(new CommutativeCounterSet());
    }

    /**
     *
     * @throws Exception
     */
    @Test
    public void testBasicCvCounter() throws Exception {
        testWordBasic(new ConvergentCounterSet());
    }

    /**
     *
     * @throws Exception
     */
    @Test
    public void testBasicCmLww() throws Exception {
        testWordBasic(new CommutativeLwwSet());
    }

    /**
     *
     * @throws Exception
     */
    @Test
    public void testBasicCvLww() throws Exception {
        testWordBasic(new ConvergentLwwSet());
    }

    /**
     *
     * @throws Exception
     */
    @Test
    public void testBasicCmOr() throws Exception {
        testWordBasic(new CommutativeOrSet());
    }

    /**
     *
     * @throws Exception
     */
    @Test
    public void testBasicCvOr() throws Exception {
        testWordBasic(new ConvergentOrSet());
    }

    /**
     * test for wordTree with set OT with soct2 and addwin policy
     *
     * @throws Exception
     */
    @Test
    public void testBasicOtAddWin() throws Exception {
        testWordBasic(new OTSet(new SOCT2(new AddWinTransformation(), null)));
    }

    /**
     * test for wordTree with set OT with soct2 and delwin policy
     *
     * @throws Exception
     */
    @Test
    public void testBasicOtDelWin() throws Exception {
        testWordBasic(new OTSet(new SOCT2(new DelWinTransformation(), null)));
    }

    private void testRoot(Factory<CRDTSet> sf, Factory<WordPolicy> rf) throws PreconditionException {
        WordTree wt1 = new WordTree(sf.create(), rf.create()),
                wt2 = new WordTree(sf.create(), rf.create());
        CRDTMessage m1 = wt1.add(wt1.getRoot(), 'a');
        m1=m1.concat(wt1.add(wt1.getRoot(), 'c'));
        m1=m1.concat(wt1.add(wt1.getRoot().getChild('a'), 'b'));
        wt2.applyRemote(m1);

        CRDTMessage m2 = wt2.remove(wt2.getRoot().getChild('a').getChild('b'));
        m1 = wt1.add(wt1.getRoot().getChild('a').getChild('b'), 'a');
        wt1.applyRemote(m2);
        wt2.applyRemote(m1);

        Tree rt = new HashTree();
        rt.add(null, 'a');
        rt.add(null, 'c');
        assertEquals(rt, wt1.lookup());
        assertEquals(rt, wt2.lookup());

        m1 = wt1.remove(wt1.getRoot().getChild('a'));
        wt2.applyRemote(m1);
        rt = new HashTree();
        rt.add(null, 'c');
        assertEquals(rt, wt1.lookup());
        assertEquals(rt, wt2.lookup());
    }

    private void testRootDouble(Factory<CRDTSet> sf, Factory<WordPolicy> rf) throws PreconditionException {
        WordTree wt1 = new WordTree(sf.create(), new WordRoot()),
                wt2 = new WordTree(sf.create(), new WordRoot());
        CRDTMessage m1 = wt1.add(wt1.getRoot(), 'a');
        m1=m1.concat(wt1.add(wt1.getRoot(), 'c'));
        m1=m1.concat(wt1.add(wt1.getRoot().getChild('a'), 'b'));
        wt2.applyRemote(m1);

        CRDTMessage m2 = wt2.remove(wt2.getRoot().getChild('a').getChild('b'));
        m1 = wt1.add(wt1.getRoot().getChild('a').getChild('b'), 'a');
        wt1.applyRemote(m2);
        wt2.applyRemote(m1);

        m1 = wt1.add(wt1.getRoot().getChild('a'), 'd');
        m2 = wt2.add(wt2.getRoot().getChild('a'), 'b');
        wt1.applyRemote(m2);
        wt2.applyRemote(m1);

        Tree rt = new HashTree();
        Node a = rt.add(null, 'a'),
                a2 = rt.add(rt.add(a, 'b'), 'a');
        rt.add(null, 'c');
        rt.add(a, 'd');
        rt.add(a2, 'd');
        rt.add(a2, 'b');
        assertEquals(rt, wt1.lookup());
        assertEquals(rt, wt2.lookup());
    }

    private void testCompact(Factory<CRDTSet> sf, Factory<WordPolicy> cf) throws PreconditionException {
        WordTree wt1 = new WordTree(sf.create(), cf.create()),
                wt2 = new WordTree(sf.create(), cf.create());
        CRDTMessage m1 = wt1.add(wt1.getRoot(), 'a');
        m1=m1.concat(wt1.add(wt1.getRoot(), 'c'));
        m1=m1.concat(wt1.add(wt1.getRoot().getChild('a'), 'a'));
        m1=m1.concat(wt1.add(wt1.getRoot().getChild('a'), 'b'));
        wt2.applyRemote(m1);

        CRDTMessage m2 = wt2.remove(wt2.getRoot().getChild('a').getChild('b'));
        m1 = wt1.add(wt1.getRoot().getChild('a').getChild('b'), 'a');
        wt1.applyRemote(m2);
        wt2.applyRemote(m1);

        Tree rt = new HashTree();
        rt.add(rt.add(null, 'a'), 'a');
        rt.add(null, 'c');
        assertEquals(rt, wt1.lookup());
        assertEquals(rt, wt2.lookup());

        m1 = wt1.remove(wt1.getRoot().getChild('a').getChild('a'));
        wt2.applyRemote(m1);
        rt = new HashTree();
        rt.add(null, 'a');
        rt.add(null, 'c');
        assertEquals(rt, wt1.lookup());
        assertEquals(rt, wt2.lookup());
    }
}
