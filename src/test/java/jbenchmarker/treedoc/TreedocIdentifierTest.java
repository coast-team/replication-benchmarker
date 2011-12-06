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

		assertEquals(1, id.length());
		assertEquals(EdgeDirection.LEFT, id.getEdgeDirection(0));
		assertEquals(tag, id.getUniqueTag(0));
	}

	@Test
	public void testLengthy() {
		final UniqueTag tag0 = new UniqueTag(0, 0);
		idRecorder.recordEdge(EdgeDirection.LEFT, tag0);
		final UniqueTag tag1 = new UniqueTag(1, 5);
		idRecorder.recordEdge(EdgeDirection.RIGHT, tag1);
		final TreedocIdentifier id = idRecorder.createIdentifier();

		assertEquals(2, id.length());
		assertEquals(EdgeDirection.LEFT, id.getEdgeDirection(0));
		assertEquals(tag0, id.getUniqueTag(0));
		assertEquals(EdgeDirection.RIGHT, id.getEdgeDirection(1));
		assertEquals(tag1, id.getUniqueTag(1));
	}
}