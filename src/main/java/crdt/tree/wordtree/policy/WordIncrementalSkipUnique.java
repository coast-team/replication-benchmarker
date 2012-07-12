/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.tree.wordtree.policy;

import collect.Node;
import collect.UnorderedNode;
import crdt.tree.wordtree.Word;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author urso
 */
public class WordIncrementalSkipUnique<T> extends WordIncrementalSkip<T> {

    // no need since element are unique
    @Override
    protected void reattach(Node<T> node, Word<T> word) {
        
    }

    // need only to remove subtree root
    @Override
    public Collection<List<T>> toBeRemoved(UnorderedNode<T> subtree) {
        return delMapping(subtree);
    }

    @Override
    public WordIncrementalSkip<T> create() {
        return new WordIncrementalSkipUnique<T>();
    }
}
