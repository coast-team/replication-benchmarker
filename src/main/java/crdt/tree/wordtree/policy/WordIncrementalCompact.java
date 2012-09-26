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
package crdt.tree.wordtree.policy;

import collect.Node;
import crdt.tree.wordtree.WordPolicy;
import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;

/**
 * WordSkip the orphan nodes.
 * @author urso
 */
public class WordIncrementalCompact<T> extends WordIncrementalMany<T> implements Serializable  {

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
