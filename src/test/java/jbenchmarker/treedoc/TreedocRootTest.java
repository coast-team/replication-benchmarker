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
	private TreedocRoot root;

	@Before
	public void setUp() throws Exception {
		root = new TreedocRoot(UniqueTag.createGenerator(0));
	}

	@Test
	public void testEmptyTree() {
		assertEquals("", root.getContent());
	}

	@Test
	public void testCreateSingle() {
		root.insertAt(0, "a");
		assertEquals(1, root.getSubtreeSize());
		assertEquals("a", root.getContent());
	}

	@Test
	public void testInsertAtBeginning() {
		root.insertAt(0, "b");
		root.insertAt(0, "a");
		assertEquals(2, root.getSubtreeSize());
		assertEquals("ab", root.getContent());
	}

	@Test
	public void testInsertAtEnd() {
		root.insertAt(0, "a");
		root.insertAt(1, "b");
		assertEquals(2, root.getSubtreeSize());
		assertEquals("ab", root.getContent());
	}

	@Test
	public void testInsertAtMiddle() {
		root.insertAt(0, "a");
		root.insertAt(1, "c");
		root.insertAt(1, "b");
		assertEquals("abc", root.getContent());
	}

	@Test
	public void testInsertsDeletesInterleaved() {
		root.insertAt(0, "a");
		root.insertAt(1, "b");
		root.insertAt(1, "c");
		root.deleteAt(1);
		root.insertAt(1, "e");
		root.deleteAt(0);
		root.insertAt(0, "d");
		root.deleteAt(2);
		root.insertAt(2, "f");
		assertEquals("def", root.getContent());
	}

	@Test
	public void testInsertsDeletesInserts() {
		root.insertAt(0, "a");
		root.deleteAt(0);
		root.insertAt(0, "b");
		root.insertAt(1, "c");
		root.insertAt(2, "d");
		assertEquals("bcd", root.getContent());
	}

	@Test
	public void test() {
		root.insertAt(0, "a");
		root.deleteAt(0);
		root.insertAt(0, "b");
	}
}
