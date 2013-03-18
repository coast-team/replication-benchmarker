/**
 * Replication Benchmarker
 * https://github.com/score-team/replication-benchmarker/
 * Copyright (C) 2012 LORIA / Inria / SCORE Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package collect;

import crdt.tree.TreeOperation;
import java.io.Serializable;
import java.util.*;

/**
 *
 * @author score
 */
public class HashTree<T> extends Observable implements Tree<T>, Serializable {

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
    public UnorderedNode<T> add(Node<T> father, T t) {
        if (father == null) {
            father = root;
        }
        NodeImpl<T> here = ((NodeImpl<T>) father).getChild(t);
        if (here != null) {
            return here;
        }
        if ((countObservers() > 0) && connected(father)) {
            setChanged();
            notifyObservers(new TreeOperation<T>(father, t));
        }
        return createNode(father, t);
    }

    @Override
    public NodeImpl<T> createOrphan(T t) {
        return createNode(null, t);
    }

    @Override
    public Iterator<? extends UnorderedNode<T>> getBFSIterator(Node<T> n) {
        if (n == null) {
            n = root;
        }
//        } else if (!contains(n)) {
//            throw new NoSuchElementException("Node not in tree");
//        }
        return new BFSIterator((NodeImpl<T>) n);
    }

    @Override
    public Iterator<? extends UnorderedNode<T>> getDFSIterator(Node<T> n) {
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
            NodeImpl<T> father = (NodeImpl<T>) ni.getFather();
//            father.getChildren().remove(n.getValue());
            if ((countObservers() > 0) && connected(father)) {
                setChanged();
                notifyObservers(new TreeOperation<T>(TreeOperation.OpType.del, father, n.getValue()));
            }            
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
        boolean move = false;
        Node f = null;
        if ((countObservers() > 0) && connected(node)) {
            move = true;
            f = n.getFather();
        }   
        
        n.setFather((NodeImpl)father);
        
        if ((countObservers() > 0) && connected(father)) {
            if (move) { // Move
                setChanged();
                notifyObservers(new TreeOperation<T>(f, father, node.getValue()));
            } else { // Add subtree
                Iterator<? extends Node<T>> it = getBFSIterator(node);
                while (it.hasNext()) {
                    Node<T> e = it.next();
                    setChanged();
                    notifyObservers(new TreeOperation<T>(e.getFather(), e.getValue()));
                }
            }
        } else if (move) { // Del    
            setChanged();
            notifyObservers(new TreeOperation<T>(f, node.getValue()));
        }
    }

    protected NodeImpl<T> createNode(Node<T> father, T t) {
        return new NodeImpl<T>((NodeImpl<T>)father, t);
    }

    protected NodeImpl<T> createRoot() {
        return new NodeImpl<T>();
    }

    private boolean connected(Node<T> father) {
        while (father != root && father != null) {
            father = father.getFather();
        }
        return (father == root);
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
            //Iterator <?extends Node<T>> it=next.iterator();
            pile.push(next.iterator());
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
            //Iterator <?extends Node<T>> it=next.iterator();
            file.addFirst(next.iterator());
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
