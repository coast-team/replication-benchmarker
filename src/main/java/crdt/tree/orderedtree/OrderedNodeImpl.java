/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.tree.orderedtree;

import collect.AbstractNode;
import collect.Node;
import collect.OrderedNode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author urso
 */
class OrderedNodeImpl<T> extends AbstractNode<T> implements OrderedNode<T> {    
    final private List<OrderedNodeImpl<T>> children;
    final PositionIdentifier pi;
    final private Positioned<T> positioned;

    public OrderedNodeImpl(T value, AbstractNode<T> father, PositionIdentifier pi) {
        super(value, father);
        this.children = new ArrayList<OrderedNodeImpl<T>>();
        this.pi = pi;
        this.positioned = pi == null ? null : new Positioned<T>(pi, value);
    }
    
    @Override
    protected List<OrderedNodeImpl<T>> getChildren() {
        return children;
    }
    
    void add(int p, Positioned<T> elem) {
        OrderedNodeImpl<T> n = new OrderedNodeImpl<T>(elem.getElem(), this, elem.getPi());
        children.add(p, n);
    }
    
    void place(int p, OrderedNodeImpl<T> n) {
        children.add(p, n);
    }
    
    OrderedNodeImpl<T> remove(Positioned<T> elem) {
        OrderedNodeImpl<T> n = getChild(elem);
        children.remove(n);
        return n;
    }
    
    @Override
    public OrderedNodeImpl<T> getChild(int p) {
        return children.get(p);
    }
    
    public PositionIdentifier getPosition() {
        return pi;
    }
    
    // TODO : HASH map Positioned -> Node
    OrderedNodeImpl<T> getChild(Positioned<T> p) {
        for (OrderedNodeImpl<T> c : children) {
            if (c.value.equals(p.getElem()) && c.pi.equals(p.getPi())) {
                return c;
            }
        }
        return null;
    }
    
    @Override
    public void deleteChild(Collection<? extends Node<T>> nodeToDelet) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getLevel() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    Positioned<T> getPositioned() {
        return positioned;
    }

    boolean same(OrderedNode<T> on) {
        OrderedNodeImpl<T> o = (OrderedNodeImpl<T>) on;
        if (value != o.value && !value.equals(o.value)) {
            return false;
        } 
        if (children.size() != o.children.size()) {
            return false;
        }
        for (int i = 0; i < children.size(); i++) {
            if (!children.get(i).same(o.children.get(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return "{" + value + children + '}';
    }
    
}
