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
package jbenchmarker.treedocReplace;

import jbenchmarker.treedocReplace.*;
import jbenchmarker.treedoc.UniqueTag;
import java.util.LinkedList;
import java.util.List;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

/**
 * Smoke tests for tree-based Treedoc implementation.
 * 
 * @author mzawirski
 */
public class TreedocReplaceRootTest {
	private static final int REPLICA_ID = 7;
	private TreedocRoot root;

	@Before
	public void setUp() throws Exception {
		root = new TreedocRoot(UniqueTag.createGenerator());
	}

	@Test
	public void testEmptyTree() {
		assertEquals("", root.getContent());
	}

        void insertAt(TreedocRoot root, int p, String s, int id) {
            List<Character> l = new LinkedList<Character>();
            for (int i = 0; i < s.length(); ++i) {
                l.add(s.charAt(i));
            }
            root.insertAt(p, l, id, false);
        }
        
	@Test
	public void testCreateSingle() {
		insertAt(root, 0, "a", REPLICA_ID);
		assertEquals(1, root.getSubtreeSize());
		assertEquals("a", root.getContent());
	}

	@Test
	public void testInsertAtBeginning() {
		insertAt(root, 0, "b", REPLICA_ID);
		insertAt(root, 0, "a", REPLICA_ID);
		assertEquals(2, root.getSubtreeSize());
		assertEquals("ab", root.getContent());
	}

	@Test
	public void testInsertAtEnd() {
		insertAt(root, 0, "a", REPLICA_ID);
		insertAt(root, 1, "b", REPLICA_ID);
		assertEquals(2, root.getSubtreeSize());
		assertEquals("ab", root.getContent());
	}

	@Test
	public void testInsertAtMiddle() {
		insertAt(root, 0, "a", REPLICA_ID);
		insertAt(root, 1, "c", REPLICA_ID);
		insertAt(root, 1, "b", REPLICA_ID);
		assertEquals("abc", root.getContent());
	}

	@Test
	public void testInsertsDeletesInterleaved() {
		insertAt(root, 0, "a", REPLICA_ID);
		insertAt(root, 1, "b", REPLICA_ID);
		insertAt(root, 1, "c", REPLICA_ID);
		root.deleteAt(1);
		insertAt(root, 1, "e", REPLICA_ID);
		root.deleteAt(0);
		insertAt(root, 0, "d", REPLICA_ID);
		root.deleteAt(2);
		insertAt(root, 2, "f", REPLICA_ID);
		assertEquals("def", root.getContent());
	}

	@Test
	public void testInsertsDeletesInserts() {
		insertAt(root, 0, "a", REPLICA_ID);
		root.deleteAt(0);
		insertAt(root, 0, "b", REPLICA_ID);
		insertAt(root, 1, "c", REPLICA_ID);
		insertAt(root, 2, "d", REPLICA_ID);
		assertEquals("bcd", root.getContent());
	}

	@Test
	public void test() {
		insertAt(root, 0, "a", REPLICA_ID);
		root.deleteAt(0);
		insertAt(root, 0, "b", REPLICA_ID);
	}
}
