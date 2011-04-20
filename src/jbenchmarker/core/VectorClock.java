/**
 *   This file is part of ReplicationBenchmark.
 *
 *   ReplicationBenchmark is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   ReplicationBenchmark is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with ReplicationBenchmark.  If not, see <http://www.gnu.org/licenses/>.
 *
 **/

package jbenchmarker.core;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

/**
 * Implementation of a vector clock. 
 * A map replica identifier -> logical clock.
 * @author urso
 * @author oster
 */
public class VectorClock extends TreeMap<Integer, Integer> {

    public VectorClock() {
        super();
    }

    // TODO: test me, plz
    public VectorClock(VectorClock siteVC) {
        super(siteVC);
    }

    /*
     * Is this VC is ready to integrate O ?
     * true iff VCr = Or - 1 && for all i!=r, VCi >= Oi
     */
    public boolean readyFor(int r, VectorClock O) {
        if (this.getSafe(r) != O.get(r) - 1) {
            return false;
        }
        Iterator<Map.Entry<Integer, Integer>> it = O.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, Integer> e = it.next();
            if ((e.getKey() != r) && (this.getSafe(e.getKey()) < e.getValue())) {
                return false;
            }
        }
        return true;
    }
    
    /** 
     * Get the sum of all entries
     * added by Roh. 
     */ 
    public int getSum(){
        int sum = 0;
        Iterator<Map.Entry<Integer, Integer>> it = this.entrySet().iterator();
        while (it.hasNext()) sum+= it.next().getValue();        
        return sum;
    }
    
    /*
     * Increment an entry.
     */
    public void inc(int r) {
        put(r, getSafe(r) + 1);
    }

    /*
     * Returns the entry for replica r. 0 if none.
     */
    public int getSafe(int r) {
        Integer v = get(r);
        return (v != null) ? v : 0;
    }

    /*
     * Is this VC > T ? 
     * true iff for all i, VCi >= Ti and exists j VCj > Tj
     */
    public boolean greaterThan(VectorClock T) {
        boolean gt = false;
        Iterator<Map.Entry<Integer, Integer>> it = T.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, Integer> i = it.next();
            if (this.getSafe(i.getKey()) < i.getValue()) {
                return false;
            } else if (this.getSafe(i.getKey()) > i.getValue()) {
                gt = true;
            }
        }
        if (gt) {
            return true;
        }
        it = this.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, Integer> i = it.next();
            if (T.getSafe(i.getKey()) < i.getValue()) {
                return true;
            }
        }
        return false;
    }

    /*
     * Is this VC // T ? 
     * true iff nor VC > T nor T > VC
     */
    public boolean concurrent(VectorClock T) {
        return !(this.greaterThan(T) || T.greaterThan(this));
    }

    /**
     * Sets each entry of the VC to max(VCi, Oi)
     */
    public void upTo(VectorClock O) {
        for (Entry<Integer, Integer> k : O.entrySet()) {
            if (k.getValue() > this.getSafe(k.getKey())) {
                this.put(k.getKey(), k.getValue());
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        final VectorClock other = (VectorClock) obj;

        Set<Integer> h = new HashSet<Integer>(this.keySet());
        h.addAll(other.keySet());

        for (Integer k : h) {
            if (this.getSafe(k) != other.getSafe(k)) {
                return false;
            }
        }
        return true;
    }

    /*
     * computes minimal vector from current and vector clocks provided in parameters.
     * for each vc in {this} U otherVectorClocks, for each i in min, min[i] <= vc[i] 
     */
    public VectorClock min(Collection<VectorClock> otherVectorClocks) {
        VectorClock min = new VectorClock(this);

        for (VectorClock clock : otherVectorClocks) {
            Iterator<Map.Entry<Integer, Integer>> componentIterator = clock.entrySet().iterator();
            while (componentIterator.hasNext()) {
                Map.Entry<Integer, Integer> i = componentIterator.next();
                Integer key = i.getKey();
                min.put(key, Math.min(min.getSafe(key), i.getValue()));
            }
        }
        return min;
    }
}
