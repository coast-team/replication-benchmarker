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

import collect.UnorderedNode;
import crdt.tree.wordtree.Word;
import crdt.tree.wordtree.WordConnectionPolicy;
import java.util.*;

/**
 * Skip the orphan nodes.
 * @author urso
 */
public class WordReappear<T> extends WordConnectionPolicy<T> {
    @Override 
    protected void treatOrphan(List<T> orphan) {
        LinkedList<T> ancestor = new LinkedList<T>(orphan);
        while (nodeToWord.getInverse(ancestor) == null) {
            ancestor.pollLast();
        }
        UnorderedNode<T> node = nodeToWord.getInverse(ancestor);
        int i = ancestor.size();
        ListIterator<T> it = orphan.listIterator(i);
        while (it.hasNext()) {
            node = tree.add(node, it.next());
            i++;
//            nodeToWord.put(node, null); // orphan.subList(0, i));
        }
        nodeToWord.put(node, orphan);
    }

    @Override
    public WordReappear<T> create() {
        return new WordReappear<T>();
    }

    @Override
    public Collection<List<T>> addMapping(UnorderedNode<T> node) {
        Collection<List<T>> m = super.addMapping(node);
        if (m == null) {
            Collection<List<T>> set = new LinkedList<List<T>>();
            set.add(new Word(node.getPath()));
            return set;            
        } else {
            return m;
        }
    }
    
    @Override
    public Collection<List<T>> delMapping(UnorderedNode<T> node) {
        Collection<List<T>> m = super.addMapping(node);
        if (m == null) {
            return Collections.EMPTY_SET;
        } else {
            return m;
        }
    }
}
