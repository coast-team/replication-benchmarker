/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package collect;

import java.util.*;

/**
 *
 * @author score
 */
public class HashTree<T> implements Tree<T> {

    private NodeImpl<T> root;

    public HashTree() {
        root = createRoot();
    }

    @Override
    public NodeImpl<T> getRoot() {
        return root;
    }
        
    @Override
    public NodeImpl<T> getNode(List<T> path) {
        NodeImpl<T> n = root;
        for (T t : path) {
            n = n.getChild(t);
            if (n == null) {
                return null;
            }
        }
        return n;
    }

    @Override
    public Node<T> add(Node<T> father, T t) {
        if (father == null) {
            father = root;
        }
        NodeImpl<T> here = (NodeImpl<T>) father.getChild(t);
        if (here!= null) {
            return here;
        }
        return createNode(father, t);
    }

    @Override
    public NodeImpl<T> createOrphan(T t) {
        return createNode(null, t);
    }

    @Override
    public Iterator<? extends Node<T>> getBFSIterator(Node<T> n) {
        if (n == null) {
            n = root;
        }
//        } else if (!contains(n)) {
//            throw new NoSuchElementException("Node not in tree");
//        }
        return new BFSIterator((NodeImpl<T>) n);
    }

    @Override
    public Iterator<? extends Node<T>> getDFSIterator(Node<T> n) {
        if (n == null) {
            n = root;
        } else if (!contains(n)) {
            throw new NoSuchElementException("Node not in tree");
        }
        return new DFSIterator((NodeImpl<T>) n);
    }
    
    
    @Override
    public void remove(Node<T> n) {
        if (n == null || n == root) {
            throw new UnsupportedOperationException("Cannot remove root");
        }
        NodeImpl<T> ni = (NodeImpl<T>)n;
        if (ni.getFather() != null) {
            ni.getFather().getChildren().remove(n.getValue());
            ni.setFather(null);
        }
    }

    @Override
    public boolean contains(Node<T> n) {
        if (n.getFather()==null){
            return n==this.getRoot();
        }
        return /*n.getFather().isChildren(n) &&*/ contains(n.getFather());
        /* optimisation deleted du noeud détruit l'adresse du père*/
    }

    @Override
    public void clear() {
        root.getChildren().clear();
        this.root = createRoot();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof HashTree)) {
            return false;
        }
        return this.root.sameTree(((HashTree<T>) obj).root);
    }
        
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + (this.root != null ? this.root.hashCode() : 0);
        return hash;
    }

    @Override
    public void move(Node<T> father, Node<T> node) {
        NodeImpl n=(NodeImpl) node;
        n.setFather((NodeImpl)father);
                
    }

    protected NodeImpl<T> createNode(Node<T> father, T t) {
        return new NodeImpl<T>((NodeImpl<T>)father, t);
    }

    protected NodeImpl<T> createRoot() {
        return new NodeImpl<T>();
    }
    
    class DFSIterator<T> implements Iterator {

        Stack<Iterator<? extends Node<T>>> pile;
        Node<T> next;

        DFSIterator(NodeImpl<T> node) {
            pile = new Stack<Iterator<? extends Node<T>>>();
            next = node;
        }

        @Override
        public boolean hasNext() {
            return next != null;
        }

        @Override
        public Node<T> next() {
            Node<T> ret;
            if (next == null) {
                throw new NoSuchElementException();
            }
            //Iterator <?extends Node<T>> it=next.getChildrenIterator();
            pile.push(next.getChildrenIterator());
            ret = next;
            while (!pile.empty() && !pile.peek().hasNext()) {
                pile.pop();
            }
            next = pile.empty() ? null : pile.peek().next();

            return ret;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Remove is not supported yet.");
        }
    }

    class BFSIterator<T> implements Iterator {

        LinkedList<Iterator<? extends Node<T>>> file;
        Node<T> next;

        BFSIterator(NodeImpl<T> node) {
            file = new LinkedList<Iterator<? extends Node<T>>>();
            next = node;
        }

        @Override
        public boolean hasNext() {
            return next != null;
        }

        @Override
        public Node<T> next() {
            Node<T> ret;
            if (next == null) {
                throw new NoSuchElementException();
            }
            //Iterator <?extends Node<T>> it=next.getChildrenIterator();
            file.addFirst(next.getChildrenIterator());
            ret = next;
            while (!file.isEmpty() && !file.peekLast().hasNext()) {
                file.removeLast();
            }
            next = file.isEmpty() ? null : file.peekLast().next();

            return ret;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Remove is not supported yet.");
        }
    }

    @Override
    public String toString() {
        return "HashTree{" + "root=" + root + '}';
    }
}
