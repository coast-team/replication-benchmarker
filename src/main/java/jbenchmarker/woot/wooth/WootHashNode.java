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

import jbenchmarker.woot.WootIdentifier;
import jbenchmarker.woot.WootNode;

/**
 * A linked list of WootNodes.
 * @author urso
 */
public class WootHashNode<T> extends WootNode<T> {
    private WootHashNode next;
    final private int degree;

    public WootHashNode(WootIdentifier id, T content, boolean visible, WootHashNode next, int degree) {
        super(id, content, visible);
        this.next = next;
        this.degree = degree;
    }

    public int getDegree() {
        return degree;
    }

    WootHashNode getNext() {
        return next;
    }

    void setNext(WootHashNode next) {
        this.next = next;
    }
}
