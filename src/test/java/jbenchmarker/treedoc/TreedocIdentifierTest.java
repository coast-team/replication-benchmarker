package jbenchmarker.treedoc;

import static org.junit.Assert.*;
import jbenchmarker.treedoc.TreedocIdentifier.EdgeDirection;
import jbenchmarker.treedoc.TreedocIdentifier.Recorder;

import org.junit.Before;
import org.junit.Test;

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