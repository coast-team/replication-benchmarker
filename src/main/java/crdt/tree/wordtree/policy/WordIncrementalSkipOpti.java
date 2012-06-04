/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.tree.wordtree.policy;

import collect.HashTree;
import collect.Node;
import collect.Tree;
import collect.UnorderedNode;
import crdt.set.CRDTSet;
import crdt.set.SetOperation;
import crdt.tree.wordtree.Word;
import crdt.tree.wordtree.WordPolicy;
import java.util.Map.Entry;
import java.util.*;

/**
 * WordSkip the orphan nodes.
 * @author urso
 */
public class WordIncrementalSkipOpti<T> implements WordPolicy<T> {
    private HashTree<T> tree = new HashTree<T>();
    private Map<List<T>, Map<T, Node<T>>> orphans = new HashMap<List<T>, Map<T, Node<T>>>();
    
    public WordIncrementalSkipOpti() {
//        orphans.put(Collections.EMPTY_LIST, tree.getRoot());
    }
    
    List hist = new LinkedList();
    
    @Override
    public void update(Observable o, Object obj) {     
        hist.add(obj);
        SetOperation<List<T>> op = (SetOperation<List<T>>) obj;
        Word<T> word = (Word<T>) op.getContent();
        List<T> fp = word.subList(0, word.size()-1);
        T t = word.getLast();
        Node<T> node = null;
        boolean connected = false;
 
        if (op.getType() == SetOperation.OpType.add) {
            if (word.size() > 1) {
                Node<T> father = getOrphan(fp);
                if (father != null) {
                    node = tree.add(father, t);
                    putOrphan(fp, node);  
                }
            } 
            if (node == null) {
                Node<T> father = tree.getNode(fp);
                if (father == null) {                    
                    node = tree.createOrphan(t);
                    putOrphan(fp, node);
                } else {
                    node = tree.add(father, t);
                    connected = true;
                }
            }
            // Reattach adopted 
            reattach(node, word);
            if (connected) {
                removeOrphans(word);
            }
        } else {  // Remove
            Map<T, Node<T>> fm = orphans.get(fp);
            if (fm != null && fm.containsKey(t)) {
                node = fm.get(t); 
                fm.remove(t);
                if (fm.isEmpty()) {
                    orphans.remove(fp);
                }    
            } else {
                node = tree.getNode(word); 
                putOrphans(word, node);
            }
            for (Node<T> n : node.getChildrenCopy()) {
                tree.move(null, n);
            }
            tree.remove(node);
        }
        check((CRDTSet) o);
    }
    
    @Override
    public WordIncrementalSkipOpti<T> create() {
        return new WordIncrementalSkipOpti<T>();
    }

    protected void reattach(Node<T> node, Word<T> word) {
        Map<T, Node<T>> fm = orphans.get(word);
        if (fm != null) {
            for (Node<T> c : fm.values()) {
                tree.move(node, c);
            }
        } 
    }

    @Override
    public Collection<List<T>> delMapping(UnorderedNode<T> node) {
        return addMapping(node);
    }

    protected void check(CRDTSet<List<T>> set) {    
        for (List<T> w : set.lookup()) {
            if (tree.getNode(w) == null && getOrphan(w) == null) {
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

    private void putOrphan(List<T> fp, Node<T> node) {
        Map<T, Node<T>> fm = orphans.get(fp);
        if (fm == null) {
            fm = new HashMap();
            orphans.put(fp, fm);
        }
        fm.put(node.getValue(), node);
    }
    
    private void putOrphans(List<T> word, Node<T> node) {
        if (node.getChildrenNumber() > 0) {
            Map<T, Node<T>> m = new HashMap();
            orphans.put(word, m);
            for (Node<T> c : node.getChildrenCopy()) {
                m.put(c.getValue(), c);
                putOrphans(new Word(word, c.getValue()), c);
            }
        }
    }

    private void removeOrphans(List<T> path) {
        Map<T, Node<T>> fm = orphans.get(path);
        if (fm != null) {
            for (Entry<T, Node<T>> e : fm.entrySet()) {
                removeOrphans(new Word(path, e.getKey()));
            }
            orphans.remove(path);
        }        
    }
    
    private Node<T> getOrphan(List<T> path) {
        Word<T> word = (Word<T>) path;
        Map<T, Node<T>> fm = orphans.get(word.getFather());
        if (fm != null) {
            return fm.get(word.getLast());
        } else {
            return null;
        }
    }
}
