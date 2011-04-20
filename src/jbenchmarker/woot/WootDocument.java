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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jbenchmarker.woot;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import jbenchmarker.core.Document;
import jbenchmarker.core.Operation;
import jbenchmarker.trace.TraceOperation;

/**
 *
 * @author urso
 */
public abstract class WootDocument<N extends WootNode> implements Document {
    final protected List<N> elements;

    public WootDocument(N CB, N CE) {
        super();
        elements = new ArrayList<N>();
        elements.add(CB);
        elements.add(CE);
    }

    @Override
    public String view() {
        StringBuilder s = new StringBuilder();
        for (WootNode w : elements) 
            if (w.isVisible()) s.append(w.getContent());
        return s.toString();
    }
    
    public int find(WootIdentifier id) {
        return findAfter(0, id);
    }

    public boolean has(WootIdentifier id) {
        for (N n : elements) 
            if (n.getId().equals(id)) return true;
        return false;
    }
    
    public int findAfter(int d, WootIdentifier id) {
        ListIterator<N> it = elements.listIterator(d);
        while (it.hasNext()) {
            if (it.next().getId().equals(id)) return d;
            d++;
        }
        throw new NoSuchElementException("Don't find " + id + " after position " + d);
    }
    
    @Override
    public void apply(Operation op) {
        WootOperation wop = (WootOperation) op;
        
        if (wop.getType() == TraceOperation.OpType.del) {
            elements.get(find(wop.getId())).setVisible(false);
        } else { 
            int ip = find(wop.getIp()), in = findAfter(ip, wop.getIn());               
            insertBetween(ip, in, wop);
        }        
    }
    
    protected abstract void insertBetween(int ip, int in, WootOperation wop);

    public List<N> getElements() {
        return elements;
    }

    /**
     * pth visible character
     */
    public int getVisible(int p) {
        ListIterator<N> it = elements.listIterator(1);
        int i=1, j=0;        
        while (j<=p) {
            if (it.next().isVisible()) j++;
            i++;
        }
        return i-1; 
    }

    /**
     * position of next visible character starting from v model position.
     */
    public int nextVisible(int v) {      
        ListIterator<N> it = elements.listIterator(v+1);
        int i = v+1;
        while (!it.next().isVisible()) {
            i++;
        }
        return i; 
    }
    
    /**
     * Index of previous character of pth visible character. 0 for 0th
     */
    public int getPrevious(int p) {
        if (p==0) return 0;
        return getVisible(p-1);
    }
    
    /**
     * Index of next character of pth visible characterstarting from v model position. n-1 for last visible. 
     */
    public int getNext(int v) {
        int i = v+1;
        while (i<elements.size()-1 && !elements.get(i).isVisible()) 
            i++;
        return i; 
    }
    
    public WootOperation delete(TraceOperation o, WootIdentifier id) {
        return new WootOperation(o, id);
    }
    
    public WootOperation insert(TraceOperation o, WootIdentifier id, WootIdentifier ip, WootIdentifier in, char content) {
        return new WootOperation(o, id, ip, in, content);
    }
}