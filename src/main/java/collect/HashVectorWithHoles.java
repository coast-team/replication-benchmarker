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
