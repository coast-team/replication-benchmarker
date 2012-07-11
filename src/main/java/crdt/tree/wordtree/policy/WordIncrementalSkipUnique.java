/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.tree.wordtree.policy;

import collect.Node;
import crdt.tree.wordtree.Word;

/**
 *
 * @author urso
 */
public class WordIncrementalSkipUnique<T> extends WordIncrementalSkip<T> {

    // no need since element are unique
    @Override
    protected void reattach(Node<T> node, Word<T> word) {
        
    }

    @Override
    public WordIncrementalSkip<T> create() {
        return new WordIncrementalSkipUnique<T>();
    }
}
