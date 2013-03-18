/**
 * Replication Benchmarker
 * https://github.com/score-team/replication-benchmarker/
 * Copyright (C) 2012 LORIA / Inria / SCORE Team
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
package crdt.tree.orderedtree;

import collect.OrderedNode;
import collect.SimpleNode;
import java.util.AbstractList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import jbenchmarker.core.Operation;
import jbenchmarker.woot.WootIdentifier;
import jbenchmarker.woot.WootOperation;
import jbenchmarker.woot.WootPosition;
import jbenchmarker.woot.wooth.WootHashDocument;
import jbenchmarker.woot.wooth.WootHashNode;

/**
 *
 * @author urso
 */
public class WootHashTreeNode<T> extends WootHashDocument<WootHashTreeNode<T>> implements PositionnedNode<T>{
    private final T value;
    private Map<WootIdentifier, WootPosition> positions = new HashMap<WootIdentifier, WootPosition>();

    public WootHashTreeNode(T value, int replicaNumber) {
        super(replicaNumber);
        this.value = value;
    }

    @Override
    public void apply(Operation op) {    
        super.apply(op);
        WootOperation wop = (WootOperation) op;
        positions.put(wop.getId(), new WootPosition(wop.getId(), wop.getIp(), wop.getIn()));
    }
    
    
    @Override
    public T getValue() {
        return value;
    }
    
    @Override
    public int getChildrenNumber() {
        return viewLength(); 
    }

    @Override
    public OrderedNode<T> getChild(int p) {
        return getVisible(p).getContent();
    }

    @Override
    public OrderedNode<T> getChild(Positioned<T> p) {
        return find(((WootPosition) p.getPi()).getId());
    }

    @Override
    public Positioned<T> getPositioned(int p) {        
        WootHashNode<WootHashTreeNode<T>> wn = getVisible(p);
        return new Positioned<T>(positions.get(wn.getId()), wn.getContent().getValue());
    }
    
    @Override
    public PositionIdentifier getNewPosition(int p, T element) {
        WootHashNode<WootHashTreeNode<T>>  wp = getPrevious(p), wn = getNext(wp);
        return new WootPosition(nextIdentifier(), wp.getId(), wn.getId()); 
    }

    @Override
    public void add(PositionIdentifier id, T elem) {
        final WootPosition wp = (WootPosition) id;
        final WootIdentifier wid = wp.getId();
        if (positions.containsKey(wid)) {
            setVisible(wid, true);
        } else {
            positions.put(wid, wp);
            add(wid, createNode(elem), wp.getIp(), wp.getIn());    
        }
    }
    
    @Override
    public void remove(PositionIdentifier id, T elem) {
        del(((WootPosition) id).getId());
    }

    // TODO : redo using sequential list
    @Override
    public List<WootHashTreeNode<T>> getElements() {
        return new AbstractList<WootHashTreeNode<T>>() {

            @Override
            public WootHashTreeNode<T> get(int i) {
                return getVisible(i).getContent();
            }

            @Override
            public int size() {
                return viewLength(); 
            }
        };
    }

    @Override
    public WootHashTreeNode<T> createNode(T elem) {
        return new WootHashTreeNode<T>(elem, getReplicaNumber());
    }
    
    @Override
    public String toString() {
        return value + "{" + getElements() + '}';
    }
    
    
    // TODO : more efficient implementation copy past is bad mokey. Mr McKey
   /* @Override
    public boolean same(OrderedNode<T> other) {
        if (other == null) {
            return false;
        }
        if (this.value != other.getValue() && (this.value == null || !this.value.equals(other.getValue()))) {
            return false;
        }
        if (getChildrenNumber() != other.getChildrenNumber()) {
            return false;
        }
        for (int i = 0; i < getChildrenNumber(); ++i) {
            if (!getChild(i).same(other.getChild(i))) {
                return false;
            }
        }
        return true;
    }*/

    @Override
    public SimpleNode<T> getFather() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Iterator<? extends SimpleNode<T>> iterator() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isChildren(SimpleNode<T> n) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
