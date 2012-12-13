/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package collect;

import java.util.*;

/**
 *
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
        int start, end;
        TreeSet<Integer> holes;

        public Atom() {
            this.start = -1;
            this.end = -1;
            holes = new TreeSet<Integer>(); 
        }
        
        void add(int clock) {
            if (clock == end + 1) {
                end++;
            } else if (clock > end + 1) {
                holes.addAll(interval(end + 1, clock - 1));
                end = clock;
            } else {
               holes.remove(clock);
               if (holes.isEmpty()) {
                   start = end;
               } else {
                   start = holes.first() - 1;
               }
            }
        }
        
        boolean belongs(int clock) {
            return (clock <= start) || (clock == end) || ((clock < end) && !holes.contains(clock));
        }

        private static Collection<? extends Integer> interval(int i, int j) {
            Collection<Integer> s = new ArrayList<Integer>(j-i+1);
            for (int n = i; n <= j; ++n) {
                s.add(n);
            }
            return s;
        }
    }
    
}
