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
package jbenchmarker.wootr;

/**
 * Recussive WOOT node 
 * @author urso
 */
public class WootRNode { 
    
    public final static WootRNode CB = new WootRNode((char)0, null, null, 0);
    public final static WootRNode CE = new WootRNode((char)1, null, null, 0);
    
    final private char content;
    final private WootRNode previous, next;
    final private int degree;

    public WootRNode(char content, WootRNode previous, WootRNode next, int degree) {
        this.content = content;
        this.previous = previous;
        this.next = next;
        this.degree = degree;
    }
    
    public WootRNode(char content, WootRNode previous, WootRNode next) {
        this.content = content;
        this.previous = previous;
        this.next = next;
        this.degree = Math.max(previous.degree, next.degree);
    }
    
    public char getContent() {
        return content;
    }

    public WootRNode getNext() {
        return next;
    }

    public WootRNode getPrevious() {
        return previous;
    }

    public WootRNode clone() {
        return new WootRNode(content, previous==null ? null : previous.clone(),
                next==null ? null : next.clone(), degree);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final WootRNode other = (WootRNode) obj;
        if (this.content != other.content) {
            return false;
        }
        if (this.previous != other.previous && (this.previous == null || !this.previous.equals(other.previous))) {
            return false;
        }
        if (this.next != other.next && (this.next == null || !this.next.equals(other.next))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + this.content;
        hash = 79 * hash + (this.previous != null ? this.previous.hashCode() : 0);
        hash = 79 * hash + (this.next != null ? this.next.hashCode() : 0);
        return hash;
    }
}
