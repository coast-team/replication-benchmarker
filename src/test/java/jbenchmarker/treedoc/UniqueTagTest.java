package jbenchmarker.treedoc;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

public class UniqueTagTest {

	@Test
	public void testVariousMixture() {
		final UniqueTag tag02 = new UniqueTag(0, 2);
		final UniqueTag tag01 = new UniqueTag(0, 1);
		final UniqueTag tag23 = new UniqueTag(2, 3);
		final UniqueTag tag20 = new UniqueTag(2, 0);
		final UniqueTag tags[] = new UniqueTag[] { tag02, tag01, tag23, tag20 };
		Arrays.sort(tags);

		assertArrayEquals(new UniqueTag[] { tag01, tag02, tag20, tag23 }, tags);
	}

	@Test
	public void testEquals() {
		final UniqueTag tagA = new UniqueTag(0, 2);
		final UniqueTag tagB = new UniqueTag(0, 2);

		assertEquals(0, tagA.compareTo(tagB));
	}
}
