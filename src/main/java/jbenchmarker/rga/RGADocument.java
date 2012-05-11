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
package jbenchmarker.rga;

import jbenchmarker.core.Document;
import jbenchmarker.core.SequenceMessage;
import jbenchmarker.core.SequenceOperation;
import java.util.HashMap;
import java.util.NoSuchElementException;

/**
 *
 * @author Roh
 */
public class RGADocument<T> implements Document {

    private HashMap<RGAS4Vector, RGANode<T>> hash;
    private RGANode head;
    private int size = 0;
    //private RGAPurger	purger;

    public RGADocument() {
        super();
        head = new RGANode();
        hash = new HashMap<RGAS4Vector, RGANode<T>>();
        //purger= new RGAPurger(this);
    }

    public String view() {
        StringBuilder s = new StringBuilder();
        RGANode node = head.getNext();
        while (node != null) {
            if (node.isVisible()) {
                s.append(node.getContent());
            }
            node = node.getNext();
        }
        return s.toString();
    }

    public void apply(SequenceMessage op) {
        RGAOperation rgaop = (RGAOperation) op;
//		if(rgaop.getLoR() == RGAOperation.LOCAL){
//			if(rgaop.getType() == SequenceOperation.OpType.del) LocalDelete(rgaop);
//			else LocalInsert(rgaop);			
//		} else {
        if (rgaop.getType() == SequenceOperation.OpType.del) {
            RemoteDelete(rgaop);
        } else {
            RemoteInsert(rgaop);
        }
//		}
    }

    private void LocalInsert(RGAOperation op) {
        RGANode newnd = new RGANode(op.getS4VTms(), op.getContent());

        if (op.getIntPos() == 0) {
            newnd.setNext(head.getNext());
            head.setNext(newnd);
        } else {
            RGANode target = getVisibleNode(op.getIntPos());
            if (target == null) {
                throw new NoSuchElementException("Don't find " + op.getIntPos());
            }
            newnd.setNext(target.getNext());
            target.setNext(newnd);
        }
        hash.put(op.getS4VTms(), newnd);
        ++size;
    }

    private void LocalDelete(RGAOperation op) {
        RGANode node = getVisibleNode(op.getIntPos());
        if (node == null) {
            throw new NoSuchElementException("Don't find " + op.getIntPos());
        }
        if (node.isVisible()) { --size; }
        node.makeTombstone(op.getS4VTms());
        //	purger.enrol(node);
    }

    private void RemoteInsert(RGAOperation op) {
        RGANode newnd = new RGANode(op.getS4VTms(), op.getContent());
        RGANode prev, next;
        RGAS4Vector s4v = op.getS4VTms();
        if (op.getS4VPos() == null) {
            prev = head;
        } else {
            prev = hash.get(op.getS4VPos());
        }
        if (prev == null) {
            throw new NoSuchElementException("RemoteInsert");
        }
        next = prev.getNext();

        while (next != null) {
            if (s4v.compareTo(next.getKey()) == RGAS4Vector.AFTER) {
                break;
            }
            prev = next;
            next = next.getNext();
        }

        newnd.setNext(next);
        prev.setNext(newnd);
        hash.put(op.getS4VTms(), newnd);
        ++size;
    }

    private void RemoteDelete(RGAOperation op) {
        RGANode node = hash.get(op.getS4VPos());
        if (node == null) {
            throw new NoSuchElementException("Cannot find" + op.getS4VPos());
        }
        if (node.isVisible()) { --size; }
        node.makeTombstone(op.getS4VTms());
        //	purger.enrol(node);
    }

    public RGAS4Vector getVisibleS4V(int v) {
        RGANode node = getVisibleNode(v);
        if (node == null) {
            throw new NoSuchElementException("getVisibleS4V");
        }
        return node.getKey();
    }

    public RGANode getVisibleNode(int v) {
        RGANode node = head;
        int j = 0;

        while (j < v && node != null) {
            node = node.getNext();
            if (node != null && node.isVisible()) {
                j++;
            }
        }

        if (node == null || !node.isVisible()) {
            throw new NoSuchElementException("getVisibleNode");
        }
        return node;
    }

    @Override
    public int viewLength() {
        return size;
    }
}
