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
package crdt.tree.wordtree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Immutable list to represent words 
 * @author urso
 */
public class Word<T> implements List<T>, Serializable  {
    
    private final List<T> bak;
    private final int hash;

    public Word() {
        bak = Collections.EMPTY_LIST;
        hash = bak.hashCode();
    }

    public Word(List<T> l) {
        bak = new ArrayList(l);
        hash = bak.hashCode();
    }
    
    public Word(List<T> l, T t) {
        bak = new ArrayList(l);
        bak.add(t);
        hash = bak.hashCode();
    }
    
    private Word(Word<T> l, int i, int i1) {
        bak = l.bak.subList(i, i1);
        hash = bak.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj.getClass() == this.getClass()) {
            final Word<T> other = (Word<T>) obj;
            return (this.hash == other.hash) && this.bak.equals(other.bak); 
        } else if (obj instanceof List) {
            return bak.equals(obj);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return hash;
    }
    
    public T getLast() {
        return bak.get(bak.size()-1);
    }

    @Override
    public String toString() {
        return bak.toString();
    }
    
    @Override
    public int size() {
        return bak.size();
    }

    @Override
    public boolean isEmpty() {
        return bak.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return bak.contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        return bak.iterator();
    }

    @Override
    public Object[] toArray() {
        return bak.toArray();
    }

    @Override
    public <T> T[] toArray(T[] ts) {
        return bak.toArray(ts);
    }

    @Override
    public boolean add(T e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean containsAll(Collection<?> clctn) {
        return bak.containsAll(clctn);
    }

    @Override
    public boolean addAll(Collection<? extends T> clctn) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean addAll(int i, Collection<? extends T> clctn) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean removeAll(Collection<?> clctn) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean retainAll(Collection<?> clctn) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public T get(int i) {
        return bak.get(i);
    }

    @Override
    public T set(int i, T e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void add(int i, T e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public T remove(int i) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int indexOf(Object o) {
        return bak.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return bak.indexOf(o);
    }

    @Override
    public ListIterator<T> listIterator() {
        return new WordIterator<T>(bak.listIterator());
    }

    @Override
    public ListIterator<T> listIterator(int i) {
        return new WordIterator<T>(bak.listIterator(i));
    }

    @Override
    public List<T> subList(int i, int i1) {
        return new Word<T>(this, i, i1);
    }

    public List<T> getFather() {
        return new Word<T>(this, 0, bak.size()-1);
    }

    private class WordIterator<T> implements ListIterator<T> {

        ListIterator<T> it;

        public WordIterator(ListIterator<T> it) {
            this.it = it;
        }

        @Override
        public boolean hasNext() {
            return it.hasNext();
        }

        @Override
        public T next() {
            return it.next();
        }

        @Override
        public boolean hasPrevious() {
            return it.hasPrevious();
        }

        @Override
        public T previous() {
            return it.previous();
        }

        @Override
        public int nextIndex() {
            return it.nextIndex();
        }

        @Override
        public int previousIndex() {
            return it.previousIndex();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void set(T e) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void add(T e) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
}
