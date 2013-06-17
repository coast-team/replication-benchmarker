/**
 * Replication Benchmarker
 * https://github.com/score-team/replication-benchmarker/ Copyright (C) 2013
 * LORIA / Inria / SCORE Team
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
package jbenchmarker.treedocReplace;

import crdt.PreconditionException;
import java.util.ArrayList;
import java.util.List;
import jbenchmarker.core.SequenceOperation;
import jbenchmarker.factories.TreedocReplaceFactory;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

/**
 * Basic tests for tree-based Treedoc implementation.
 *
 * @author urso
 */
public class TreedocReplaceMergeTest {

    private static final int REPLICA_ID = 7;
    private TreedocRepMerge replica;

    @Before
    public void setUp() throws Exception {
        replica = (TreedocRepMerge) new TreedocReplaceFactory().create(REPLICA_ID);
    }

    @Test
    public void testInsert() throws PreconditionException {
        String content = "abcdejk", c2 = "fghi";
        int pos = 3;
        replica.applyLocal(SequenceOperation.insert(0, content));
        assertEquals(content, replica.lookup());
        replica.applyLocal(SequenceOperation.insert(pos, c2));
        assertEquals(content.substring(0, pos) + c2 + content.substring(pos), replica.lookup());
    }

    @Test
    public void testEmptyTree() {
        assertEquals("", replica.lookup());
    }

    @Test
    public void testDelete() throws PreconditionException {
        String content = "abcdefghijk";
        int pos = 3, off = 4;
        replica.applyLocal(SequenceOperation.insert(0, content));
        assertEquals(content, replica.lookup());
        replica.applyLocal(SequenceOperation.delete(pos, off));
        assertEquals(content.substring(0, pos) + content.substring(pos + off), replica.lookup());
    }

    @Test
    public void testUpdate() throws PreconditionException {
        String content = "abcdefghijk", upd = "xy";
        int pos = 3, off = 5;
        replica.applyLocal(SequenceOperation.insert(0, content));
        assertEquals(content, replica.lookup());
        replica.applyLocal(SequenceOperation.replace(pos, off, upd));
        assertEquals(content.substring(0, pos) + upd + content.substring(pos + off), replica.lookup());
    }

    @Test
    public void testUpdate2() throws PreconditionException {
        String ligne1 = "aaa", ligne2 = "bbb", ligne3 = "ccc", upd1 = "xxx", upd2 = "yyy";
        replica.applyLocal(SequenceOperation.insert(0, ligne1));
        replica.applyLocal(SequenceOperation.insert(replica.lookup().length(), ligne3));
        replica.applyLocal(SequenceOperation.insert(3, ligne2));
        assertEquals("aaabbbccc", replica.lookup());
        int pos = 3;
        List<String> list = new ArrayList();
        list.add(upd1);
        //SequenceOperation opt= SequenceOperation.replace(pos, list.size(), list);
        SequenceOperation opt = SequenceOperation.replace(pos, 3, upd1);
        replica.applyLocal(opt);
        assertEquals("aaaxxxccc", replica.lookup());
    }
}