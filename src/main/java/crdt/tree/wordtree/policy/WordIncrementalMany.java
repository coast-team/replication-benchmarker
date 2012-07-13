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
import collect.Tree;
import collect.UnorderedNode;
import crdt.set.SetOperation;
import crdt.tree.wordtree.Word;
import crdt.tree.wordtree.WordPolicy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Set;

/**
 * WordSkip the orphan nodes.
 * @author urso
 */
abstract public class WordIncrementalMany<T> extends WordPolicy<T> {
    protected CounterTree<T> tree = new CounterTree<T>();
    protected Map<List<T>, Node<T>> w2n = new HashMap<List<T>, Node<T>>();
    
            
    @Override
    public void update(Observable o, Object obj) { 
        SetOperation<List<T>> op = (SetOperation<List<T>>) obj;
        Word<T> word = (Word<T>) op.getContent();
        
        if (op.getType() == SetOperation.OpType.add) {
            Node<T> node = tree.add(addPoint(word), word.get(word.size()-1), word);   
            w2n.put(word, node);
            // Reattach adopted 
            move(destPoint(node), node, word);
        } else {  // Remove
            Node<T> node = w2n.get(word);
            w2n.remove(word);
            move(node, destPoint(node), word);
            tree.remove(node, word);
        }
//        check(o);
    }


    abstract protected Node<T> addPoint(List<T> word);

    abstract protected Node<T> destPoint(Node<T> node);

    abstract protected void move(Node<T> orig, Node<T> dest, List<T> word);
    
    @Override
    public Set<List<T>> delMapping(UnorderedNode<T> node) {
        return addMapping(node);
    }

    @Override
    public Tree<T> lookup() {
        return tree;
    }

    @Override
    public Set<List<T>> addMapping(UnorderedNode<T> node) {
        return tree.getAttached(node);
    }

    abstract void check(Observable o);
}
