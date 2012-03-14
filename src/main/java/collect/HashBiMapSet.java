/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
