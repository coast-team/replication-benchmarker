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
package crdt.set.twophases;

import crdt.CRDTMessage;
import crdt.PreconditionException;
import crdt.set.CRDTSet;
import crdt.set.ConvergentSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 *
 * @author score
 */
public class ConvergentTwoPhaseSet<T> extends ConvergentSet<T>  {

    private HashSet setA; //add element
    private HashSet setR; //for removing elements

    public ConvergentTwoPhaseSet() {
        this.setA = new HashSet<T>();
        this.setR = new HashSet<T>();
    }

    @Override
    public Set lookup() {
        Set result = new HashSet<T>();
        T t;
        Iterator iterator = setA.iterator();
        while (iterator.hasNext()) {
            t = (T) iterator.next();
            if (!setR.contains(t)) {
                result.add(t);
            }
        }
        return result;
    }
    
    @Override
    public void applyOneRemote (CRDTMessage stat) {
        ConvergentTwoPhaseSet tpcs = (ConvergentTwoPhaseSet) stat;
        setA.addAll(tpcs.getSetA());
        setR.addAll(tpcs.getSetR());
    }

    public Set<T> getSetA() {
        return setA;
    }

    public Set<T> getSetR() {
        return setR;
    }
   
    @Override
    public ConvergentTwoPhaseSet<T> innerAdd(T t) throws PreconditionException {
        if (this.getSetR().contains(t)) {
            throw new PreconditionException("This element was deleted");
        }
        setA.add(t);
        return this;
    }

    @Override
    public ConvergentTwoPhaseSet innerRemove(T t) {
        setR.add(t);
        return this;
    }

    @Override
    public boolean contains(T t) {
        return(this.lookup().contains(t));
    }

    @Override
    public CRDTSet<T> create() {
        return new ConvergentTwoPhaseSet();
    }
    
    @Override
    public ConvergentTwoPhaseSet<T> clone() {
        ConvergentTwoPhaseSet<T> clone = new ConvergentTwoPhaseSet<T>();
        clone.setA = (HashSet) this.setA.clone();
        clone.setR = (HashSet) this.setR.clone();
        return clone;
    }
}
