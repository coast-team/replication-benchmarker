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

import java.util.Collection;
import java.util.List;
import crdt.Operation;
import jbenchmarker.core.SequenceOperation;
import jbenchmarker.core.SequenceOperation.OpType;
import jbenchmarker.logoot.*;

/**
 *
 * @author mehdi urso
 */
public class MuOperation<T> implements Operation {
    
    final private ListIdentifier origin;
    final private ListIdentifier destination;
    final private Collection<Timestamp> oldVersions;
    final private Timestamp timestamp;   
    final private Timestamp target;   
    final private T content;
    final private OpType type; 

    public MuOperation(Timestamp target, ListIdentifier position, ListIdentifier destination, 
            Collection<Timestamp> oldVersions, Timestamp timestamp, T content, OpType type) {
        this.origin = position;
        this.destination = destination;
        this.oldVersions = oldVersions;
        this.timestamp = timestamp;
        this.target = target;
        this.content = content;
        this.type = type;
    }


    ListIdentifier getOrigin() {
        return origin;
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

    public T getContent() {
        return content;
    }

    public OpType getType() {
        return type;
    }

    Timestamp getTarget() {
        return target;
    }

    // FIXME: shoud clone the operation and its parameters
    @Override
    public Operation clone() {
        return new MuOperation(target, origin, destination, oldVersions, timestamp, content, type);
    }
}
