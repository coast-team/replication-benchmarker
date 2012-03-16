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
package crdt.simulator;

import collect.VectorClock;
import crdt.Operation;

/**
 *
 * @author urso
 */
public class TraceOperation {

    public enum Type {emit, receive, random};
    final private Type type; 
    final private int replica;
    final private VectorClock VC;
    final private Operation op;

    public TraceOperation(Type type, int replica, VectorClock VC, Operation op) {
        this.type = type;
        this.replica = replica;
        this.VC = VC;
        this.op = op;
    }

    public Type getType() {
        return type;
    }

    public VectorClock getVectorClock() {
        return VC;
    }

    public int getReplica() {
        return replica;
    }

    public Operation getOperation() {
        return op;
    }
   
    static public TraceOperation receive(int replica, VectorClock VC) {
        return new TraceOperation(Type.receive, replica, VC, null);
    }
    
    static public TraceOperation emit(int replica, VectorClock VC, Operation op) {
        return new TraceOperation(Type.emit, replica, VC, op);
    }
    
    static public TraceOperation random(int replica, VectorClock VC) {
        return new TraceOperation(Type.random, replica, VC, null);
    }

    @Override
    public String toString() {
        return "TO{VC=" + VC + '}';
    }
}
