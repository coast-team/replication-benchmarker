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
package collect;

import java.util.*;

/**
 * HashMapSet is a HashSet allow many elements with an key.
 *
 * @param <K> is a key
 * @param <T> is an type of element T
 * @author Stéphane Martin
 */
public class HashMapSet<K, T> {

    private HashMap<K, HashSet<T>> hashmap;
    private HashSet<T> values;
    //private int size = 0;

    /**
     * Constructor of object
     *
     */
    public HashMapSet() {
        hashmap = new HashMap();
        values = new HashSet();
    }

    /**
     * Check if t is present in values set
     *
     * @param t element to check
     * @return if t is in values set.
     */
    public boolean contains(T t) {
        return values.contains(t);
    }

    /**
     * Remove one element t placed in set witch have k Key.
     *
     * @param k
     * @param t
     * @return true if element was present.
     */
    public boolean remove(K k, T t) {
        HashSet hs = hashmap.get(k);

        if (hs != null && hs.remove(t)) {
            values.remove(t);
            if (hs.isEmpty()) {
                hashmap.remove(k);
            }

            return true;
        }
        return false;

    }

    /**
     * Remove all element witch have key k
     *
     * @param k
     * @return removed elements set
     */
    public HashSet<T> removeAll(K k) {
        HashSet hs = hashmap.get(k);
        if (hs != null) {
            values.removeAll(hs);
            return hashmap.remove(k);
        }
        return null;

    }

    /**
     * get all element with key k
     *
     * @param o
     * @return all element
     */
    public HashSet<T> getAll(K o) {
        return hashmap.get(o);
    }

    /**
     *
     * @return numbers of stored elements
     */
    public int size() {
        return values.size();
    }

    /**
     * Check if size is null
     *
     * @return empty
     */
    public boolean isEmpty() {
        return values.isEmpty();
    }

    /**
     *
     * @param k
     * @return True if key k is present
     */
    public boolean containsKey(K k) {
        return hashmap.containsKey(k);
    }

    /**
     *
     * @param k
     * @param t
     * @return true if element t is stored with key k.
     */
    public boolean containsValue(K k, T t) {
        HashSet<T> hs = hashmap.get(k);

        return hs != null && hs.contains(t);
    }

    /**
     * Store an element t with key k
     *
     * @param k
     * @param t
     * @return the set with the element t has been stored
     */
    public HashSet put(K k, T t) {
        HashSet<T> hs = hashmap.get(k);
        if (hs == null) {
            hs = new HashSet<T>();
            hashmap.put(k, hs);
        }
        values.add(t);
        hs.add(t);
        return hs;

    }
   

    /**
     * Remove all elements and keys
     */
    public void clear() {
        hashmap.clear();
        values.clear();
    }

    /**
     *
     * @return the set of keys
     */
    public Set<K> keySet() {
        return hashmap.keySet();
    }
   

    /**
     *
     * @return return set of all stored elements
     */
    public Collection values() {
        return values;
    }

    /**
     *
     * @return Iterator of values.
     */
    public Iterator iterator() {
        return values.iterator();
    }

    /**
     *
     * @return return set of entry
     */
    public Set entrySet() {
        return hashmap.entrySet();
    }

    /**
     * Return one element contained in key k.
     * @param k the key
     * @return one element stored under k.
     */
    public T getOne(K k) {
        Set<T> set;
        if ((set = this.getAll(k)) != null) {
            Iterator<T> it = set.iterator();
            return it.hasNext() ? it.next() : null;
        }
        return null;
    }
    /**
     * Add all elements in h to k set.
     * @param k Is the key.
     * @param h and set to add under k.
     */
    public void putAll(K k, HashSet<T> h){
        HashSet set=hashmap.get(k);
        if (set==null){
            hashmap.put(k, (HashSet<T>)h.clone());
        }else{
            set.addAll(h);
        }
        values.addAll(h);
    }
}
