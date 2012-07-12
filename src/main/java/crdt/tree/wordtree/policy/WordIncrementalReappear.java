/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.tree.wordtree.policy;

import collect.HashTree;
import collect.Node;
import collect.Tree;
import collect.UnorderedNode;
import crdt.set.SetOperation;
import crdt.tree.wordtree.Word;
import crdt.tree.wordtree.WordPolicy;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Set;

/**
 * WordSkip the orphan nodes.
 * @author urso
 */

public class WordIncrementalReappear<T> extends WordPolicy<T> implements Serializable{

    private HashTree<T> tree = new HashTree<T>();
    private Set<Node<T>> ghosts = new HashSet<Node<T>>();
    
    @Override
    public WordIncrementalReappear<T> create() {
        return new WordIncrementalReappear<T>();
    }
    
    private void removeGhosts(Node<T> node, List<T> word) {
        ghosts.remove(node);
        Node<T> father = node.getFather();
        tree.remove(node);
//        w2n.remove(word);
        if (ghosts.contains(father) && father.getChildrenNumber() == 0) {
            removeGhosts(father, word.subList(0, word.size() - 1));
        }
    }

    protected void remove(Node<T> node, List<T> word) {      
        if (node.getChildrenNumber() == 0) {
            removeGhosts(node, word);          
        } else {            
            ghosts.add(node);
        }
    }

    protected void addOrphan(List<T> word) {
        UnorderedNode<T> node = tree.getRoot();
        for (T t : word) {
            UnorderedNode<T> c = node.getChild(t);
            if (c == null) {
                c = tree.add(node, t);
                ghosts.add(c);
            }
            node = c;
        }
        ghosts.remove(node);
    }

    protected void addNode(Node<T> father, List<T> word) {
        Node<T> node = tree.add(father, word.get(word.size()-1));
        ghosts.remove(node);
    }

    @Override
    public Collection<List<T>> delMapping(UnorderedNode<T> node) {
        if (ghosts.contains(node)) {
            return Collections.EMPTY_SET;
        } else {
            return addMapping(node);
        }
    }

    protected void check() {
        Iterator<? extends Node<T>> it = tree.getBFSIterator(null);
        while (it.hasNext()) {
            Node<T> n = it.next();
            List<T> w = n.getPath();
            if (ghosts.contains(n) && n.getChildrenNumber() == 0) {
                throw new IllegalStateException();
            }
        }
        for (Node n : ghosts) {
            if (!tree.contains(n)) {
                throw new IllegalStateException();
            }
        }
        //        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Tree<T> lookup() {
        return tree;
    }

    @Override
    public Collection<List<T>> addMapping(UnorderedNode<T> node) {
        Collection<List<T>> set = new LinkedList<List<T>>();
        set.add(new Word(node.getPath()));
        return set;
    }

    @Override
    public void update(Observable o, Object obj) {
        SetOperation<List<T>> op = (SetOperation<List<T>>) obj;
        List<T> word = op.getContent();
        
        if (op.getType() == SetOperation.OpType.add) {
            Node<T> father = tree.getNode(word.subList(0, word.size()-1));
            if (father == null) {
                addOrphan(word);
            } else {
                addNode(father, word);
            }
        } else {  // Remove
            Node<T> node = tree.getNode(word);
            remove(node, word);
        }
//        check();
    }
}
