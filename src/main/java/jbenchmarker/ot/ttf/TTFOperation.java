/**
 * Replication Benchmarker
 * https://github.com/score-team/replication-benchmarker/ Copyright (C) 2013
 * LORIA / Inria / SCORE Team
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package jbenchmarker.ot.ttf;

import java.io.Serializable;
import crdt.Operation;
import jbenchmarker.core.SequenceOperation.OpType;

/**
 *
 * @param <T> type of characters
 * @author oster
 *
 * TTFOperation Operation Add,Del,up, of TTFsequence
 */
public class TTFOperation<T> implements Operation, Serializable {

    private int pos;
    private T content;
    private OpType type;

    /**
     * make TTF Operation
     *
     * @param type Add,Del, Up or unsupported
     * @param pos Position obsolute
     * @param content Character
     * @param siteId Id site or replicat which sent this operation
     */
    public TTFOperation(OpType type, int pos, T content) {
        this.pos = pos;
        this.type = type;
        this.content = content;
    }

    /**
     * @return type of operation
     */
    public OpType getType() {
        return this.type;
    }

    /**
     * Change type of operation
     */
    public void setType(OpType opType) {
        this.type = opType;
    }
    
    /**
     *
     * @return position of element
     */
    public int getPosition() {
        return this.pos;
    }

    /**
     * change position of element
     *
     * @param pos position
     */
    public void setPosition(int pos) {
        this.pos = pos;
    }

    /**
     * @return Character of operation
     */
    public T getContent() {
        return this.content;
    }

    /**
     * clone operation
     *
     * @return new instance of the operation
     */
    @Override
    public TTFOperation<T> clone() {
        return new TTFOperation(this.getType(), this.getPosition(), this.content);
    }

    /**
     * return a string representation of operation
     *
     * @return String
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getType());
        sb.append('(');
        sb.append(this.pos);
//        if (OpType.insert == this.getType()) {
            sb.append(',');
            sb.append(this.content);
//        }
        sb.append(')');
        return sb.toString();
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 71 * hash + this.pos;
        hash = 71 * hash + (this.content != null ? this.content.hashCode() : 0);
        hash = 71 * hash + (this.type != null ? this.type.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TTFOperation<T> other = (TTFOperation<T>) obj;
        if (this.pos != other.pos) {
            return false;
        }
        if (this.content != other.content && (this.content == null || !this.content.equals(other.content))) {
            return false;
        }
        if (this.type != other.type) {
            return false;
        }
        return true;
    }
}
