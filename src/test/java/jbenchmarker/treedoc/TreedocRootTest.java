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
package jbenchmarker.treedoc;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

/**
 * Smoke tests for tree-based Treedoc implementation.
 * 
 * @author mzawirski
 */
public class TreedocRootTest {
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

	@Test
	public void testCreateSingle() {
		root.insertAt(0, "a", REPLICA_ID);
		assertEquals(1, root.getSubtreeSize());
		assertEquals("a", root.getContent());
	}

	@Test
	public void testInsertAtBeginning() {
		root.insertAt(0, "b", REPLICA_ID);
		root.insertAt(0, "a", REPLICA_ID);
		assertEquals(2, root.getSubtreeSize());
		assertEquals("ab", root.getContent());
	}

	@Test
	public void testInsertAtEnd() {
		root.insertAt(0, "a", REPLICA_ID);
		root.insertAt(1, "b", REPLICA_ID);
		assertEquals(2, root.getSubtreeSize());
		assertEquals("ab", root.getContent());
	}

	@Test
	public void testInsertAtMiddle() {
		root.insertAt(0, "a", REPLICA_ID);
		root.insertAt(1, "c", REPLICA_ID);
		root.insertAt(1, "b", REPLICA_ID);
		assertEquals("abc", root.getContent());
	}

	@Test
	public void testInsertsDeletesInterleaved() {
		root.insertAt(0, "a", REPLICA_ID);
		root.insertAt(1, "b", REPLICA_ID);
		root.insertAt(1, "c", REPLICA_ID);
		root.deleteAt(1);
		root.insertAt(1, "e", REPLICA_ID);
		root.deleteAt(0);
		root.insertAt(0, "d", REPLICA_ID);
		root.deleteAt(2);
		root.insertAt(2, "f", REPLICA_ID);
		assertEquals("def", root.getContent());
	}

	@Test
	public void testInsertsDeletesInserts() {
		root.insertAt(0, "a", REPLICA_ID);
		root.deleteAt(0);
		root.insertAt(0, "b", REPLICA_ID);
		root.insertAt(1, "c", REPLICA_ID);
		root.insertAt(2, "d", REPLICA_ID);
		assertEquals("bcd", root.getContent());
	}

	@Test
	public void test() {
		root.insertAt(0, "a", REPLICA_ID);
		root.deleteAt(0);
		root.insertAt(0, "b", REPLICA_ID);
	}
}
