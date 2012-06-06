/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.tree.orderedtree;

import collect.Node;
import collect.OrderedNode;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author urso
 */
public class OrderedNodeMock<T> implements OrderedNode<T> {
    List<OrderedNodeMock<T>> children = new ArrayList<OrderedNodeMock<T>>();
    T value;

    public OrderedNodeMock(T value) {
        this.value = value;
    }
    
    @Override
    public int childrenNumber() {
        return children.size(); 
    }

    @Override
    public OrderedNode<T> getChild(int p) {
        return children.get(p);
    }

    @Override
    public OrderedNode<T> getChild(Positioned<T> p) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public T getValue() {
        return value;
    }

    @Override
    public Positioned<T> getPositioned(int p) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public PositionIdentifier getNewPosition(int p, T element) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void add(PositionIdentifier pi, T element) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void remove(PositionIdentifier pi, T element) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<? extends OrderedNode<T>> getElements() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public OrderedNode<T> createNode(T elem) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean same(OrderedNode<T> on) {
        if (value != on.getValue() && !value.equals(on.getValue())) {
            return false;
        } 
        if (children.size() != on.childrenNumber()) {
            return false;
        }
        for (int i = 0; i < children.size(); i++) {
            if (!children.get(i).same(on.getChild(i))) {
                return false;
            }
        }
        return true;
    }
    
   static <T> OrderedNodeMock<T> tree(T ch, Object ... cn) {
        OrderedNodeMock on = new OrderedNodeMock(ch);
        for (Object c : cn) {
            if (c instanceof OrderedNode) {
                on.children.add(c);
            } else {
                on.children.add(new OrderedNodeMock(c));
            }
        }
        return on;
    }

    @Override
    public void setReplicaNumber(int replicaNumber) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
