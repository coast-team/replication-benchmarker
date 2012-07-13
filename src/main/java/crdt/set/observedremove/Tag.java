/**
 * Replication Benchmarker
 * https://github.com/score-team/replication-benchmarker/
 * Copyright (C) 2012 LORIA / Inria / SCORE Team
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
package crdt.set.observedremove;

/**
 *
 * @author score
 */
public class Tag {

    private int numReplica;
    private int numOp;

    public Tag() {
        numReplica = 0;
        numOp = 0;
    }

    public Tag(int r, int o) {
        numReplica = r;
        numOp = o;
    }
    
    public int getNumOp() {
        return numOp;
    }
    
    public int getNumReplica() {
        return numReplica;
    }
    
    public void setNumOp(int o) {
        numOp = o;
    }
    
    public void setNumReplica(int n) {
        numReplica = n;
    }
    
    public Tag clone() {
        return new Tag(this.numReplica,this.numOp);
    }

    @Override
    public String toString() {
        return "(" + numReplica + ", " + numOp + ')';
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Tag other = (Tag) obj;
        if (this.numReplica != other.numReplica) {
            return false;
        }
        if (this.numOp != other.numOp) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 73 * hash + this.numReplica;
        hash = 73 * hash + this.numOp;
        return hash;
    }
}
