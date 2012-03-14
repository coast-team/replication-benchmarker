/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.tree.wordtree.policy;

import crdt.tree.wordtree.WordConnectionPolicy;
import java.util.List;

/**
 * WordSkip the orphan nodes.
 * @author urso
 */
public class WordSkip<T> extends WordConnectionPolicy<T> {
    @Override 
    protected void treatOrphan(List<T> orphan) {
    }

    @Override
    public WordSkip<T> create() {
        return new WordSkip<T>();
    }
}
