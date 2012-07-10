/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.tree.orderedtree;

import collect.OrderedNode;
import java.util.List;
import jbenchmarker.logoot.LogootDocument;
import jbenchmarker.logoot.LogootIdentifier;
import jbenchmarker.logoot.LogootStrategy;

/**
 *
 * @author urso
 */
public class LogootTreeNode<T> extends LogootDocument<LogootTreeNode<T>> implements PositionnedNode<T> {
    private final T value;
    
    public LogootTreeNode(T value, int r, int nbBit, LogootStrategy strategy) {
        super(r, nbBit, strategy);
        this.value = value;
    }
    
    @Override
    public T getValue() {
        return value;
    }
    
    @Override
    public int childrenNumber() {
        return document.size()-2; 
    }

    @Override
    public OrderedNode<T> getChild(int p) {
        return document.get(p+1);
    }

    @Override
    public OrderedNode<T> getChild(Positioned<T> p) {
        return document.get(dicho((LogootIdentifier)p.getPi()));
    }

    @Override
    public Positioned<T> getPositioned(int p) {
        return new Positioned<T>(idTable.get(p+1), document.get(p+1).getValue());
    }
    
    @Override
    public PositionIdentifier getNewPosition(int p, T element) {
        return getNewId(p);
    }

    @Override
    public void add(PositionIdentifier id, T elem) {
         int pos = dicho((LogootIdentifier)id);
         idTable.add(pos, (LogootIdentifier)id);
         document.add(pos, createNode(elem));       
    }
    
    @Override
    public void remove(PositionIdentifier id, T elem) {
         int pos = dicho((LogootIdentifier)id);
         idTable.remove(pos);
         document.remove(pos);       
    }

    @Override
    public List<LogootTreeNode<T>> getElements() {
        return document.subList(1, document.size()-1);
    }

    @Override
    public LogootTreeNode<T> createNode(T elem) {
        return new LogootTreeNode<T>(elem, replicaNumber, nbBit, strategy);
    }

/*    public boolean same(OrderedNode<T> other) {
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
    }
    */
    @Override
    public String toString() {
        return value + "{" + getElements() + '}';
    }
}
