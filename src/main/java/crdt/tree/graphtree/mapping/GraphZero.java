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
package crdt.tree.graphtree.mapping;

import collect.HashMapSet;
import collect.HashTree;
import collect.Node;
import collect.Tree;
import crdt.tree.graphtree.Edge;
import crdt.tree.graphtree.GraphMappPolicy;
import java.util.*;

/**
 *
 * @author score
 */
public class GraphZero<T> extends GraphMappPolicyNoInc<T>{

    HashMap<T, Node<T>> mark = new HashMap();

    @Override
    protected Tree<T> getTreeFromMapping(HashMapSet<T, Edge<T>> setTree) {
        tree = new HashTree();
        zero(tree,setTree,tree.getRoot(),null);

        return tree;
    }

    void zero(Tree tree, HashMapSet<T, Edge<T>> setTree, Node father, T fathert) {
        Node tmp;
        for (Edge<T> edge : setTree.getAll(fathert)) {
            if (!mark.containsKey(edge.getSon())) {
                tmp = tree.add(father, edge.getSon());
                mark.put(edge.getSon(), tmp);
                if(setTree.getAll(edge.getSon()) != null){
                zero(tree, setTree, tmp, edge.getSon());}
            }
            else
            {
                tree.remove(mark.get(edge.getSon()));
            }
        }
    }

    @Override
    public GraphMappPolicy<T> create() {
        return new GraphZero<T>();
    }

    @Override
    public void update(Observable o, Object o1) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

}
