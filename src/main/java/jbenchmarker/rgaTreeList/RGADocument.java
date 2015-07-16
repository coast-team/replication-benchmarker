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
package jbenchmarker.rgaTreeList;

import jbenchmarker.core.Document;
import jbenchmarker.core.SequenceOperation;
import java.util.HashMap;
import java.util.NoSuchElementException;
import crdt.Operation;

/**
 *
 * @author Roh
 */
public class RGADocument<T> implements Document {

	private HashMap<RGAS4Vector, RGANode<T>> hash;
	private RGANode head;
	private int size = 0;
	private TreeList list = new TreeList();
	//private RGAPurger	purger;

	public RGADocument() {
		super();
		head = new RGANode();
		hash = new HashMap<RGAS4Vector, RGANode<T>>();
		//purger= new RGAPurger(this);
	}

	@Override
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

	public void apply(Operation op) {
		RGAOperation rgaop = (RGAOperation) op;
		if (rgaop.getType() == SequenceOperation.OpType.delete) {
			RemoteDelete(rgaop);
		} else {
			RemoteInsert(rgaop);
		}
	}

	private void RemoteInsert(RGAOperation op) {
		RGANode newnd = new RGANode(op.getS4VTms(), op.getContent());
		RGANode prev, next;
		RGAS4Vector s4v = op.getS4VTms();
		if (op.getS4VPos() == null) {
			prev = head;
			list.add(0, newnd);
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


		int p= op.getIntPos();
		if ( p >= size) p=size-1;
		list.add(p+2, newnd);
		newnd.setNext(next);
		prev.setNext(newnd);
		hash.put(op.getS4VTms(), newnd);
		++size;
	}

	private void RemoteDelete(RGAOperation op) {
		RGANode node = hash.get(op.getS4VPos());
		int p = op.getIntPos() ;
		if ( p >= size) p=size-1;
		if (node == null) {
			throw new NoSuchElementException("Cannot find" + op.getS4VPos());
		}
		if (node.isVisible()) { 
			list.remove(p);
			--size;

		}
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
		if (list.isEmpty()) return head;
		else return (RGANode) list.get(v);
	}

	@Override
	public int viewLength() {
		return size;
	}
}
