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
package jbenchmarker.treedoc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Unique position identifier for Treedoc operations. Identifier is a sequence
 * of pairs: (direction, timestamp), indexed by integer [0, length()]. Direction
 * is {@link EdgeDirection#LEFT} or {@link EdgeDirection#RIGHT} and reflects
 * treedoc tree edges.
 * <p>
 * Identifiers are constructed and used by {@link TreedocRoot}.
 * 
 * @author mzawirski
 */
public class TreedocIdentifier implements Serializable {
	enum EdgeDirection {
		LEFT, RIGHT
	};

	/**
	 * Treedoc tree path recorder, used to instatiate identifiers.
	 * 
	 * @author mzawirski
	 */
	static class Recorder {
		private BitSet path = new BitSet();
		private ArrayList<Integer> tagChanges = new ArrayList<Integer>();
		private int index;

		public void recordEdge(final EdgeDirection direction, UniqueTag tag) {
			if (tag == null)
				tag = UniqueTag.MIN;
			path.set(index, direction == EdgeDirection.RIGHT);
			if (!equalsLastTag(tag))
				appendTagChange(tag);
			index++;
		}

		private boolean equalsLastTag(UniqueTag tag) {
			if (index == 0)
				return false;

			// Read using secret encoding;-) See appendTagChange().
			final int lastReplicaId = tagChanges.get(tagChanges.size() - 2);
			final int lastCounter = tagChanges.get(tagChanges.size() - 1);
			return lastReplicaId == tag.getReplicaId()
					&& lastCounter == tag.getCounter();
		}

		private void appendTagChange(final UniqueTag tag) {
			if (index > 0) {
				// Record the index of new tag.
				tagChanges.add(index);
			}
			tagChanges.add(tag.getReplicaId());
			tagChanges.add(tag.getCounter());
		}

		public TreedocIdentifier createIdentifier() {
			if (index == 0)
				throw new IllegalStateException(
						"Cannot create empty identifier");
			final int tags[] = new int[tagChanges.size()];
			for (int i = 0; i < tagChanges.size(); i++)
				tags[i] = tagChanges.get(i);
			return new TreedocIdentifier(path, tags, index);
		}
	}

	static class ComponentScanner {
		private EdgeDirection direction;
		private UniqueTag tag;

		public EdgeDirection getDirection() {
			return direction;
		}

		public UniqueTag getTag() {
			return tag;
		}
	}

	/**
	 * Directions on the path.
	 */
	private final BitSet path;
	/**
	 * UniqueTags on the path, encoded as sequence of (replicaId, counter,
	 * newTagIndex) triples, where newTagIndex is the index where the tag
	 * changes. Last triple is actually a pair without newTagIndex component.
	 */
	private final int tags[];
	private final int length;

	private TreedocIdentifier(final BitSet path, final int tags[],
			final int length) {
		this.path = path;
		this.tags = tags;
		this.length = length;
	}

	/**
	 * @return new iterator over the identifier components. Note that
	 *         ComponentScanner object is mutable between #next() calls.
	 */
	public Iterator<ComponentScanner> iterator() {
		return new ComponentIterator();
	}

	public int length() {
		return length;
	}

	/**
	 * Makes a deep copy of identifier.
	 */
	public TreedocIdentifier clone() {
		final int clonedTags[] = Arrays.copyOf(tags, tags.length);
		return new TreedocIdentifier((BitSet) path.clone(), clonedTags, length);
	}

	@Override
	public String toString() {
		final StringBuilder buf = new StringBuilder();
		final Iterator<ComponentScanner> iter = iterator();
		while (iter.hasNext()) {
			final ComponentScanner component = iter.next();
			buf.append(component.getDirection() == EdgeDirection.LEFT ? '0'
					: '1');
			buf.append(':');
			buf.append(component.getTag());
			buf.append("|");
		}
		// Shrink last "|".
		return buf.substring(0, buf.length() - 1);
	}

	private class ComponentIterator implements Iterator<ComponentScanner> {
		private ComponentScanner component = new ComponentScanner();
		private int index;
		private int tagIndex;

		@Override
		public ComponentScanner next() {
			if (!hasNext())
				throw new NoSuchElementException();
			component.direction = path.get(index) ? EdgeDirection.RIGHT
					: EdgeDirection.LEFT;
			// TODO: This code looks awful and depends on Recorder's code.
			// Refactor.
			if ((component.tag == null && tags[tagIndex] != UniqueTag.MIN
					.getReplicaId())
					|| (component.tag != null && (tags[tagIndex] != component.tag
							.getReplicaId() || tags[tagIndex + 1] != component.tag
							.getCounter()))) {
				if (tags[tagIndex] == UniqueTag.MIN.getReplicaId()
						&& tags[tagIndex + 1] == UniqueTag.MIN.getReplicaId()) {
					component.tag = null;
				} else {
					component.tag = new UniqueTag(tags[tagIndex],
							tags[tagIndex + 1]);
				}
			}
			index++;
			if (tagIndex + 2 < tags.length && index >= tags[tagIndex + 2])
				tagIndex += 3;
			return component;
		}

		@Override
		public boolean hasNext() {
			return index < length;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

}
