/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package collect;

import java.util.*;

/**
 * Implementation of vector with holes using an hash map to a tree set of holes. 
 * @author urso
 */
public class HashVectorWithHoles implements VectorWithHoles {
    Map<Integer, Atom> map = new HashMap<Integer, Atom>();     
    
    @Override
    public boolean contains(int key, int clock) {
        Atom a = map.get(key);
        return a != null && a.belongs(clock);
    }

    @Override
    public void add(int key, int clock) {
        Atom a = map.get(key);
        if (a == null) {
            a = new Atom();
            map.put(key, a);
        }
        a.add(clock);
    }

    static class Atom {
        int end;
        TreeSet<Integer> holes;

        public Atom() {
            this.end = -1;
            holes = new TreeSet<Integer>(); 
        }
        
        void add(int clock) {
            if (clock > end) {
                for (int n = end + 1; n < clock; ++n) {
                    holes.add(n);
                }
                end = clock;
            } else {
               holes.remove(clock);           
            }
        }
        
        boolean belongs(int clock) {
            return (clock == end) || ((clock < end) && !holes.contains(clock));
        }
    }    
}
