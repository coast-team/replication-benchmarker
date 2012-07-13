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
package jbenchmarker.treedoc;

import static org.junit.Assert.*;

import java.util.Iterator;
import java.util.NoSuchElementException;

import jbenchmarker.treedoc.TreedocIdentifier.ComponentScanner;
import jbenchmarker.treedoc.TreedocIdentifier.EdgeDirection;
import jbenchmarker.treedoc.TreedocIdentifier.Recorder;

import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author mzawirski
 */
public class TreedocIdentifierTest {

	private Recorder idRecorder;

	@Before
	public void setUp() {
		idRecorder = new TreedocIdentifier.Recorder();
	}

	@Test
	public void testLength1() {
		final UniqueTag tag = new UniqueTag(0, 0);
		idRecorder.recordEdge(EdgeDirection.LEFT, tag);
		final TreedocIdentifier id = idRecorder.createIdentifier();
		final Iterator<ComponentScanner> iter = id.iterator();

		assertEquals(1, id.length());
		assertTrue(iter.hasNext());

		final ComponentScanner component = iter.next();
		assertEquals(EdgeDirection.LEFT, component.getDirection());
		assertEquals(tag, component.getTag());
		assertFalse(iter.hasNext());

		try {
			iter.next();
			fail();
		} catch (NoSuchElementException x) {
			// expected
		}
	}

	@Test
	public void testLengthyWithNull() {
		final UniqueTag tag0 = new UniqueTag(0, 0);
		idRecorder.recordEdge(EdgeDirection.LEFT, tag0);
		final UniqueTag tag1 = null;
		idRecorder.recordEdge(EdgeDirection.LEFT, tag1);
		final UniqueTag tag2 = new UniqueTag(1, 5);
		idRecorder.recordEdge(EdgeDirection.RIGHT, tag2);
		final TreedocIdentifier id = idRecorder.createIdentifier();

		assertEquals(3, id.length());

		final Iterator<ComponentScanner> iter = id.iterator();
		assertTrue(iter.hasNext());
		ComponentScanner component = iter.next();
		assertEquals(EdgeDirection.LEFT, component.getDirection());
		assertEquals(tag0, component.getTag());
		assertTrue(iter.hasNext());
		component = iter.next();
		assertEquals(EdgeDirection.LEFT, component.getDirection());
		assertEquals(tag1, component.getTag());
		assertTrue(iter.hasNext());
		component = iter.next();
		assertEquals(EdgeDirection.RIGHT, component.getDirection());
		assertEquals(tag2, component.getTag());
		assertFalse(iter.hasNext());
	}

	@Test
	public void testWithRepeatitions() {
		final UniqueTag tag01 = new UniqueTag(0, 0);
		idRecorder.recordEdge(EdgeDirection.LEFT, tag01);
		idRecorder.recordEdge(EdgeDirection.RIGHT, tag01);
		final UniqueTag tag234 = null;
		idRecorder.recordEdge(EdgeDirection.LEFT, tag234);
		idRecorder.recordEdge(EdgeDirection.RIGHT, tag234);
		idRecorder.recordEdge(EdgeDirection.RIGHT, tag234);
		final UniqueTag tag5 = new UniqueTag(1, 5);
		idRecorder.recordEdge(EdgeDirection.RIGHT, tag5);
		final TreedocIdentifier id = idRecorder.createIdentifier();

		assertEquals(6, id.length());

		final Iterator<ComponentScanner> iter = id.iterator();

		assertTrue(iter.hasNext());
		ComponentScanner component = iter.next();
		assertEquals(EdgeDirection.LEFT, component.getDirection());
		assertEquals(tag01, component.getTag());

		assertTrue(iter.hasNext());
		iter.next();
		assertEquals(EdgeDirection.RIGHT, component.getDirection());
		assertEquals(tag01, component.getTag());

		assertTrue(iter.hasNext());
		component = iter.next();
		assertEquals(EdgeDirection.LEFT, component.getDirection());
		assertEquals(tag234, component.getTag());

		assertTrue(iter.hasNext());
		component = iter.next();
		assertEquals(EdgeDirection.RIGHT, component.getDirection());
		assertEquals(tag234, component.getTag());

		assertTrue(iter.hasNext());
		component = iter.next();
		assertEquals(EdgeDirection.RIGHT, component.getDirection());
		assertEquals(tag234, component.getTag());

		assertTrue(iter.hasNext());
		component = iter.next();
		assertEquals(EdgeDirection.RIGHT, component.getDirection());
		assertEquals(tag5, component.getTag());
		assertFalse(iter.hasNext());
	}

	@Test
	public void testStartsWithNull() {
		final UniqueTag tag0 = null;
		idRecorder.recordEdge(EdgeDirection.LEFT, tag0);
		final UniqueTag tag1 = new UniqueTag(1, 5);
		idRecorder.recordEdge(EdgeDirection.RIGHT, tag1);
		final TreedocIdentifier id = idRecorder.createIdentifier();

		assertEquals(2, id.length());

		final Iterator<ComponentScanner> iter = id.iterator();
		assertTrue(iter.hasNext());
		ComponentScanner component = iter.next();
		assertEquals(EdgeDirection.LEFT, component.getDirection());
		assertEquals(tag0, component.getTag());

		assertTrue(iter.hasNext());
		iter.next();
		assertEquals(EdgeDirection.RIGHT, component.getDirection());
		assertEquals(tag1, component.getTag());
	}
}
