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

import collect.TreeList;
import jbenchmarker.core.Document;
import jbenchmarker.core.SequenceOperation;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import crdt.Operation;

/**
 *
 * @author Roh
 */
public class RGADocument<T> implements Document {

	private HashMap<RGAS4Vector, RGANode<T>> hash;
	private RGANode head;
	private TreeList list;
	

	public RGADocument() {
		super();
		head = new RGANode();
		hash = new HashMap<RGAS4Vector, RGANode<T>>();
		list = new TreeList();
	}

	@Override
	public String view() {
		StringBuilder s = new StringBuilder();
		for (Object n : list) {
			s.append(((RGANode)n).getContent());
		}
		return s.toString();
	}

	public void apply(Operation op) {
		RGAOperation rgaop = (RGAOperation) op;
		if (rgaop.getType() == SequenceOperation.OpType.delete) {
			boolean wasVisible = remoteDelete(rgaop);
			if (wasVisible) {
				list.remove(list.indexOf(hash.get(rgaop.getS4VPos())));
				hash.get(rgaop.getS4VPos()).setTree(null);
			}
		} else {
			RGANode prev;
			if (rgaop.getS4VPos() == null) {
				prev = head;
			} else {
				prev = hash.get(rgaop.getS4VPos());
			}
			List<RGANode<T>> news = new LinkedList<RGANode<T>>();
			RGANode node = prev;
			
			RGAS4Vector v = rgaop.getS4VTms();
			for (Object e : rgaop.getBlock()) {
				node = remoteInsert(node, v, (T)e);
				news.add(node);
				v = v.follower();
			}
			
			RGANode next = node.getNextVisible();
			int index = (next==null) ? list.size() : list.indexOf(next);
			list.addAll(index, news);
		}
	}

	
	
	public RGANode remoteInsert(RGANode prev, RGAS4Vector s4v, T content) {
		RGANode newnd = new RGANode(s4v, content);
		RGANode next;

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
		hash.put(s4v, newnd);
		return newnd;
	}

	public boolean remoteDelete(RGAOperation op) {
		RGANode node = hash.get(op.getS4VPos());
		
		if (node == null) {
			throw new NoSuchElementException("Cannot find" + op.getS4VPos());
		}
		
		boolean wasVisible= node.isVisible();
	    
		node.makeTombstone();
		
		return wasVisible;
	}

	void removeLocal(int p, int offset) {
		for (int i=0; i<offset; i++){
			((RGANode)list.get(p)).setTree(null);;
			list.remove(p);
		}
	}
	
	void addLocal(int i, List<RGANode<T>> ln) {
		list.addAll(i, ln);
	}
	
	public RGAS4Vector getVisibleS4V(int v) {
		RGANode node = getVisibleNode(v);
		if (node == null) {
			throw new NoSuchElementException("getVisibleS4V");
		}
		return node.getKey();
	}

	public RGANode getVisibleNode(int v) {
		if (v==0 || list.isEmpty()) return head;
		else return (RGANode) list.get(v-1);
	}

	@Override
	public int viewLength() {
		return list.size();
	}


	
}
