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

import collect.Node;
import collect.UnorderedNode;
import crdt.tree.wordtree.WordConnectionPolicy;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * Skip the orphan nodes.
 * @author urso
 */
public class WordRoot<T> extends WordConnectionPolicy<T> implements Serializable {
    @Override 
    protected void treatOrphan(List<T> orphan) {
        LinkedList<T> ancestor = new LinkedList<T>(orphan);
        T elem = ancestor.pollLast();
        while (words.contains(ancestor)) {
            ancestor.pollLast();
        }
        UnorderedNode<T> father = nodeToWord.getInverse(orphan.subList(ancestor.size(), orphan.size()-1)),
                node = father.getChild(elem);
        if (node == null) {
            node = tree.add(node, elem);
        }
        nodeToWord.put(node, orphan);
    }

    @Override
    public WordRoot<T> create() {
        return new WordRoot<T>();
    }
}
