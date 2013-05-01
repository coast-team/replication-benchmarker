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
package crdt.tree.orderedtree;

import collect.OrderedNode;
import collect.SimpleNode;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import jbenchmarker.logoot.LogootDocument;
import jbenchmarker.logoot.LogootIdentifier;
import jbenchmarker.logoot.LogootStrategy;

/**
 *
 * @author urso
 */
// TODO : optimize space occupation by factoring common elements
public class LogootTreeNode<T> extends LogootDocument<LogootTreeNode<T>> implements PositionnedNode<T> {
    private final T value;
    private final Clock clock;

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
    
    static private class Clock implements Serializable{
        int value = 0; 
    }
    
    private LogootTreeNode(T value, int r, LogootStrategy strategy, Clock c) {
        super(r, strategy);
        this.value = value;
        this.clock = c;
    }
    
    public LogootTreeNode(T value, int r, LogootStrategy strategy) {
        this(value, r, strategy, new Clock());
    }
    
    @Override
    public T getValue() {
        return value;
    }
    
    @Override
    public int getChildrenNumber() {
        return document.size()-2; 
    }

    @Override
    public OrderedNode<T> getChild(int p) {
        return document.get(p+1);
    }

    @Override
    public OrderedNode<T> getChild(Positioned<T> p) {
        return document.get(dicho((LogootIdentifier)p.getPi()));
    }

    @Override
    public Positioned<T> getPositioned(int p) {
        return new Positioned<T>(idTable.get(p+1), document.get(p+1).getValue());
    }
    
    @Override
    public PositionIdentifier getNewPosition(int p, T element) {
        return getNewId(p);
    }

    @Override
    public void add(PositionIdentifier id, T elem) {
         int pos = dicho((LogootIdentifier)id);
         idTable.add(pos, (LogootIdentifier)id);
         document.add(pos, createNode(elem));       
    }
    
    @Override
    public void remove(PositionIdentifier id, T elem) {
         int pos = dicho((LogootIdentifier)id);
         idTable.remove(pos);
         document.remove(pos);       
    }

    @Override
    public List<LogootTreeNode<T>> getElements() {
        return document.subList(1, document.size()-1);
    }

    @Override
    public LogootTreeNode<T> createNode(T elem) {
        return new LogootTreeNode<T>(elem, replicaNumber, strategy, clock);
    }

    @Override
    public int nextClock() {
        return clock.value++;
    }

/*    public boolean same(OrderedNode<T> other) {
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
    }
    */
    @Override
    public String toString() {
        return value + "{" + getElements() + '}';
    }
}
