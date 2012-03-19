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
package crdt.simulator.random;

import crdt.CRDT;
import crdt.set.CRDTSet;
import crdt.set.SetOperation;
import collect.VectorClock;
import java.util.Set;

/**
 * A profile that generates operation.
 * @author urso
 */
public abstract class SetOperationProfile<T> implements OperationProfile {
 
    private final double perIns;
    private final RandomGauss r;

    /**
     * Constructor of profile
     * @param perAdd  percentage of ins vs del operation 
     * @param perBlock percentage of block operation (size >= 1)
     * @param avgBlockSize average size of block operation
     * @param sdvBlockSize standard deviation of block operations' size.
     */
    public SetOperationProfile(double perAdd) {
        this.perIns = perAdd;
        this.r = new RandomGauss();
    }
    
    @Override
    public SetOperation nextOperation(CRDT crdt, VectorClock vectorClock) {
        Set<T> s = ((CRDTSet<T>) crdt).lookup();       
        if (!full(s) && (s.isEmpty()  || r.nextDouble() < perIns)) {
            T elem = nextElement();
            while (s.contains(elem)) {
                elem = nextElement(elem);
            }
            return new SetOperation<T>(SetOperation.OpType.add, elem);
        } else {
            Object t[] = s.toArray();
            return new SetOperation<T>(SetOperation.OpType.del, (T) t[r.nextInt(t.length)]);
        }        
    }
    
    abstract public T nextElement();

    abstract public T nextElement(T elem);

    abstract public boolean full(Set<T> s);
}
