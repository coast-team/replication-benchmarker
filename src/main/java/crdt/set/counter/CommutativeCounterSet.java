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

import crdt.CRDTMessage;
import crdt.OperationBasedOneMessage;
import crdt.set.CRDTSet;
import crdt.set.CommutativeSet;
import crdt.set.CommutativeSetMessage;
import java.util.*;

/**
 *
 * @author score
 */
public class CommutativeCounterSet<T> extends CommutativeSet<T>{
    
     final private HashMap<T, Integer> map;
     Set<T> lookup;

    public CommutativeCounterSet() {
        this.map = new HashMap<T, Integer>();
        lookup = new HashSet<T>();
    }

    @Override
    public Set<T> lookup() {
        return lookup;
    }
    
    @Override
    protected void applyOneInRemote(CommutativeSetMessage<T> op) {
        CounterMessage<T> countOp = (CounterMessage<T>) op;
        T elem = countOp.getContent();
        int oldc = (map.containsKey(elem) ? map.get(elem) : 0);
        int newc = oldc + countOp.getCounter();
        
        if (oldc <= 0 && newc > 0) {
            lookup.add(elem);
        } else if (oldc > 0 && newc <= 0) {
            lookup.remove(elem);            
        } 
        if (newc == 0) {
            map.remove(elem);
        } else {
            map.put(elem, newc);
        }
    }
    
    Map<T, Integer> getMap() {
        return map;
    }

    @Override
    public OperationBasedOneMessage innerAdd(T t) {        
        int c = map.containsKey(t) ? -map.get(t) + 1 : 1;       
        map.put(t, 1);
        lookup.add(t);
        return new OperationBasedOneMessage(new CounterMessage(t, c));
    }

    @Override
    public OperationBasedOneMessage innerRemove(T t) {
        int c = -map.get(t);
        map.remove(t);
        lookup.remove(t);
        return new OperationBasedOneMessage(new CounterMessage(t, c));
    }

    @Override
    public boolean contains(T t) {
        return lookup.contains(t);
    }

    @Override
    public CRDTSet<T> create() {
        return new CommutativeCounterSet<T>();
    }

   


}
