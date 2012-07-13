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
package crdt.set.counter;

import crdt.CRDT;
import crdt.CRDTMessage;
import crdt.set.CRDTSet;
import crdt.set.ConvergentSet;
import java.util.*;

/**
 *
 * @author score
 */
public class ConvergentCounterSet<T>  extends ConvergentSet<T>  {

    HashMap<T, Integer> mapA;//add
    HashMap<T, Integer> mapR;//del
    private Set<T> lookup;

    public ConvergentCounterSet() {
        this.mapA = new HashMap<T, Integer>();
        this.mapR = new HashMap<T, Integer>();
        lookup = new HashSet();
    }

    @Override
    public Set<T> lookup() {
        
        return lookup;
    }

    @Override
    public void applyOneRemote(CRDTMessage statv) {
        ConvergentCounterSet<T> that = (ConvergentCounterSet<T>) statv;

        // least upper bound 
        for (T t : that.mapA.keySet()) {
            boolean there = lookup.contains(t);
            int lA = this.mapA.containsKey(t) ? this.mapA.get(t) : 0,
                    lR = this.mapR.containsKey(t) ? this.mapR.get(t) : 0,
                    rA = that.mapA.containsKey(t) ? that.mapA.get(t) : 0,
                    rR = that.mapR.containsKey(t) ? that.mapR.get(t) : 0;
            if (rA > lA) {
                this.mapA.put(t, rA);
                if (!there && rA > Math.max(lR, rR)) {
                    lookup.add(t);
                    notifyAdd(t);
                }
            }
            if (rR > lR) {
                this.mapR.put(t, rR);
                if (there && rR >= Math.max(lA, rA)) {
                    lookup.remove(t);
                    notifyDel(t);
                }
            }
        }
    }

    @Override
    public ConvergentCounterSet<T> innerAdd(T t) {
        if (mapA.containsKey(t) && mapR.get(t) >= mapA.get(t)) {
            mapA.put(t, mapA.get(t) + 1);
        } else {
            mapA.put(t, 1);
        }
        lookup.add(t);
        return this;
    }

    @Override
    public ConvergentCounterSet<T> innerRemove(T t) {
        if (mapR.containsKey(t)) {
        //    newOp.setCounter(mapR.get(t) + 1);
            mapR.put(t, mapR.get(t) + 1);
        } else {
            mapR.put(t, 1);
        }

        lookup.remove(t);
        return this;
    }

    @Override
    public boolean contains(T t) {
        return (this.lookup().contains(t));
    }

    @Override
    public CRDTSet<T> create() {
        return new ConvergentCounterSet<T>();
    }

    @Override
    public ConvergentCounterSet<T> clone() {
        ConvergentCounterSet<T> clone = new ConvergentCounterSet<T>();
        clone.mapA = (HashMap<T, Integer>) this.mapA.clone();
        clone.mapR = (HashMap<T, Integer>) this.mapR.clone();
        return clone;
    }

  
}
