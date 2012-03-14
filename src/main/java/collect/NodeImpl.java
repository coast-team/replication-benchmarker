/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package collect;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 *
 * @author score
 */
public class NodeImpl<T> implements Node<T> {

    //private Tree origin;
     final private T value;
     final private Map<T, NodeImpl<T>> children;
     private NodeImpl<T> father=null;
     private int level=0; // TODO : is level really usefull ?
     private int hash;


    public Map<T, NodeImpl<T>> getChildren() {
        return children;
    }
    
    protected NodeImpl() {      
        this.children = new HashMap<T, NodeImpl<T>>();
        this.value = null;
        this.hash = generateHash();
    }
    
    @SuppressWarnings("LeakingThisInConstructor")
    protected NodeImpl(NodeImpl<T> f, T t) {
        this.children = new HashMap<T, NodeImpl<T>>();
        this.value = t;
        this.hash = generateHash();
        this.level = (f == null) ? -1 : f.level + 1;
        this.father = f;
        this.hash = generateHash();
        if (f != null) f.children.put(t, this);
    }

    @Override
    public T getValue() {
        return value;
    }

    @Override
    public NodeImpl<T> getFather() {
        return father;
    }

    @Override
    public Iterator<NodeImpl<T>> getChildrenIterator() {
        // TODO : make unmutable iterator
        return children.values().iterator();
    }

    @Override
    public int getChildrenNumber() {
        return children.size();
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

    protected void setFather(NodeImpl<T> n) {
        if (this.father!=null){
            this.getFather().children.remove(this.value);
        }
        this.father = n;
        if (this.father != null) {
            this.father.children.put(this.value, this);
            this.level = this.father.level + 1;
        } else {
            this.level = -1;
        }
        this.hash = generateHash();
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
        NodeImpl<T> f, fo;
        for (f = father, fo = other.father; f != null && fo != null ; f = f.father, fo = fo.father) {
            if (f.value != fo.value) {
                return false;
            }
        }
        return f==fo;
    }

    @Override
    public String toString() {
        return "" + value + children;
    }

    @Override
    public NodeImpl<T> getChild(T t) {
        return children.get(t);
    }

    @Override
    public boolean isChildren(Node<T> n) {
        return this.children.containsValue((NodeImpl)n);
        /* TODO : Possible qu'un this.children.containsKey(n.getValue()) 
         * serait plus rapide*/
    }

    @Override
    public Collection<? extends Node<T>> getChildrenCopy() {
        return new LinkedList(children.values());
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
        for (Entry<T, NodeImpl<T>> e : this.getChildren().entrySet()) {
            if (!e.getValue().sameTree(other.children.get(e.getKey()))) {
                return false;
            }
        }
        return true; 
    }

    @Override
    public List<T> getPath() {
        LinkedList<T> p = new LinkedList();
        NodeImpl<T> n = this; 
        while (n != null) {
            if (n.value != null) p.addFirst(n.value);
            n = n.getFather();
        }
        return p;
    }

    @Override
    public Node<T> getRoot() {
        NodeImpl<T> n = this; 
        while (n.father != null) {
            n = n.father;
        }
        return n;
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
}
