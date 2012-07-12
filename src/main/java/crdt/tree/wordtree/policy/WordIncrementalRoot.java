/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.tree.wordtree.policy;

import collect.Node;
import crdt.tree.wordtree.Word;
import crdt.tree.wordtree.WordPolicy;
import java.io.Serializable;
import java.util.List;
import java.util.Observable;

/**
 * WordSkip the orphan nodes.
 * @author urso
 */
public class WordIncrementalRoot<T> extends WordIncrementalMany<T> implements Serializable {    
            
    @Override
    public WordIncrementalRoot<T> create() {
        return new WordIncrementalRoot<T>();
    }

    private WordPolicy nonInc = new WordRoot();

    @Override
    void check(Observable o) {
        nonInc.update(o, null);
        if (!nonInc.lookup().equals(tree)) {
            throw new IllegalStateException();
        }
    }

    @Override
    protected Node<T> addPoint(List<T> word) {
        Node<T> father = w2n.get(word.subList(0, word.size()-1)), node;
        if (father == null) {
           father = tree.getRoot();
        }
        return father;
    }

    @Override
    protected Node<T> destPoint(Node<T> node) {
        return tree.getRoot();
    }
    
    @Override
    protected void move(Node<T> orig, Node<T> dest, List<T> word) {     
        for (Node<T> c : orig.getChildrenCopy()) {
            List<T> p = new Word<T>(word, c.getValue());
            if (tree.getAttached(c).contains(p)) {
                tree.remove(c, p);
                Node<T> n = tree.add(dest, c.getValue(), p);
                move(c, n, p);
                if (w2n.put(p, n) != c)
                    throw new IllegalStateException();
            }
        }
    }        
}
