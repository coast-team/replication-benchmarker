/**
 * Replication Benchmarker
 * https://github.com/score-team/replication-benchmarker/
 * Copyright (C) 2013 LORIA / Inria / SCORE Team
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
package collect;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author urso
 */
public class HashBiMapSet<K,V> {
    private HashMap<K,Set<V>> map;
    //private
        public    HashMap<V,K> inv;
  
    public HashBiMapSet() {        
        map = new HashMap<K,Set<V>>();
        inv = new HashMap<V,K>();
    }

    public void clear() {
        inv.clear();
        map.clear();
    }

    @Override
    public Object clone() {
        HashBiMapSet<V,K> clone = new HashBiMapSet<V,K>();
        clone.map = (HashMap<V, Set<K>>) this.map.clone();
        clone.inv = (HashMap<K, V>) this.inv.clone();
        return clone;
    }

    public void put(K k, V v) {
        if (!map.containsKey(k)) {
            map.put(k, new HashSet<V>());
        }
        this.map.get(k).add(v);
        inv.put(v, k);
    }

    public void putAll(K k, Set<V> vs) {
        if (!map.containsKey(k)) {
            map.put(k, new HashSet<V>());
        }
        this.map.get(k).addAll(vs);
        for (V v : vs) {
            inv.put(v, k);
        }
    }

    public Set<V> removeKey(K k) {
        Set<V> old = map.remove(k);
        inv.keySet().removeAll(old);
        return old;
    }

    public boolean remove(K k, V v) {
        boolean ret = false;
        if (map.containsKey(k)) {
            ret = map.get(k).remove(v);
            if (map.get(k).isEmpty()) {
                map.remove(k);
            }
        }
        inv.remove(v);
        return ret;
    }

    public boolean removeAll(K k, Set<V> vs) {
        boolean ret = false;
        if (map.containsKey(k)) {
            ret = map.get(k).removeAll(vs);
            if (map.get(k).isEmpty()) {
                map.remove(k);
            }
        }
        inv.keySet().removeAll(vs);
        return ret;
    }

    // not mapped
    public Set<V> get (K k) {
        return map.get(k);
    }
    
    public K getInverse(V v) {
        return inv.get(v);
    }
    
    public int size() {
        return inv.size();
    }
    
    public Set<V> valueSet() {
        return inv.keySet();
    }
}
