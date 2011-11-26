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

import java.util.ArrayList;
import java.util.BitSet;

/**
 * Unique position identifier for Treedoc operations. Identifier is a sequence
 * of pairs: (direction, timestamp), indexed by integer [0, length()]. Direction
 * is {@link EdgeDirection#LEFT} or {@link EdgeDirection#RIGHT} and reflects
 * treedoc tree edges.
 * 
 * @author mzawirski
 */
class TreedocIdentifier {
	enum EdgeDirection {
		LEFT, RIGHT
	};

	/**
	 * Treedoc tree path recorder, used to instatiate identifiers.
	 * 
	 * @author mzawirski
	 */
	public static class Recorder {
		private BitSet path = new BitSet();
		private ArrayList<UniqueTag> tags = new ArrayList<UniqueTag>();
		private int index;

		public void recordEdge(final EdgeDirection direction,
				final UniqueTag tag) {
			path.set(index++, direction == EdgeDirection.RIGHT);
			tags.add(tag);
		}

		public TreedocIdentifier createIdentifier() {
			if (index == 0)
				throw new IllegalStateException(
						"Cannot create empty identifier");
			return new TreedocIdentifier(path, tags);
		}
	}

	private BitSet path;
	private ArrayList<UniqueTag> tags;

	private TreedocIdentifier(final BitSet path, final ArrayList<UniqueTag> tags) {
		this.path = path;
		this.tags = tags;
	}

	public int length() {
		return tags.size();
	}

	public EdgeDirection getEdgeDirection(final int index) {
		if (path.get(index))
			return EdgeDirection.RIGHT;
		return EdgeDirection.LEFT;
	}

	public UniqueTag getUniqueTag(final int index) {
		return tags.get(index);
	}

	/**
	 * Makes a deep copy of identifier.
	 */
	public TreedocIdentifier clone() {
		final ArrayList<UniqueTag> clonedTags = new ArrayList<UniqueTag>(
				tags.size());
		for (final UniqueTag tag : tags)
			clonedTags.add(tag.clone());
		return new TreedocIdentifier(path, clonedTags);
	}
}
