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

import java.util.Arrays;

import org.junit.Test;

/**
 * 
 * @author mzawirski
 */
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

	@Test
	public void testGenerator() {
		final UniqueTagGenerator generator = UniqueTag.createGenerator(123);
		assertEquals(new UniqueTag(123, 0), generator.nextTag());
		assertEquals(new UniqueTag(123, 1), generator.nextTag());
	}
}
