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

import java.io.Serializable;

/**
 *
 * @author urso
 */ 

public class Positioned<T> implements Serializable {
    final private PositionIdentifier pi;
    final private T elem;

    public Positioned(PositionIdentifier pi, T elem) {
        this.pi = pi;
        this.elem = elem;
    }

    public T getElem() {
        return elem;
    }

    public PositionIdentifier getPi() {
        return pi;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Positioned<T> other = (Positioned<T>) obj;
        if (this.pi != other.pi && (this.pi == null || !this.pi.equals(other.pi))) {
            return false;
        }
        if (this.elem != other.elem && (this.elem == null || !this.elem.equals(other.elem))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + (this.pi != null ? this.pi.hashCode() : 0);
        hash = 97 * hash + (this.elem != null ? this.elem.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "Positioned{" + "pi=" + pi + ", elem=" + elem + '}';
    }
}
