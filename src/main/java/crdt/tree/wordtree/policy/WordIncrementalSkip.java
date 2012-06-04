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
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Set;

/**
 * WordSkip the orphan nodes.
 * @author urso
 */
public class WordIncrementalSkip<T> implements WordPolicy<T> {
    private Set<List<T>> orphans = new HashSet();
    private HashTree<T> tree = new HashTree<T>();
    private Map<List<T>, Node<T>> w2n = new HashMap<List<T>, Node<T>>();
    
    public WordIncrementalSkip() {
        w2n.put(new Word<T>(), tree.getRoot());
    }
    
    @Override
    public void update(Observable o, Object obj) {     
        SetOperation<List<T>> op = (SetOperation<List<T>>) obj;
        Word<T> word = (Word<T>) op.getContent();
        
        if (op.getType() == SetOperation.OpType.add) {
            Node<T> father = w2n.get(word.subList(0, word.size()-1));
            if (father == null) {
                addOrphan(word);
            } else {
                addNode(father, word);
            }
            
            // Reattach adopted 
            // reattach(node, word);
        } else {  // Remove
            Node<T> node = w2n.get(word);
            remove(node, word);
        }
//        check();
    }
    
    @Override
    public WordIncrementalSkip<T> create() {
        return new WordIncrementalSkip<T>();
    }

    protected void remove(Node<T> node, Word<T> word) {
        if (orphans.contains(word)) {
            orphans.remove(word);
        }
        for (Node<T> n : node.getChildrenCopy()) {
            Word<T> path = new Word(word, n.getValue());
            orphans.add(path);
            tree.move(null, n);
        }
        w2n.remove(word);
        tree.remove(node);
    }

    protected void addOrphan(Word<T> word) {
        Node<T> node = tree.createOrphan(word.getLast());
        orphans.add(word);
        w2n.put(word, node);
        reattach(node, word);
    }
   
    protected void addNode(Node<T> father, Word<T> word) {
        Node<T> node = tree.add(father, word.getLast());
        w2n.put(word, node);
        reattach(node, word);
    }

    protected void reattach(Node<T> node, Word<T> word) {
        Iterator<List<T>> it = orphans.iterator();
        while (it.hasNext()) {
//            Entry<List<T>, Node<T>> e = it.next();
            List<T> orphanWord = it.next();// e.getKey();
//            Node<T> orphanNode = e.getValue();
            if (orphanWord.subList(0, orphanWord.size() - 1).equals(word)) {
                tree.move(node, w2n.get(orphanWord));
                it.remove();
            }
        }
    }

    @Override
    public Collection<List<T>> delMapping(UnorderedNode<T> node) {
        return addMapping(node);
    }

    protected void check() {
        LinkedList path;
        for (List<T> w : orphans) {
            if (w2n.get(w) == null || w2n.get(w).getRoot() == tree.getRoot()) {
                throw new IllegalStateException();
            }
            Iterator<? extends Node<T>> it = tree.getBFSIterator(w2n.get(w));
            it.next();
            while (it.hasNext()) {
                Node<T> s = it.next();
                path = new LinkedList(w);
                path.removeLast();
                path.addAll(s.getPath());
                if (w2n.get(path) == null || orphans.contains(path)) {
                    throw new IllegalStateException();
                }
            }
        }
        Iterator<? extends Node<T>> it = tree.getBFSIterator(null);
        while (it.hasNext()) {
            List<T> w = it.next().getPath();
            if (w2n.get(w) == null || orphans.contains(w)) {
                throw new IllegalStateException();
            }
        }
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
}
