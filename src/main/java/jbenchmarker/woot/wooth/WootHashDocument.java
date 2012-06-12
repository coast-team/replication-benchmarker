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
package jbenchmarker.woot.wooth;

import java.util.Map;
import jbenchmarker.core.Document;
import jbenchmarker.core.Operation;
import jbenchmarker.core.SequenceOperation;
import jbenchmarker.woot.WootIdentifier;
import jbenchmarker.woot.WootOperation;

/**
 *
 * @author urso
 */
public class WootHashDocument<T> implements Document {
    final private WootHashNode<T> first;
    final private Map<WootIdentifier, WootHashNode<T>> map;
    private int size = 0;
    private int clock = 0;
    private int replicaNumber;

    public WootHashDocument() {
        super();
        WootHashNode end = new WootHashNode(WootIdentifier.IE, ' ', false, null, 0);
        this.first = new WootHashNode(WootIdentifier.IB, ' ', false, end, 0);
        this.map = new java.util.HashMap<WootIdentifier, WootHashNode<T>>();
        this.map.put(WootIdentifier.IB, first);
        this.map.put(WootIdentifier.IE, end);
    }

    public WootHashDocument(int replicaNumber) {
        this();
        this.replicaNumber = replicaNumber;
    }

    @Override
    public String view() {
        StringBuilder s = new StringBuilder();
        for (WootHashNode w=first; w!=null; w = w.getNext()) 
            if (w.isVisible()) s.append(w.getContent());
        return s.toString();
    }
    
    public T find(WootIdentifier id) {
        return map.get(id).getContent();
    }

    public boolean has(WootIdentifier id) {
        for (WootHashNode w=first; w!=null; w = w.getNext()) 
            if (w.isVisible()) return true;
        return false;    
    }    
    
    @Override
    public void apply(Operation op) {
        WootOperation<T> wop = (WootOperation<T>) op;
        
        if (wop.getType() == SequenceOperation.OpType.del) {
            del(wop.getId());
        } else { 
            add(wop.getId(), wop.getContent(), wop.getIp(), wop.getIn());
        }        
    }

    protected void add(WootIdentifier id, T content, WootIdentifier ip, WootIdentifier in) {
        WootHashNode wp = map.get(ip), wn = map.get(in),
                w = new WootHashNode(id, content, true, null, Math.max(wp.getDegree(), wn.getDegree()) + 1);
        insertBetween(w, wp, wn);
        map.put(id, w);
        ++size;
    }

    protected void del(WootIdentifier id) {
        setVisible(id, false);
    }
    
    
    protected void setVisible(WootIdentifier id, boolean b) {
        WootHashNode<T> e = map.get(id);
        if (!b && e.isVisible()) {
            --size;
        } else if (b && !e.isVisible()) {
            ++size;
        }
        e.setVisible(b);
    }
    
    /**
     * pth visible character
     */
    public WootHashNode<T> getVisible(int p) {
        int j=-1;        
        WootHashNode w = first;
        while (j<p) {
            if (w.isVisible()) j++;
            if (j<p) w = w.getNext();
        }
        return w;
    }

    /**
     * next visible character starting from v model position.
     */
    public WootHashNode nextVisible(WootHashNode v) { 
        v = v.getNext();
        while (!v.isVisible()) {
            v = v.getNext();
        }
        return v; 
    }

    /**
     * Previous character of pth visible character. 0 for 0th
     */
    public WootHashNode getPrevious(int p) {
        if (p==0) return first;
        return getVisible(p-1);
    }
    
    /**
     * Next character of pth visible characterstarting from v model position. IE for last visible. 
     */
    public WootHashNode getNext(WootHashNode v) {
        v = v.getNext();
        while ( !v.isVisible() && v.getNext() != null) {
            v = v.getNext();
        }
        return v; 
    }
    
    public WootOperation delete(SequenceOperation o, WootIdentifier id) {
        return new WootOperation(o, id);
    }
    
    public WootOperation insert(SequenceOperation o, WootIdentifier ip, WootIdentifier in, T content) {
        return new WootOperation(o, nextIdentifier(), ip, in, content);
    }

    private void insertBetween(WootHashNode wn, WootHashNode ip, WootHashNode in) {
        if (in == ip.getNext()) {
            wn.setNext(in);
            ip.setNext(wn);
        } else {
            WootHashNode e = ip.getNext().getNext();
            int dMin = ip.getNext().getDegree();
            while (e != in) {
                if (e==null)
                    System.out.println("bug");
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
                if (e!=in) e=e.getNext();
            }
            insertBetween(wn, ip, in);
        }

    }
    
    WootHashNode getFirst() {
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
}
