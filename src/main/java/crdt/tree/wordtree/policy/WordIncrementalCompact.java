/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.tree.wordtree.policy;

import collect.Node;
import crdt.tree.wordtree.WordPolicy;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;

/**
 * WordSkip the orphan nodes.
 * @author urso
 */
public class WordIncrementalCompact<T> extends WordIncrementalMany<T> {

    public WordIncrementalCompact() {
        w2n.put(Collections.EMPTY_LIST, tree.getRoot());
    }
   
    @Override
    public WordIncrementalCompact<T> create() {
        return new WordIncrementalCompact<T>();
    }
    
    private WordPolicy nonInc = new WordCompact();

    @Override
    void check(Observable o) {
        nonInc.update(o, null);
        if (!nonInc.lookup().equals(tree)) {
            throw new IllegalStateException();
        }
    }

    @Override
    protected Node<T> addPoint(List<T> word) {
        Node<T> father = null;
        int i = word.size();
        while (father == null) {
            --i;
            father = w2n.get(word.subList(0, i));
        }
        return father;
    }

    @Override
    protected Node<T> destPoint(Node<T> node) {
        return node.getFather();
    }
    
    @Override
    protected void move(Node<T> orig, Node<T> dest, List<T> word) {     
        for (Node<T> c : orig.getChildrenCopy()) {
            Iterator<List<T>> it = tree.getAttached(c).iterator();
            while (it.hasNext()) {
                List<T> w = it.next();
                if (isPrefix(word, w)) {
                    it.remove();
                    Node<T> n = tree.add(dest, c.getValue(), w);
                    move(c, n, w);
                    if (w2n.put(w, n) != c) {
                        throw new IllegalStateException();
                    }
                }
            }
            if (tree.getAttached(c).isEmpty()) {
                tree.remove(c);
            }                
        }
    }

    private boolean isPrefix(List<T> p, List<T> w) {
        Iterator<T> itp = p.iterator(), itw = w.iterator();
        while (itp.hasNext()) {
            if (!itw.hasNext() || !itp.next().equals(itw.next())) {
                return false;
            }
        }
        return itw.hasNext();
    }
}
