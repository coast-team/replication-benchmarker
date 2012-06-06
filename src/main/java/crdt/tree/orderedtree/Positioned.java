/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.tree.orderedtree;

/**
 *
 * @author urso
 */ 

public class Positioned<T> {
    final private PositionIdentifier pi;
    final private T elem;

    public Positioned(PositionIdentifier pi, T elem) {
        this.pi = pi;
        this.elem = elem;
    }

    public T getElem() {
        return elem;
    }

    public PositionIdentifier getPi() {
        return pi;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Positioned<T> other = (Positioned<T>) obj;
        if (this.pi != other.pi && (this.pi == null || !this.pi.equals(other.pi))) {
            return false;
        }
        if (this.elem != other.elem && (this.elem == null || !this.elem.equals(other.elem))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + (this.pi != null ? this.pi.hashCode() : 0);
        hash = 97 * hash + (this.elem != null ? this.elem.hashCode() : 0);
        return hash;
    }
}
