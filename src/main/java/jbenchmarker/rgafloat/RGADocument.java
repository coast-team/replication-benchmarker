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
package jbenchmarker.rgafloat;

import collect.RangeList;
import jbenchmarker.core.Document;
import jbenchmarker.core.SequenceOperation;
import java.util.HashMap;
import java.util.NoSuchElementException;
import crdt.Operation;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Roh, urso
 */
public class RGADocument<T> implements Document {

    private final HashMap<RGAS2Vector, RGANode<T>> hash;
    private final RangeList<RGANode<T>> localOrder;
    private final RGANode head;

    public RGADocument() {
        super();
        head = new RGANode();
        head.setPosition(BigDecimal.ZERO);
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
                localOrder.remove(findLocal(hash.get(rgaop.getS4VPos()).getPosition()));
            }
        } else {
            RGANode prev;
            if (rgaop.getS4VPos() == null) {
                prev = head;
            } else {
                prev = hash.get(rgaop.getS4VPos());
            }           
            RGANode n = remoteInsert(prev,  rgaop);
            n.setPosition(middle(previousPosition(n), nextPosition(n)));
            localOrder.add(findLocal(n.getPosition()), n);
        }
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
        newnd.setLast(prev);
        prev.setNext(newnd);
        if (next != null) {
            next.setLast(newnd);
        }
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
            return localOrder.get(v-1);
        }
    }

    @Override
    public int viewLength() {
        return localOrder.size();
    }

    /**
     * Add a list of nodes in the local order table.
     * @param i position
     * @param ln nodes
     */
    void addLocal(int i, List<RGANode<T>> ln) {
        localOrder.addAll(i, ln);
    }

    /**
     * Remove a range of nodes from the local order table.
     * @param p position
     * @param offset number of nodes
     */
    void removeLocal(int p, int offset) {
        localOrder.removeRangeOffset(p, offset);
    }

    /**
     * Next position to be taken into account after a node 
     * @param prev the node
     * @return the position of the next visible node. ONE if none
     */
    private BigDecimal nextPosition(RGANode prev) {
        prev = prev.getNextVisible();
        if (prev == null) {
            return BigDecimal.ONE;
        } else {
            return prev.getPosition();
        }            
    }
    
    /**
     * Previous position to be taken into account after a node 
     * @param prev the node
     * @return the position of the previous visible node. ZERO if none
     */
    private BigDecimal previousPosition(RGANode prev) {
        prev = prev.getLastVisible();
        if (prev == null) {
            return BigDecimal.ZERO;
        } else {
            return prev.getPosition();
        } 
    }
    
    /**
     * Dichotomic search to find a position in the local order table. 
     * @param pos the position
     * @return the lower index with a position greater or equal pos.
     */
    private int findLocal(BigDecimal pos) {
        int startIndex = 0, endIndex = localOrder.size(), middleIndex;
        while (startIndex < endIndex) {
            middleIndex = startIndex + (endIndex - startIndex) / 2;
            int c = localOrder.get(middleIndex).getPosition().compareTo(pos);
            if (c == 0) {
                return middleIndex;
            } else if (c < 0) {
                startIndex = middleIndex + 1;
            } else {
                endIndex = middleIndex;
            }
        } 
        return startIndex;
    }

    /**
     * Middle of two bigdecimal 
     * @param a A
     * @param b B (B > A)
     * @return (A + (B - A) / 2) 
     */
    public static BigDecimal middle(BigDecimal a, BigDecimal b) {
        return a.add(b.subtract(a).divide(BigDecimal.valueOf(2)));
    }

}
