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
package jbenchmarker.woot;

/**
 * A woot identifier : replica and logical clock.
 * @author urso
 */
public class WootIdentifier implements Comparable<WootIdentifier>, WootId {

    public static final WootIdentifier IB = new WootIdentifier(-1, 0);
    public static final WootIdentifier IE = new WootIdentifier(-1, 1);
    
    public WootIdentifier(int replica, int clock) {
        this.replica = replica;
        this.clock = clock;
    }
    private int replica;
    private int clock;

    public int getClock() {
        return clock;
    }

    public int getReplica() {
        return replica;
    }

    @Override
    public int compareTo(WootIdentifier t) {
        if (this.replica == t.replica) {
            return this.clock - t.clock;
        } else {
            return this.replica - t.replica;
        }
    }

    @Override
    public WootIdentifier clone() {
        return new WootIdentifier(replica, clock);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final WootIdentifier other = (WootIdentifier) obj;
        if (this.replica != other.replica) {
            return false;
        }
        if (this.clock != other.clock) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 13 * hash + this.replica;
        hash = 13 * hash + this.clock;
        return hash;
    }

    @Override
    public String toString() {
        return "[" + replica + ',' + clock + ']';
    }

    @Override
    public WootIdentifier getId() {
        return this;
    }
}
