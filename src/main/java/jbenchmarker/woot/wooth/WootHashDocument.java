/**
 *   This file is part of ReplicationBenchmark.
 *
 *   ReplicationBenchmark is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   ReplicationBenchmark is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with ReplicationBenchmark.  If not, see <http://www.gnu.org/licenses/>.
 *
 **/


package jbenchmarker.woot.wooth;

import java.util.Map;
import jbenchmarker.core.Document;
import jbenchmarker.core.Operation;
import jbenchmarker.trace.TraceOperation;
import jbenchmarker.woot.WootIdentifier;
import jbenchmarker.woot.WootOperation;

/**
 *
 * @author urso
 */
public class WootHashDocument implements Document {
    final protected WootHashNode first;
    final protected Map<WootIdentifier, WootHashNode> map;

    public WootHashDocument() {
        super();
        WootHashNode end = new WootHashNode(WootIdentifier.IE, ' ', false, null, 0);
        this.first = new WootHashNode(WootIdentifier.IB, ' ', false, end, 0);
        this.map = new java.util.HashMap<WootIdentifier, WootHashNode>();
        this.map.put(WootIdentifier.IB, first);
        this.map.put(WootIdentifier.IE, end);
    }

    public String view() {
        StringBuilder s = new StringBuilder();
        for (WootHashNode w=first; w!=null; w = w.getNext()) 
            if (w.isVisible()) s.append(w.getContent());
        return s.toString();
    }
    
    public WootHashNode find(WootIdentifier id) {
        return map.get(id);
    }

    public boolean has(WootIdentifier id) {
        for (WootHashNode w=first; w!=null; w = w.getNext()) 
            if (w.isVisible()) return true;
        return false;    
    }    
    
    public void apply(Operation op) {
        WootOperation wop = (WootOperation) op;
        
        if (wop.getType() == TraceOperation.OpType.del) {
            map.get(wop.getId()).setVisible(false);
        } else { 
            WootHashNode wn = new WootHashNode(wop.getId(), wop.getContent(), true, null, 
                    Math.max(map.get(wop.getIp()).getDegree(), map.get(wop.getIn()).getDegree())+1);           
            insertBetween(wn, map.get(wop.getIp()), map.get(wop.getIn()));
            map.put(wop.getId(), wn);
        }        
    }

    /**
     * pth visible character
     */
    public WootHashNode getVisible(int p) {
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
        while ( !v.isVisible() && !v.getId().equals(WootIdentifier.IE)) {
            v = v.getNext();
        }
        return v; 
    }
    
    public WootOperation delete(TraceOperation o, WootIdentifier id) {
        return new WootOperation(o, id);
    }
    
    public WootOperation insert(TraceOperation o, WootIdentifier id, WootIdentifier ip, WootIdentifier in, char content) {
        return new WootOperation(o, id, ip, in, content);
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
}