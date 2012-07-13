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
import collect.UnorderedNode;
import crdt.tree.wordtree.Word;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;


/**
 *
 * @author urso
 */
public class WordIncrementalSkipUnique<T> extends WordIncrementalSkip<T> implements Serializable {

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
