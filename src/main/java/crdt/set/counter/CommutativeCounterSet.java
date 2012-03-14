/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.set.counter;

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
    protected void applyOneRemote(CommutativeSetMessage<T> op) {
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
    public CounterMessage innerAdd(T t) {        
        int c = map.containsKey(t) ? -map.get(t) + 1 : 1;       
        map.put(t, 1);
        lookup.add(t);
        return new CounterMessage(t, c);
    }

    @Override
    public CounterMessage innerRemove(T t) {
        int c = -map.get(t);
        map.remove(t);
        lookup.remove(t);
        return new CounterMessage(t, c);
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
