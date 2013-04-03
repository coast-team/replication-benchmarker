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
package crdt.tree.graphtree.mapping;

import collect.HashMapSet;
import collect.HashTree;
import collect.Node;
import collect.Tree;
import crdt.tree.graphtree.Edge;
import java.util.HashSet;

/**
 *
 * @author score
 */
public class GraphSeveral<T> extends GraphMappPolicyNoInc<T> {

    HashSet<T> mark= new HashSet();

    @Override
    protected Tree<T> getTreeFromMapping(HashMapSet<T, Edge<T>> setTree) {
        tree = new HashTree();
        several(tree,setTree,tree.getRoot(),null);

        return tree;
    }

    void several(Tree tree, HashMapSet<T, Edge<T>> setTree, Node father, T fathert) {
        Node tmp;
        for (Edge<T> edge : setTree.getAll(fathert)) {
            if (!mark.contains(edge.getSon())) {
                tmp = tree.add(father, edge.getSon());
                mark.add(edge.getSon());
                if(setTree.getAll(edge.getSon()) != null)
                    several(tree, setTree, tmp, edge.getSon());
                mark.remove(edge.getSon());
            }
        }
    }

    @Override
    public GraphSeveral<T> create() {
        return new GraphSeveral<T>();
    }
}
