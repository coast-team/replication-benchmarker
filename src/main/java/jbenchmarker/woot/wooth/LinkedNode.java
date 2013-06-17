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

import jbenchmarker.woot.WootIdentifier;
import jbenchmarker.woot.WootNode;

/**
 * A Woot node within a linked list. 
 * @author urso
 */
public abstract class LinkedNode<T> extends WootNode<T> {

    private LinkedNode next;
    final private int degree;

    public LinkedNode(WootIdentifier id, T content, LinkedNode<T> next, int degree) {
        super(id, content);
        this.next = next;
        this.degree = degree;
    }

    public int getDegree() {
        return degree;
    }

    public LinkedNode getNext() {
        return next;
    }

    public void setNext(LinkedNode next) {
        this.next = next;
    }

    /**
     * May not halt. Costly!
     *
     * @param obj
     * @return the lists are equals
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final LinkedNode<T> other = (LinkedNode<T>) obj;
        if (this.next != other.next && (this.next == null || !this.next.equals(other.next))) {
            return false;
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 97 * hash + (this.next != null ? this.next.hashCode() : 0);
        return hash;
    }
}
