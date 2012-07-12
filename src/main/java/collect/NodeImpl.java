/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package collect;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 *
 * @author score
 */
public class NodeImpl<T>  extends AbstractNode<T> implements UnorderedNode<T>, Serializable {

    //private Tree origin;
     final private Map<T, NodeImpl<T>> children;
     private int level=0; // TODO : is level really usefull ?
     private int hash;


    @Override
    protected Collection<NodeImpl<T>> getChildren() {
        return children.values();
    }
    
    public Map<T, NodeImpl<T>> getChildrenMap() {
        return children;
    }
    
    protected NodeImpl() {
        super(null,null);
        this.children = new HashMap<T, NodeImpl<T>>();
        this.hash = generateHash();
    }
    
    @SuppressWarnings("LeakingThisInConstructor")
    protected NodeImpl(NodeImpl<T> f, T t) {
        super(t, f);
        this.children = new HashMap<T, NodeImpl<T>>();
        this.hash = generateHash();
        this.level = (f == null) ? -1 : f.level + 1;
        this.father = f;
        this.hash = generateHash();
        if (f != null) f.children.put(t, this);
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final NodeImpl<T> other = (NodeImpl<T>) obj;
        if (this.value != other.value && (this.value == null || !this.value.equals(other.value))) {
            return false;
        }
        if (this.father != other.father && (this.father == null || !this.father.equals(other.father))) {
            return false;
        }
        return true;
    }

    private int generateHash() {
        int hashRet = 7;
        hashRet = 13 * hashRet + (this.value != null ? this.value.hashCode() : 0);
        hashRet = 13 * hashRet + (this.father != null ? this.father.hashCode() : 0);
        return hashRet;
    }

    @Override
    public int hashCode() {
        return hash;
    }

    private boolean samePath(NodeImpl<T> other) {
        AbstractNode<T> f, fo;
        for (f = father, fo = other.father; f != null && fo != null ; f = f.father, fo = fo.father) {
            if (f.value != fo.value) {
                return false;
            }
        }
        return f==fo;
    }

    @Override
    public String toString() {
        return value + "[" + children.values() + ']';
    }

    @Override
    public NodeImpl<T> getChild(T t) {
        return children.get(t);
    }
    
    boolean sameTree(NodeImpl<T> other) {
        if (other == null) {
            return false; 
        }
        if (this.getValue() != other.getValue()) {
            return false; 
        } 
        if (this.children.size() != other.children.size()) {
            return false;
        }
        for (Entry<T, NodeImpl<T>> e : children.entrySet()) {
            if (!e.getValue().sameTree(other.children.get(e.getKey()))) {
                return false;
            }
        }
        return true; 
    }

    @Override
    public void deleteChild( Collection<? extends Node<T>> nodeToDelet) {
        Iterator itr = this.getChildrenIterator();
        while (itr.hasNext()) {
            Node node = (Node) itr.next();
            if (nodeToDelet.contains(node)) {
                itr.remove();
            }
        }
    }

    protected void setFather(NodeImpl<T> n) {
        if (this.father != null) {
            ((NodeImpl<T>) this.father).children.remove(this.value);
        }
        this.father = n;
        if (this.father != null) {
            ((NodeImpl<T>) this.father).children.put(this.value, this);
            this.level = ((NodeImpl<T>) this.father).level + 1;
        } else {
            this.level = -1;
        }
        this.hash = generateHash();
    }
}
