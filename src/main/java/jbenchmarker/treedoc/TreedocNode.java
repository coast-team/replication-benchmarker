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

import jbenchmarker.treedoc.TreedocIdentifier.EdgeDirection;
import jbenchmarker.treedoc.TreedocIdentifier.Recorder;

/**
 * A node of of Treedoc binary-like tree. Each node have (optionally) left and
 * right children. The infix traversal order specifies document content.
 * <p>
 * Only the root node of this class (created by {@link #createRoot()} is meant
 * for public access.
 * <p>
 * Thread-hostile.
 * 
 * @author mzawirski
 */
class TreedocNode {
	public static TreedocNode createRoot() {
		return new TreedocNode(null, (char) 0, true);
	}

	private static int binarySearchByTag(final TreedocNode[] nodes,
			final UniqueTag tag) {
		// Based on code from Arrays.binarySearch, since rewrting binary search
		// differently does not make sense. I do not expect copyright trobules.
		int low = 0;
		int high = nodes.length - 1;
		while (low <= high) {
			int mid = (low + high) >>> 1;
			final UniqueTag midTag = nodes[mid].uniqueTag;
			int cmp = midTag.compareTo(tag);

			if (cmp < 0)
				low = mid + 1;
			else if (cmp > 0)
				high = mid - 1;
			else
				return mid;
		}
		return -(low + 1);
	}

	private static void getNodesContent(final TreedocNode nodes[],
			final StringBuilder contentBuffer) {
		if (nodes == null)
			return;
		for (TreedocNode child : nodes) {
			if (child.getSubtreeSize() > 0)
				child.getSubtreeContent(contentBuffer);
		}
	}

	private UniqueTag uniqueTag;
	private char content;
	private int subtreeSize;
	private boolean tombstone;
	private TreedocNode leftChildren[];
	private TreedocNode rightChildren[];

	private TreedocNode(final UniqueTag uniqueTag, final char content,
			final boolean tombstone) {
		this.uniqueTag = uniqueTag;
		this.content = content;
		this.tombstone = tombstone;
		if (!tombstone)
			this.subtreeSize = 1;
	}

	public void insertAt(final TreedocIdentifier id, final char content) {
		final int lastIndex = id.length() - 1;
		final TreedocNode newNodeParent = findNodeAndAlterSize(id, 0,
				lastIndex, 1);
		final UniqueTag lastTag = id.getUniqueTag(lastIndex);
		final EdgeDirection lastDirection = id.getEdgeDirection(lastIndex);
		final TreedocNode newNode = new TreedocNode(lastTag, content,
				false);
		newNodeParent.insertChild(newNode, lastDirection);
	}

	public void deleteAt(final TreedocIdentifier id) {
		final TreedocNode node = findNodeAndAlterSize(id, 0, id.length(),
				-1);
		if (node.tombstone)
			throw new IllegalStateException(
					"State corrupted, content already deleted");
		node.tombstone = true;
	}

	public TreedocIdentifier insertAt(final int index, final char content,
			final UniqueTag tag) {
		final TreedocNode newNode = new TreedocNode(tag, content, false);
		final Recorder idRecorder = new Recorder();
		final TreedocNode precedingNode;
		if (index == 0) {
			// Assumption: this is the root of the tree.
			precedingNode = this;
		} else {
			precedingNode = findNthContentAndAlterSize(index, idRecorder, 1);
		}
		precedingNode.insertAfter(newNode, idRecorder);
		return idRecorder.createIdentifier();
	}

	public TreedocIdentifier deleteAt(final int index) {
		final Recorder idRecorder = new Recorder();
		final TreedocNode node = findNthContentAndAlterSize(index + 1,
				idRecorder, -1);
		node.tombstone = true;
		return idRecorder.createIdentifier();
	}

	public int getSubtreeSize() {
		return subtreeSize;
	}

	public void getSubtreeContent(final StringBuilder contentBuffer) {
		getNodesContent(leftChildren, contentBuffer);
		if (containsContent())
			contentBuffer.append(content);
		getNodesContent(rightChildren, contentBuffer);
	}

	private boolean containsContent() {
		return !tombstone;
	}

	private TreedocNode findNodeAndAlterSize(final TreedocIdentifier id,
			final int idIndex, final int idUseLength, final int sizeDelta) {
		subtreeSize += sizeDelta;
		if (idIndex == idUseLength)
			return this;
		final EdgeDirection direction = id.getEdgeDirection(idIndex);
		final TreedocNode children[] = direction == EdgeDirection.LEFT ? leftChildren
				: rightChildren;
		final UniqueTag tag = id.getUniqueTag(idIndex);
		final TreedocNode child = children[binarySearchByTag(children, tag)];
		return child.findNodeAndAlterSize(id, idIndex + 1, idUseLength,
				sizeDelta);
	}

	private void insertChild(final TreedocNode child,
			final EdgeDirection direction) {
		final TreedocNode children[] = direction == EdgeDirection.LEFT ? leftChildren
				: rightChildren;
		TreedocNode newChildren[];
		if (children == null) {
			newChildren = new TreedocNode[1];
			newChildren[0] = child;
		} else {
			newChildren = new TreedocNode[children.length + 1];
			int childIndex = binarySearchByTag(children, child.uniqueTag);
			if (childIndex >= 0)
				throw new IllegalStateException(
						"Corrupted state, node to insert already exists.");
			childIndex = (childIndex - 1) * -1;
			System.arraycopy(children, 0, newChildren, 0, childIndex);
			newChildren[childIndex] = child;
			System.arraycopy(children, childIndex, newChildren, childIndex + 1,
					children.length - childIndex);
		}
		if (direction == EdgeDirection.LEFT)
			leftChildren = newChildren;
		else
			rightChildren = newChildren;
	}

	private TreedocNode findNthContentAndAlterSize(final int n,
			final Recorder idRecorder, final int sizeDelta) {
		if (n <= 0)
			throw new IllegalArgumentException(
					"requested node with a negative index");
		if (n > subtreeSize)
			throw new IllegalArgumentException(
					"requested node out of subtree range");
		subtreeSize += sizeDelta;
		int nodesToSkip = n;
		for (TreedocNode child : leftChildren) {
			final int childSize = child.getSubtreeSize();
			if (childSize == 0)
				continue;
			if (childSize <= nodesToSkip) {
				idRecorder.recordEdge(EdgeDirection.LEFT, child.uniqueTag);
				child.findNthContentAndAlterSize(nodesToSkip, idRecorder,
						sizeDelta);
			} else {
				nodesToSkip -= childSize;
			}
		}

		if (containsContent())
			nodesToSkip--;
		if (nodesToSkip == 0)
			return this;

		for (TreedocNode child : rightChildren) {
			final int childSize = child.getSubtreeSize();
			if (childSize == 0)
				continue;
			if (childSize <= nodesToSkip) {
				idRecorder.recordEdge(EdgeDirection.RIGHT, child.uniqueTag);
				child.findNthContentAndAlterSize(nodesToSkip, idRecorder,
						sizeDelta);
			} else {
				nodesToSkip -= childSize;
			}
		}
		throw new IllegalStateException(
				"Could not find a node within a subtree - corrupted metadata!");
	}

	private TreedocNode insertAfter(final TreedocNode newNode,
			final Recorder idRecorder) {
		if (rightChildren == null) {
			rightChildren = new TreedocNode[1];
			rightChildren[0] = newNode;
			idRecorder.recordEdge(EdgeDirection.RIGHT, newNode.uniqueTag);
		} else {
			// TODO: If we want to compact the space & reduce access time,
			// we might handle the case "node.compareTo(rightChildren[0]) < 0"
			// differently.

			final TreedocNode firstChild = rightChildren[0];
			idRecorder.recordEdge(EdgeDirection.RIGHT, firstChild.uniqueTag);
			firstChild.insertBefore(newNode, idRecorder);
		}
		return newNode;
	}

	private void insertBefore(final TreedocNode newNode,
			final Recorder idRecorder) {
		subtreeSize++;
		if (leftChildren == null) {
			leftChildren = new TreedocNode[1];
			leftChildren[0] = newNode;
			idRecorder.recordEdge(EdgeDirection.LEFT, newNode.uniqueTag);
		} else {
			final TreedocNode firstChild = leftChildren[0];
			idRecorder.recordEdge(EdgeDirection.LEFT, firstChild.uniqueTag);
			firstChild.insertBefore(newNode, idRecorder);
		}
	}
}
