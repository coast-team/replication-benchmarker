/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
