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
    final List<OrderedNodeMock<T>> children = new ArrayList<OrderedNodeMock<T>>();
    final T value;

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
        return children;
    }

    @Override
    public OrderedNode<T> createNode(T elem) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

   /* @Override
    public boolean same(OrderedNode<T> other) {
        if (other == null) {
            return false;
        }
        if (this.value != other.getValue() && (this.value == null || !this.value.equals(other.getValue()))) {
            return false;
        }
        if (childrenNumber() != other.childrenNumber()) {
            return false;
        }
        for (int i = 0; i < childrenNumber(); ++i) {
            if (!getChild(i).same(other.getChild(i))) {
                return false;
            }
        }
        return true;
    }*/

    static <T> OrderedNodeMock<T> tree(T ch, Object... cn) {
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
    public String toString() {
        return value + "{" + getElements() + '}';
    }
    
    @Override
    public void setReplicaNumber(int replicaNumber) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
