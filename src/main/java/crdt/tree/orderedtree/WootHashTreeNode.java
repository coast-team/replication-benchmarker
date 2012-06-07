/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.tree.orderedtree;

import collect.OrderedNode;
import java.util.AbstractList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jbenchmarker.core.SequenceMessage;
import jbenchmarker.woot.WootIdentifier;
import jbenchmarker.woot.WootOperation;
import jbenchmarker.woot.WootPosition;
import jbenchmarker.woot.wooth.WootHashDocument;
import jbenchmarker.woot.wooth.WootHashNode;

/**
 *
 * @author urso
 */
class WootHashTreeNode<T> extends WootHashDocument<WootHashTreeNode<T>> implements OrderedNode<T> {
    private final T value;
    private Map<WootIdentifier, WootPosition> positions = new HashMap<WootIdentifier, WootPosition>();

    public WootHashTreeNode(T value, int replicaNumber) {
        super(replicaNumber);
        this.value = value;
    }

    @Override
    public void apply(SequenceMessage op) {    
        super.apply(op);
        WootOperation wop = (WootOperation) op;
        positions.put(wop.getId(), new WootPosition(wop.getId(), wop.getIp(), wop.getIn()));
    }
    
    
    @Override
    public T getValue() {
        return value;
    }
    
    @Override
    public int childrenNumber() {
        return viewLength(); 
    }

    @Override
    public OrderedNode<T> getChild(int p) {
        return getVisible(p).getContent();
    }

    @Override
    public OrderedNode<T> getChild(Positioned<T> p) {
        return (OrderedNode<T>) find((WootIdentifier) p.getPi());
    }

    @Override
    public Positioned<T> getPositioned(int p) {        
        WootHashNode<WootHashTreeNode<T>> wn = getVisible(p);
        return new Positioned<T>(positions.get(wn.getId()), wn.getContent().getValue());
    }
    
    @Override
    public PositionIdentifier getNewPosition(int p, T element) {
        WootHashNode<WootHashTreeNode<T>>  wp = getPrevious(p), wn = getNext(wp);
        return new WootPosition(nextIdentifier(), wp.getId(), wn.getId()); 
    }

    @Override
    public void add(PositionIdentifier id, T elem) {
        WootPosition wp = (WootPosition) id;
        add(wp.getId(), createNode(elem), wp.getIp(), wp.getIn());    
    }
    
    @Override
    public void remove(PositionIdentifier id, T elem) {
        del(((WootPosition) id).getId());
    }

    // TODO : redo using sequential list
    @Override
    public List<WootHashTreeNode<T>> getElements() {
        return new AbstractList<WootHashTreeNode<T>>() {

            @Override
            public WootHashTreeNode<T> get(int i) {
                return getVisible(i).getContent();
            }

            @Override
            public int size() {
                return viewLength(); 
            }
        };
    }

    @Override
    public WootHashTreeNode<T> createNode(T elem) {
        return new WootHashTreeNode<T>(elem, getReplicaNumber());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final WootHashTreeNode<T> other = (WootHashTreeNode<T>) obj;
        if (this.value != other.value && (this.value == null || !this.value.equals(other.value))) {
            return false;
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 71 * hash + (this.value != null ? this.value.hashCode() : 0);
        return hash;
    }
    
    @Override
    public String toString() {
        return value + "{" + getElements() + '}';
    }
}
