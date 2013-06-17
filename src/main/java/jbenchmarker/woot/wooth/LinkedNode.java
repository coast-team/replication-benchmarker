/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker.woot.wooth;

import jbenchmarker.woot.WootIdentifier;
import jbenchmarker.woot.WootNode;

/**
 *
 * @author urso
 */
public abstract class LinkedNode<T> extends WootNode<T> {

    private LinkedNode next;
    final private int degree;

    public LinkedNode(WootIdentifier id, T content, LinkedNode<T> next, int degree) {
        super(id, content);
        this.next = next;
        this.degree = degree;
    }

    public int getDegree() {
        return degree;
    }

    public LinkedNode getNext() {
        return next;
    }

    public void setNext(LinkedNode next) {
        this.next = next;
    }

    /**
     * May not halt. Costly!
     *
     * @param obj
     * @return the lists are equals
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final LinkedNode<T> other = (LinkedNode<T>) obj;
        if (this.next != other.next && (this.next == null || !this.next.equals(other.next))) {
            return false;
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 97 * hash + (this.next != null ? this.next.hashCode() : 0);
        return hash;
    }
}
