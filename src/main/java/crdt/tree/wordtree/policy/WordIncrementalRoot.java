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
