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
package jbenchmarker.sim;

import jbenchmarker.trace.TraceOperation;

/**
 * Profile to generate random operations.
 * @author urso
 */
public interface OperationProfile {

    /**
     * Randomly generates a string with [a-z] characters
     */
    String nextContent();

    /**
     *  Randomly generates an offset from this position for a delete operation
     */
    int nextOffset(int position, int length);

    /**
     *  Randomly generates a position for an operation
     */
    int nextPosition(int length);
    
    /**
     *  Randomly generates a type (ins, del) for an operation
     */
    TraceOperation.OpType nextType();    
}
