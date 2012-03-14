/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.tree.graphtree.mapping;

import collect.HashMapSet;
import collect.HashTree;
import collect.Node;
import crdt.tree.graphtree.Edge;
import collect.Tree;
import crdt.tree.graphtree.GraphMappPolicy;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

/**
 *
 * @author score
 */
public class GraphShortest<T> extends GraphMappPolicyNoInc<T> {
    /*
     * TODO : test meee !!!!
     */

    HashMap<T, Node<T>> history;
    LinkedList<Edge<T>> file;

    @Override
    /*
     * Utilisa un parcour BFS pour ajouter de manière unique les plus proches
     */
    protected Tree<T> getTreeFromMapping(HashMapSet<T, Edge<T>> treeSet) {
        Tree t = new HashTree();
        file.addAll(treeSet.getAll(null));
        while (!file.isEmpty()) {
            Edge<T> cur = file.pollFirst();
            /*
             * Pas déjà ajouté
             */
            Node<T> nodeBack = history.get(cur.getSon());
            if (nodeBack == null) {

                Node<T> n = tree.add(history.get(cur.getFather()), cur.getSon());
                history.put(n.getValue(), n);

            } else {
                Node<T> conflict = history.get(cur.getFather());
                /*
                 * Si le nouveau possède un meilleur scrore on le prends.
                 */
                if (takeFirst(conflict, nodeBack.getFather())) {
                    tree.move(conflict, nodeBack);
                }
            }
            /*
             * ajoute les fils s'il y en a.
             */
            Set<Edge<T>> fils = treeSet.getAll(cur.getSon());
            if (fils != null) {
                file.addAll(fils);
            }
        }


        return t;
    }

    boolean takeFirst(Node<T> a, Node<T> b) {
        if (a == null) {
            return false;
        }
        if (b == null) {
            return true;
        }
        if (b.getLevel() > a.getLevel()) {
            return true;
        }
        return (b.getLevel() == a.getLevel()
                && ((Comparable<T>) b.getValue()).compareTo(a.getValue()) > 0);
    }
    /*
     * private void recurcivTree(Tree t,Node Father, T father){
     *
     * }
     */

    @Override
    public GraphMappPolicy<T> create() {
        GraphShortest ret = new GraphShortest<T>();
        ret.history = new HashMap<T, Node<T>>();
        ret.file = new LinkedList<Edge<T>>();

        return ret;
    }
}