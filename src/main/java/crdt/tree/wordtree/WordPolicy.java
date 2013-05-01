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

import collect.Node;
import collect.Tree;
import collect.UnorderedNode;
import crdt.Factory;
import java.util.*;

/**
 *
 * @author urso
 */
public abstract class WordPolicy<T> implements Factory<WordPolicy<T>>, Observer {
    
    /**
     * The lookup computed by the policy
     * @return a tree
     */
    abstract public Tree<T> lookup();

    /**
     * Mapping between tree lookup node and words
     * @return a bimap
     */
    abstract public Collection<List<T>> addMapping(UnorderedNode<T> node);

    abstract protected Collection<List<T>> delMapping(UnorderedNode<T> node);
    
    public Collection<List<T>> toBeRemoved(UnorderedNode<T> subtree) {       
        Iterator<? extends Node<T>> subtreeIt = lookup().getBFSIterator(subtree);
        List<List<T>> toBeRemoved = new LinkedList<List<T>>();
        while (subtreeIt.hasNext()) {
            UnorderedNode<T> n = (UnorderedNode<T>) subtreeIt.next();
            Collection<List<T>> w = delMapping(n);
            toBeRemoved.addAll(0, w);
        }
        return toBeRemoved;
    }
}
