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
package jbenchmarker.mu;

import crdt.Operation;
import java.util.Collection;
import jbenchmarker.core.SequenceOperation.OpType;
import jbenchmarker.logoot.*;

/**
 *
 * @author mehdi urso
 */
public class MuOperation<T> extends LogootOperation<T> {
    
    final private ListIdentifier destination;
    final private Collection<Timestamp> oldVersions;
    final private Timestamp timestamp;   
    final private Timestamp target;   

    MuOperation(Timestamp target, ListIdentifier position, ListIdentifier destination, 
            Collection<Timestamp> oldVersions, Timestamp timestamp, T content, OpType type) {
        super(type, position, content);
        this.destination = destination;
        this.oldVersions = oldVersions;
        this.timestamp = timestamp;
        this.target = target;
    }

    public ListIdentifier getDestination() {
        return destination;
    }

    Collection<Timestamp> getOldVersions() {
        return oldVersions;
    }

    Timestamp getTimestamp() {
        return timestamp;
    }

    Timestamp getTarget() {
        return target;
    }

    // FIXME: shoud clone the operation and its parameters
    @Override
    public Operation clone() {
        return new MuOperation(target, getPosition(), destination, oldVersions, timestamp, getContent(), getType());
    }
}
