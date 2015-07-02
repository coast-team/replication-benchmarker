/**
 * Replication Benchmarker
 * https://github.com/score-team/replication-benchmarker/ Copyright (C) 2013
 * LORIA / Inria / SCORE Team
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package jbenchmarker.rgalocal;

import collect.RangeList;
import jbenchmarker.core.Document;
import jbenchmarker.core.SequenceOperation;
import java.util.HashMap;
import java.util.NoSuchElementException;
import crdt.Operation;
import java.util.List;
import static jbenchmarker.rgalocal.RGAMerge.MAGIC;

/**
 * Handles double local identifiers
 *
 * @author Roh, urso
 */
public class RGADocument<T> implements Document {

    public static final long MAX = Long.MAX_VALUE / 2, MIN = Long.MIN_VALUE / 2;
    private final HashMap<RGAS2Vector, RGANode<T>> hash;
    private final RangeList<RGANode<T>> localOrder;
    private final RGANode head;
    int collision;

    public RGADocument() {
        super();
        collision = 0;
        head = new RGANode();
        head.setPosition(MIN);
        hash = new HashMap<RGAS2Vector, RGANode<T>>();
        localOrder = new RangeList<RGANode<T>>();
    }

    @Override
    public String view() {
        StringBuilder s = new StringBuilder();
        for (RGANode<T> n : localOrder) {
            s.append(n.getContent());
        }
        return s.toString();
    }

    @Override
    public void apply(Operation op) {
        RGAOperation rgaop = (RGAOperation) op;
        if (rgaop.getType() == SequenceOperation.OpType.delete) {
            boolean wasVisible = remoteDelete(rgaop);
            if (wasVisible) {
                localOrder.remove(findLocal(hash.get(rgaop.getS4VPos())));
            }
        } else {
            RGANode prev;
            if (rgaop.getS4VPos() == null) {
                prev = head;
            } else {
                prev = hash.get(rgaop.getS4VPos());
            }
            RGANode node = remoteInsert(prev, rgaop), next = node.getNextVisible();
            long nextPos = next == null ? MAX : next.getPosition();
            int afterPos = next == null ? localOrder.size() : findLocal(next);
            long prevPos = afterPos == 0 ? MIN : localOrder.get(afterPos - 1).getPosition();
            node.setPosition(middle(prevPos, nextPos));
            localOrder.add(afterPos, node);
        }
        if (collision > localOrder.size()) {
            rebalance();
        }
    }

    /**
     * Rebalance the table
     */
    void rebalance() {
        long step = Long.MAX_VALUE / (localOrder.size() * MAGIC), pos = MIN;
        for (RGANode<T> n : localOrder) {
            pos += step;
            n.setPosition(pos);
        }
        collision = 0;
    }

    RGANode remoteInsert(RGANode prev, RGAOperation op) {
        RGANode newnd = new RGANode(op.getS4VTms(), op.getContent());
        RGANode next;
        RGAS2Vector s4v = op.getS4VTms();

        if (prev == null) {
            throw new NoSuchElementException("RemoteInsert");
        }
        next = prev.getNext();

        while (next != null) {
            if (s4v.compareTo(next.getKey()) == RGAS2Vector.AFTER) {
                break;
            }
            prev = next;
            next = next.getNext();
        }

        newnd.setNext(next);
        prev.setNext(newnd);

        hash.put(op.getS4VTms(), newnd);
        return newnd;
    }

    boolean remoteDelete(RGAOperation op) {
        boolean wasVisible;
        RGANode node = hash.get(op.getS4VPos());
        if (node == null) {
            throw new NoSuchElementException("Cannot find" + op.getS4VPos());
        }
        wasVisible = node.isVisible();
        node.makeTombstone();

        return wasVisible;
    }

    public RGAS2Vector getVisibleS4V(int v) {
        RGANode node = getVisibleNode(v);
        if (node == null) {
            throw new NoSuchElementException("getVisibleS4V");
        }
        return node.getKey();
    }

    public RGANode getVisibleNode(int v) {
        if (v == 0) {
            return head;
        } else {
            return localOrder.get(v - 1);
        }
    }

    @Override
    public int viewLength() {
        return localOrder.size();
    }

    /**
     * Add a list of nodes in the local order table.
     *
     * @param i position
     * @param ln nodes
     */
    void addLocal(int i, List<RGANode<T>> ln) {
        localOrder.addAll(i, ln);
    }

    /**
     * Remove a range of nodes from the local order table.
     *
     * @param p position
     * @param offset number of nodes
     */
    void removeLocal(int p, int offset) {
        localOrder.removeRangeOffset(p, offset);
    }

    /**
     * Search an existing node in the local order table. Dichotomic search +
     * verification to handles collisions
     *
     * @param pos the position
     * @return the lower index with a position greater or equal pos.
     */
    private int findLocal(RGANode<T> node) {
        long pos = node.getPosition();
        int startIndex = 0, endIndex = localOrder.size(), middleIndex;
        do {
            middleIndex = startIndex + (endIndex - startIndex) / 2;
            if (localOrder.get(middleIndex).getPosition() < pos) {
                startIndex = middleIndex + 1;
            } else {
                endIndex = middleIndex;
            }
        } while (localOrder.get(middleIndex).getPosition() != pos);
        int index = middleIndex;
        while (!localOrder.get(index).equals(node)
                && index < localOrder.size() - 1
                && localOrder.get(index + 1).getPosition() == pos) {
            ++index;
            ++collision;
        }
        if (localOrder.get(index).equals(node)) {
            return index;
        }
        index = middleIndex - 1;
        while (!localOrder.get(index).equals(node)) { // must be somewhere
            --index;
            ++collision;
        }
        return index;
    }

    public static long middle(long a, long b) {
        return a + ((b - a) / (2 * MAGIC));
    }
}
