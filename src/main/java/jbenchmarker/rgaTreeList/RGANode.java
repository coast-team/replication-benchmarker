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

import java.io.Serializable;

/**
 *
 * @author Roh
 */
public class RGANode<T> implements Serializable {

    private RGAS4Vector key;
    private RGAS4Vector tomb;	//used for visible and tombstone purging if null, then not tombstone 		 
    private T content;
    private RGANode next;

    public RGANode() {
        this.key = null;
        this.next = null;
        this.tomb = null;
    }

    public RGANode(RGAS4Vector s4v, T c) {
        this.key = s4v;
        this.content = c;
        this.tomb = null;
        this.next = null;
    }

    public RGAS4Vector getKey() {
        return key;
    }

    public T getContent() {
        return content;
    }

    public boolean isVisible() {
        if (this.tomb == null) {
            return true;
        }
        return false;
    }

    public void makeTombstone(RGAS4Vector s4v) {
        this.tomb = s4v;
    }

    public void setNext(RGANode nd) {
        this.next = nd;
    }

    public RGANode getNext() {
        return next;
    }

    public RGAS4Vector getTomb() {
        return this.tomb;
    }

    public RGANode getNextVisible() {
        RGANode node = next;
        while (node != null && !node.isVisible()) {
            node = node.getNext();
        }
        return node;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final RGANode other = (RGANode) obj;
        if (this.key != other.key && (this.key == null || !this.key.equals(other.key))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + (this.key != null ? this.key.hashCode() : 0);
        return hash;
    }
}
