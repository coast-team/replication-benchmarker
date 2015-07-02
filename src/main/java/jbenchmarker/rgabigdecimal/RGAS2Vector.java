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
package jbenchmarker.rgabigdecimal;

import collect.VectorClock;
import java.io.Serializable;

/**
 *
 * @author Roh
 */
public class RGAS2Vector implements Comparable<RGAS2Vector>, Serializable {

    public static final int AFTER = 1;
    public static final int EQUAL = 0;
    public static final int BEFORE = -1;
    protected int sid;
    protected int sum;

    public RGAS2Vector(int ssn, int sid, VectorClock vc) {
        this.sid = sid;
        this.sum = vc.getSum();
    }

    public RGAS2Vector(int sid, VectorClock vc) {
        this(0, sid, vc);
    }

    @Override
    public String toString() {
        return "[" + "," + sid + "," + sum + "]";
    }

    @Override
    public int compareTo(RGAS2Vector s4v) {

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

    public RGAS2Vector(int sid, int sum) {
        this.sid = sid;
        this.sum = sum;
    }

    public RGAS2Vector clone() {
        return new RGAS2Vector(sid, sum);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final RGAS2Vector other = (RGAS2Vector) obj;
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
}
