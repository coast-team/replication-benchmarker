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
package jbenchmarker.rgaTreeList;

import collect.VectorClock;

import java.io.Serializable;

/**
 *
 * @author Roh
 */
public class RGAS4Vector implements Comparable<RGAS4Vector>, Serializable {

	public static final int AFTER = 1;
    public static final int EQUAL = 0;
    public static final int BEFORE = -1;
    protected int sid;
    protected int sum;
    
    RGAS4Vector(int sid, int sum) {
        this.sid = sid;
        this.sum = sum;
    }
    
    public RGAS4Vector(int sid, VectorClock vc) {
        this(sid, vc.getSum());
    }

    @Override
    public String toString() {
        return "[" + sid + "," + sum + "]";
    }

    @Override
    public int compareTo(RGAS4Vector s4v) {

        if (this.sum > s4v.sum) {
            return AFTER;
        } else if (this.sum < s4v.sum) {
            return BEFORE;
        } else { // this.sum == s4v.sum
            if (this.sid > s4v.sid) {
                return AFTER;
            } else if (this.sid < s4v.sid) {
                return BEFORE;
            } else {
                return EQUAL;
            }
        }
    }

    public RGAS4Vector clone() {
        return new RGAS4Vector(sid, sum);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final RGAS4Vector other = (RGAS4Vector) obj;
        if (this.sid != other.sid) {
            return false;
        }
        if (this.sum != other.sum) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + this.sid;
        hash = 79 * hash + this.sum;
        return hash;
    }
    
    public RGAS4Vector follower() {
        return new RGAS4Vector(sid, sum+1);
    }
}
