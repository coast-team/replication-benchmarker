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
package crdt.tree.wordtree.policy;

import collect.DecoratedNode;
import collect.DecoratedTree;
import collect.Node;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author score
 */
public class CounterTree<T> extends DecoratedTree<T,Set<List<T>>> {

    @Override
    protected DecoratedNode<T,Set<List<T>>> createNode(Node<T> father, T t) {
        DecoratedNode<T,Set<List<T>>> n = super.createNode(father, t);
        setAttached(n, new HashSet());
        return n;
    }

    @Override
    protected DecoratedNode<T,Set<List<T>>> createRoot() {
        DecoratedNode<T,Set<List<T>>> n = super.createRoot();
        Set<List<T>> s = new HashSet();
        s.add(Collections.EMPTY_LIST);
        setAttached(n, s);
        return n;
    }
    
    
    Node<T> add(Node<T> father, T t, List<T> word) {
        Node<T> node = super.add(father, t);
        if (!getAttached(node).add(word))          
            throw new IllegalStateException(); 
        return node;
    }

    public void remove(Node<T> node, List<T> word) {
        if (!getAttached(node).remove(word))
            throw new IllegalStateException();            
        if (getAttached(node).isEmpty()) {
            super.remove(node);
        }
    }    
}

