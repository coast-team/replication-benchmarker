/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package collect;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author urso
 */
public abstract class AbstractNode<T> implements Node<T> {
    final protected T value;
    protected AbstractNode<T> father;

    public AbstractNode(T value, AbstractNode<T> father) {
        this.value = value;
        this.father = father;
    }
    
    abstract protected Collection<? extends AbstractNode<T>> getChildren();
    
    
    @Override
    public Collection<? extends Node<T>> getChildrenCopy() {
        return new LinkedList(getChildren());
    }
     
    @Override
    public int getChildrenNumber() {
        return getChildren().size();
    }
    
    @Override
    public boolean isChildren(Node<T> n) {
        return getChildren().contains((NodeImpl)n);
        /* TODO : Possible qu'un this.children.containsKey(n.getValue()) 
         * serait plus rapide*/
    }
    
    @Override
    public Iterator<? extends AbstractNode<T>> getChildrenIterator() {
        // TODO : make unmutable iterator
        return getChildren().iterator();
    }

    @Override
    public Node<T> getFather() {
        return father;
    }

    public List<T> getPath() {
        LinkedList<T> p = new LinkedList();
        AbstractNode<T> n = this;
        while (n != null) {
            if (n.value != null) {
                p.addFirst(n.value);
            }
            n = n.father;
        }
        return p;
    }

    @Override
    public Node<T> getRoot() {
        AbstractNode<T> n = this;
        while (n.father != null) {
            n = n.father;
        }
        return n;
    }

    @Override
    public T getValue() {
        return value;
    }    
}
