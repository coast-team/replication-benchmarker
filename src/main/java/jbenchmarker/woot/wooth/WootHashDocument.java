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
package jbenchmarker.woot.wooth;

import crdt.Factory;
import java.util.Map;
import jbenchmarker.core.Document;
import crdt.Operation;
import jbenchmarker.core.SequenceOperation;
import jbenchmarker.woot.WootIdentifier;
import jbenchmarker.woot.WootOperation;
import jbenchmarker.woot.WootPosition;

/**
 * A WOOTH document. Linked list and hash table to retrieve elements.
 * @author urso
 */
public class WootHashDocument<T> implements Document, Factory<Document> {

    final protected LinkedNode<T> first;
    final protected Map<WootIdentifier, LinkedNode<T>> map = new java.util.HashMap<WootIdentifier, LinkedNode<T>>();
    protected int size = 0;
    private int clock = 0;
    private int replicaNumber;

     public WootHashDocument() {
        LinkedNode<T> end = new WootHashNode<T>(WootIdentifier.IE, null, false, null, 0);
        this.first = new WootHashNode<T>(WootIdentifier.IB, null, false, end, 0);
        this.map.put(WootIdentifier.IB, first);
        this.map.put(WootIdentifier.IE, end);
     }
    
    public WootHashDocument(LinkedNode<T> first, LinkedNode<T> end) {
        this.first = first;
        this.map.put(WootIdentifier.IB, first);
        this.map.put(WootIdentifier.IE, end);
    }

    @Override
    public String view() {
        StringBuilder s = new StringBuilder();
        for (LinkedNode<T> w = first; w != null; w = w.getNext()) {
            if (w.isVisible()) {
                s.append(w.getContent());
            }
        }
        return s.toString();
    }

    public T find(WootIdentifier id) {
        return map.get(id).getContent();
    }

    public boolean has(WootIdentifier id) {
        for (LinkedNode<T> w = first; w != null; w = w.getNext()) {
            if (w.isVisible()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void apply(Operation op) {
        WootOperation<T> wop = (WootOperation<T>) op;

        if (wop.getType() == SequenceOperation.OpType.delete) {
            del(wop.getId());
        } else {
            add(wop.getId(), wop.getContent(), wop.getIp(), wop.getIn());
        }
    }

    protected void add(WootIdentifier id, T content, WootIdentifier ip, WootIdentifier in) {
        LinkedNode<T> wp = map.get(ip);
        LinkedNode<T> wn = map.get(in);
        LinkedNode<T> w = newNode(id, content, true, null, Math.max(wp.getDegree(), wn.getDegree()) + 1);
        insertBetween(w, wp, wn);
        map.put(id, w);
        ++size;
    }

    protected void del(WootIdentifier id) {
        setVisible(id, false);
    }

    protected void setVisible(WootIdentifier id, boolean b) {
        LinkedNode<T> e = map.get(id);
        if (!b && e.isVisible()) {
            --size;
        } else if (b && !e.isVisible()) {
            ++size;
        }
        ((WootHashNode) e).setVisible(b);
    }

    /**
     * pth visible character
     */
    public LinkedNode<T> getVisible(int p) {
        int j = -1;
        LinkedNode<T> w = first;
        while (j < p) {
            if (w.isVisible()) {
                j++;
            }
            if (j < p) {
                w = w.getNext();
            }
        }
        return w;
    }

    /**
     * next visible character starting from v model position.
     */
    public LinkedNode<T> nextVisible(LinkedNode<T> v) {
        v = v.getNext();
        while (!v.isVisible()) {
            v = v.getNext();
        }
        return v;
    }

    /**
     * Previous character of pth visible character. 0 for 0th
     */
    public LinkedNode<T> getPrevious(int p) {
        if (p == 0) {
            return first;
        }
        return getVisible(p - 1);
    }

    /**
     * Next character of pth visible characterstarting from v model position. IE
     * for last visible.
     */
    public LinkedNode<T> getNext(LinkedNode<T> v) {
        v = v.getNext();
        while (!v.isVisible() && v.getNext() != null) {
            v = v.getNext();
        }
        return v;
    }

    public WootOperation delete(SequenceOperation o, WootIdentifier id) {
        return new WootOperation(SequenceOperation.OpType.delete, id, null);
    }

    public WootOperation insert(SequenceOperation o, WootIdentifier ip, WootIdentifier in, T content) {
        return new WootOperation(SequenceOperation.OpType.insert,
                new WootPosition(nextIdentifier(), ip, in), content);
    }

    private void insertBetween(LinkedNode<T> wn, LinkedNode<T> ip, LinkedNode<T> in) {
        if (in == ip.getNext()) {
            wn.setNext(in);
            ip.setNext(wn);
        } else {
            LinkedNode<T> e = ip.getNext().getNext();
            int dMin = ip.getNext().getDegree();
            while (e != in) {
                if (e.getDegree() < dMin) {
                    dMin = e.getDegree();
                }
                e = e.getNext();
            }
            e = ip.getNext();
            while (e != in) {
                if (e.getDegree() == dMin) {
                    if (e.getId().compareTo(wn.getId()) < 0) {
                        ip = e;
                    } else {
                        in = e;
                    }
                }
                if (e != in) {
                    e = e.getNext();
                }
            }
            insertBetween(wn, ip, in);
        }
    }
    
    protected LinkedNode<T> get(WootIdentifier id) {
        return map.get(id);
    }
            
    LinkedNode<T> getFirst() {
        return first;
    }

    @Override
    public int viewLength() {
        return size;
    }

    protected WootIdentifier nextIdentifier() {
        clock++;
        return new WootIdentifier(this.replicaNumber, clock);
    }

    public void setReplicaNumber(int replicaNumber) {
        this.replicaNumber = replicaNumber;
    }

    public int getReplicaNumber() {
        return replicaNumber;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final WootHashDocument<T> other = (WootHashDocument<T>) obj;
        if (this.first != other.first && (this.first == null || !this.first.equals(other.first))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + (this.first != null ? this.first.hashCode() : 0);
        return hash;
    }

    protected LinkedNode<T> newNode(WootIdentifier id, T content, boolean visible, LinkedNode<T> next, int degree) {
        return new WootHashNode<T>(id, content, visible, next, degree);
    }

    @Override
    public Document create() {
        return new WootHashDocument();
    }
}
