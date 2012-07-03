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

import java.util.Iterator;
import java.util.List;

import jbenchmarker.treedoc.TreedocIdentifier.ComponentScanner;
import jbenchmarker.treedoc.TreedocIdentifier.EdgeDirection;
import jbenchmarker.treedoc.TreedocIdentifier.Recorder;

/**
 * A node of of Treedoc binary-like tree. Each node have (optionally) left and
 * right children. The infix traversal order specifies document content.
 * <p>
 * Thread-hostile.
 * 
 * @author mzawirski
 */
class TreedocNode<T> {
	// TODO: export as a setting
	static final boolean USE_DISAMBIGUATORS_TRICK = true;

	protected static int binarySearchByTag(final EdgeDirection direction,
			final TreedocNode[] nodes, UniqueTag tag) {
		if (nodes == null)
			return -1;

		if (tag == null)
			tag = getMajorNodeTag(direction);

		// Based on code from Arrays.binarySearch, since rewriting binary search
		// differently does not make sense. I do not expect copyright trobules.
		int low = 0;
		int high = nodes.length - 1;
		while (low <= high) {
			int mid = (low + high) >>> 1;
			UniqueTag midTag = nodes[mid].uniqueTag;
			if (midTag == null)
				midTag = getMajorNodeTag(direction);
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

	protected static UniqueTag getMajorNodeTag(final EdgeDirection direction) {
		return direction == EdgeDirection.RIGHT ? UniqueTag.MAX : UniqueTag.MIN;
	}

	protected static void getNodesContent(final TreedocNode nodes[],
			final StringBuilder contentBuffer) {
		if (nodes == null)
			return;
		for (final TreedocNode child : nodes) {
			if (child.getSubtreeSize() > 0)
				child.getSubtreeContent(contentBuffer);
		}
	}

	protected static int getNodesNumber(final TreedocNode nodes[]) {
		int result = 0;
		if (nodes != null) {
			for (final TreedocNode child : nodes)
				result += child.getNodesNumber();
		}
		return result;
	}

	protected UniqueTag uniqueTag;
	/**
	 * Node content, valid only if !tombstone.
	 */
	protected T content;
	protected int subtreeSize;
	protected boolean tombstone;
	protected TreedocNode leftChildren[];
	protected TreedocNode rightChildren[];

	/**
	 * Creates non-empty node.
	 */
	protected TreedocNode(final UniqueTag uniqueTag, final T content) {
		this.uniqueTag = uniqueTag;
		this.content = content;
		this.subtreeSize = 1;
	}

	/**
	 * Creates tombstone node.
	 */
	protected TreedocNode(final UniqueTag uniqueTag) {
		this.uniqueTag = uniqueTag;
		this.tombstone = true;
	}

	protected int getSubtreeSize() {
		return subtreeSize;
	}

	protected void getSubtreeContent(final StringBuilder contentBuffer) {
		final int origBufferSize = contentBuffer.length();
		getNodesContent(leftChildren, contentBuffer);
		if (!tombstone)
			contentBuffer.append(content);
		getNodesContent(rightChildren, contentBuffer);
		// if (contentBuffer.length() - origBufferSize != getSubtreeSize()) {
		// throw new IllegalStateException(
		// "corrupted state, size of a content does not match internal size counter");
		// }
	}

	protected int getNodesNumber() {
		return getNodesNumber(leftChildren) + 1 + getNodesNumber(rightChildren);
	}

	protected boolean isRoot() {
		return false;
	}

	/**
	 * Finds an deletes a node indicated by id.
	 * 
	 * @param id
	 * @return true if node was still there.
	 */
	protected boolean findAndDeleteNode(
			final Iterator<ComponentScanner> idIterator) {
		if (!idIterator.hasNext()) {
			final boolean wasTombstone = tombstone;
			if (!wasTombstone)
				subtreeSize--;
			tombstone = true;
			return !wasTombstone;
		} else {
			final ComponentScanner component = idIterator.next();
			final EdgeDirection direction = component.getDirection();
			final UniqueTag tag = component.getTag();
			final TreedocNode children[] = getChildren(direction);
			final int childIndex = binarySearchByTag(direction, children, tag);
			if (childIndex >= 0) {
				final TreedocNode child = children[childIndex];
				if (child.findAndDeleteNode(idIterator)) {
					subtreeSize--;
					// Discard a whole subtree, as close to the root as
					// possible.
					if (child.subtreeSize == 0 && (isRoot() || subtreeSize > 0))
						removeEmptyChild(direction, childIndex);
					return true;
				}
			}
			// Else: subtree already does not exist.
			return false;
		}
	}

	protected void findAndInsertNode(
			final Iterator<ComponentScanner> idIterator, final List<T> content) {
		subtreeSize += content.size();
		if (!idIterator.hasNext()) {
			// Fill in the last subtree on the path with the content.
			createBalancedSubtreeOfContent(content, 0, content.size());
		} else {
			final TreedocIdentifier.ComponentScanner component = idIterator
					.next();
			final EdgeDirection direction = component.getDirection();
			final UniqueTag tag = component.getTag();
			final TreedocNode children[] = getChildren(direction);
			int childIndex = binarySearchByTag(direction, children, tag);
			final TreedocNode child;
			if (childIndex >= 0) {
				child = children[childIndex];
			} else {
				// Create node on the path if necessary.
				child = new TreedocNode(tag);
				TreedocNode newChildren[];
				if (children == null) {
					// Sequential case - single child.
					newChildren = new TreedocNode[1];
					newChildren[0] = child;
				} else {
					// Concurrent case - multiple children.
					newChildren = new TreedocNode[children.length + 1];
					childIndex = (childIndex + 1) * -1;
					System.arraycopy(children, 0, newChildren, 0, childIndex);
					newChildren[childIndex] = child;
					System.arraycopy(children, childIndex, newChildren,
							childIndex + 1, children.length - childIndex);
				}
				setChildren(direction, newChildren);
			}
			child.findAndInsertNode(idIterator, content);
		}
	}

	/**
	 * Creates a balanced tree of a given length containing a content. Assumes
	 * that this node is empty.
	 * 
	 * @param content
	 *            content for the subtree.
	 * @param begin
	 *            first index of content to use.
	 * @param length
	 *            size of content to create.
	 */
	protected void createBalancedSubtreeOfContent(final List<T> content,
			final int begin, final int length) {
		// Invariant: leftSubtree + rightSubtree + 1 = length
		final int leftSubtree = (length - 1) / 2;
		final int rightSubtree = length - 1 - leftSubtree;
		if (leftSubtree > 0) {
			final TreedocNode leftChild = new TreedocNode(uniqueTag);
			final TreedocNode[] leftChildren = new TreedocNode[1];
			leftChildren[0] = leftChild;
			assertHasNoChildren(this.leftChildren);
			setChildren(EdgeDirection.LEFT, leftChildren);
			leftChild.createBalancedSubtreeOfContent(content, begin,
					leftSubtree);
		}
		this.content = content.get(begin + leftSubtree);
		this.tombstone = false;
		if (subtreeSize != 0 && subtreeSize != length) {
			throw new IllegalStateException(
					"trying to create a balanced subtree within the node that already contains "
							+ subtreeSize + " nodes");
		}
		this.subtreeSize = length;
		if (rightSubtree > 0) {
			final TreedocNode rightChild = new TreedocNode(uniqueTag);
			final TreedocNode[] rightChildren = new TreedocNode[1];
			rightChildren[0] = rightChild;
			assertHasNoChildren(this.rightChildren);
			setChildren(EdgeDirection.RIGHT, rightChildren);
			rightChild.createBalancedSubtreeOfContent(content, begin
					+ leftSubtree + 1, rightSubtree);
		}
	}

	private void assertHasNoChildren(final TreedocNode children[]) {
		if (children != null && children.length > 0) {
			throw new IllegalStateException(
					"trying to create a balance subtree within the node that already contains some children");
		}
	}

	protected TreedocNode[] getChildren(final EdgeDirection direction) {
		return direction == EdgeDirection.LEFT ? leftChildren : rightChildren;
	}

	protected void setChildren(final EdgeDirection direction,
			final TreedocNode[] newChildren) {
		if (direction == EdgeDirection.LEFT)
			leftChildren = newChildren;
		else
			rightChildren = newChildren;
	}

	protected void removeEmptyChild(final EdgeDirection direction,
			final int childIndex) {
		final TreedocNode children[] = getChildren(direction);
		final TreedocNode newChildren[];
		if (children.length == 1) {
			newChildren = null;
		} else {
			newChildren = new TreedocNode[children.length - 1];
			System.arraycopy(children, 0, newChildren, 0, childIndex);
			System.arraycopy(children, childIndex + 1, newChildren, childIndex,
					children.length - childIndex - 1);
		}
		setChildren(direction, newChildren);
	}

	protected TreedocNode findNthContentAndAlterSize(final DecreasingCounter n,
			final Recorder idRecorder, final int sizeDelta) {
		subtreeSize += sizeDelta;
		final TreedocNode leftDescendant = findNthContentInChildrenAndAlterSize(
				EdgeDirection.LEFT, n, idRecorder, sizeDelta);
		if (leftDescendant != null)
			return leftDescendant;

		if (!tombstone)
			n.decrement(1);
		if (n.get() == 0)
			return this;

		final TreedocNode rightDescendant = findNthContentInChildrenAndAlterSize(
				EdgeDirection.RIGHT, n, idRecorder, sizeDelta);
		if (rightDescendant == null)
			throw new IllegalStateException(
					"Could not find a node within a subtree - corrupted metadata!");
		return rightDescendant;
	}

	protected TreedocNode findNthContentInChildrenAndAlterSize(
			final EdgeDirection direction, final DecreasingCounter n,
			final Recorder idRecorder, final int sizeDelta) {
		final TreedocNode children[] = getChildren(direction);
		if (children == null)
			return null;
		for (int childIndex = 0; childIndex < children.length; childIndex++) {
			final TreedocNode child = children[childIndex];
			final int childSize = child.getSubtreeSize();
			if (childSize == 0)
				continue;
			if (n.get() <= childSize) {
				// TODO: the fact this exact condition is here is a hack.
				if (USE_DISAMBIGUATORS_TRICK) {
					if (sizeDelta > 0 && direction == EdgeDirection.RIGHT
							&& childSize == n.get()
							&& childIndex == children.length - 1
							&& children[childIndex].uniqueTag != null) {
						final TreedocNode newChildren[] = new TreedocNode[children.length + 1];
						System.arraycopy(children, 0, newChildren, 0,
								children.length);
						final TreedocNode maxTagNode = new TreedocNode(null);
						idRecorder.recordEdge(direction, maxTagNode.uniqueTag);
						maxTagNode.subtreeSize = sizeDelta;
						newChildren[children.length] = maxTagNode;
						setChildren(direction, newChildren);
						return newChildren[children.length];
					}
				}
				idRecorder.recordEdge(direction, child.uniqueTag);
				final TreedocNode foundNode = child.findNthContentAndAlterSize(
						n, idRecorder, sizeDelta);
				// Discard a whole subtree, as close to the root as possible.
				if (child.subtreeSize == 0 && (isRoot() || subtreeSize > 0))
					removeEmptyChild(direction, childIndex);

				return foundNode;
			} else {
				n.decrement(childSize);
			}
		}
		return null;
	}

	protected TreedocNode insertAfter(final TreedocNode newNode,
			final Recorder idRecorder) {
		if (rightChildren == null) {
			rightChildren = new TreedocNode[1];
			rightChildren[0] = newNode;
			idRecorder.recordEdge(EdgeDirection.RIGHT, newNode.uniqueTag);
		} else {
			final TreedocNode firstChild = rightChildren[0];
			idRecorder.recordEdge(EdgeDirection.RIGHT, firstChild.uniqueTag);
			// TODO: An alternative here is to handle the case
			// (node.uniqueTag.compareTo(rightChildren[0].uniqueTag) < 0)
			// differently.
			firstChild.insertBefore(newNode, idRecorder);
		}
		return newNode;
	}

	protected void insertBefore(final TreedocNode newNode,
			final Recorder idRecorder) {
		subtreeSize += newNode.subtreeSize;
		if (leftChildren == null) {
			leftChildren = new TreedocNode[1];
			leftChildren[0] = newNode;
			idRecorder.recordEdge(EdgeDirection.LEFT, newNode.uniqueTag);
		} else {
			final TreedocNode firstChild = leftChildren[0];
			idRecorder.recordEdge(EdgeDirection.LEFT, firstChild.uniqueTag);
			// TODO: An alternative here is to handle the case
			// (node.uniqueTag.compareTo(leftChildren[0].uniqueTag) < 0)
			// differently.
			firstChild.insertBefore(newNode, idRecorder);
		}
	}

	@Override
	public String toString() {
		final StringBuilder buf = new StringBuilder(getSubtreeSize());
		getNodesContent(leftChildren, buf);
		buf.append('|');
		buf.append(tombstone ? '!' : content);
		buf.append('|');
		getNodesContent(rightChildren, buf);
		buf.append("|size=");
		buf.append(getSubtreeSize());
		return buf.toString();
	}

	/**
	 * An integer counter object. A work-around for Java, whic does not support
	 * returning two objects (a pretty handy feature for recursively traversing
	 * the tree).
	 */
	protected static class DecreasingCounter {
		private int value;

		public DecreasingCounter(final int value) {
			this.value = value;
		}

		public int get() {
			return value;
		}

		public void decrement(final int delta) {
			value -= delta;
		}
	}
}
