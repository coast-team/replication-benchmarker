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
