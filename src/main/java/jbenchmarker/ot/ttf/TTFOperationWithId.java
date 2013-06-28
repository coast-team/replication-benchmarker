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

import jbenchmarker.core.SequenceOperation.OpType;

/**
 *
 * @param <T> type of characters
 * @author oster
 *
 * TTFOperation Operation Add,Del,up, of TTFsequence
 */
public class TTFOperationWithId<T> extends TTFOperation<T> {

    private int siteId;

    /**
     *
     * @return site id or replicat number
     */
    public int getSiteId() {
        return siteId;
    }

    /**
     * make TTF Operation
     *
     * @param type Add,Del, Up or unsupported
     * @param pos Position obsolute
     * @param content Character
     * @param siteId Id site or replicat which sent this operation
     */
    public TTFOperationWithId(OpType type, int pos, T content, int siteId) {
        super(type, pos, content);
        this.siteId = siteId;
    }


    /**
     * clone operation
     *
     * @return new instance of the operation
     */
    @Override
    public TTFOperationWithId<T> clone() {
        return new TTFOperationWithId(this.getType(), this.getPosition(), this.getContent(), siteId);
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
        sb.append(this.getPosition());
//        if (OpType.insert == this.getType()) {
            sb.append(',');
            sb.append(this.getContent());
            sb.append(',');
            sb.append(this.siteId);
//        }
        sb.append(')');
        return sb.toString();
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 83 * hash + this.siteId;
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
        final TTFOperationWithId<T> other = (TTFOperationWithId<T>) obj;
        if (this.siteId != other.siteId) {
            return false;
        }
        return super.equals(obj);
    }

}
