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
package jbenchmarker.core;

import jbenchmarker.trace.TraceOperation;

/**
 * An operation for a replication algorithm.
 * @author urso
 */
public abstract class Operation {
    final private TraceOperation originalOp;       // Trace operation issuing this one

    public TraceOperation getOriginalOp() {
        return originalOp;
    }
    
    public Operation(TraceOperation o) {
        this.originalOp = o;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Operation other = (Operation) obj;
        if (this.originalOp != other.originalOp && (this.originalOp == null || !this.originalOp.equals(other.originalOp))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 17 * hash + (this.originalOp != null ? this.originalOp.hashCode() : 0);
        return hash;
    }
    
    abstract public Operation clone();
}
