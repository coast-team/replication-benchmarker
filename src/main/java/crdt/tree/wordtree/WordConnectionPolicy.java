/**
 * Replication Benchmarker
 * https://github.com/score-team/replication-benchmarker/
 * Copyright (C) 2013 LORIA / Inria / SCORE Team
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
package crdt.tree.wordtree;

import collect.*;
import crdt.set.CRDTSet;
import java.util.*;

/**
 *
 * @author urso
 */
public abstract class WordConnectionPolicy<T> extends WordPolicy<T> {
    private boolean fresh;
    protected HashTree<T> tree;
    protected HashBiMapSet<UnorderedNode<T>, List<T>> nodeToWord;
    protected Set<List<T>> words;
    
    public WordConnectionPolicy() {
        this.nodeToWord = new HashBiMapSet();
        this.tree = new HashTree();
        this.nodeToWord.put(tree.getRoot(), Collections.EMPTY_LIST);
        this.words = new HashSet<List<T>>();
        this.fresh = false;
    }
    
    /**
     * Modifies the tree to obtain a connected tree according the actual set of words. 
     * Updates also the bidirectional addMapping between nodes and words.
     */
    public void connect (){
        List<Set<List<T>>> buckets = new ArraySkipList<Set<List<T>>>();
        tree.clear();
        nodeToWord.clear();
        nodeToWord.put(tree.getRoot(), Collections.EMPTY_LIST);
        
        for (List<T> w : words) {
            int size = w.size();
            Set<List<T>> b = buckets.get(size);
            if (b == null) {
                b = new HashSet<List<T>>();
                buckets.set(size, b);
            }
            b.add(w);
        }
        for (Set<List<T>> b : buckets) {
            for (List<T> w : b) {
                int size = w.size();
                UnorderedNode<T> father = nodeToWord.getInverse(w.subList(0, size - 1));
                if (father == null) {
                    treatOrphan(w);
                } else {
                    UnorderedNode<T> node = tree.add(father, w.get(size-1));
                    nodeToWord.put(node, w);
                }
            }
        } 
        fresh = true;
        
    }
    
    /**
     * Define how to treat an orphan node. Be called in word length order.
     * The abstract policy to be implemented.
     * @param orphan an orphan node (i.e. the father is not in the tree)
     */
    abstract protected void treatOrphan (List<T> orphan);

    @Override
    public Collection<List<T>> addMapping(UnorderedNode<T> node) {
        if (!fresh) connect();
        return nodeToWord.get(node);
    }

    @Override
    public Tree<T> lookup() {
        if (!fresh) connect();
        return tree;
    }

    @Override
    public void update(Observable o, Object op) {
        this.words = ((CRDTSet<List<T>>) o).lookup();
        fresh = false;
    }

    @Override
    public Collection<List<T>> delMapping(UnorderedNode<T> node) {
        return addMapping(node);
    }
}
