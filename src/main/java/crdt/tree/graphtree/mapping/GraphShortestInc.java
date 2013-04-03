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
import crdt.tree.graphtree.GraphMappPolicy;
import java.util.HashMap;

/**
 *
 * @author Stephane Martin
 */
public class GraphShortestInc<T> extends GraphMappPolicyInc<T> {

    Tree tree;
    /*
     * HashMap<Edge<T>, Node<T>> tEdge; HashMap<Edge<T>, Node<T>> fEdge;
     */
    HashMap<T, Node<T>> tNode;
    HashMapSet<T, T> getFathers;

    @Override
    void addEdge(Edge<T> e) {

        Node<T> fatherN = tNode.get(e.getFather());
        Node<T> son = tNode.get(e.getSon());

        /*
         * Vérifie qu'il n'a pas déjà un père et un autre.
         */

        if (son != null && (son.getFather() != null
                || !son.getFather().getValue().equals(e.getFather()))) {
            if (takeFirst(fatherN, son.getFather())) {
                /*
                 * On déplace si le level est plus petit si les level sont egaux
                 * on prend T du père en compte
                 */
                tree.move(fatherN, son);
            }
        } else {
            Node<T> n = tree.add(fatherN, e);
            tNode.put(n.getValue(), n);
        }

    }

    int compare(T t1, T t2) {
        Comparable<T> c1 = (Comparable<T>) t1;
        //Comparable<T> c2=(Comparable<T>)t2;
        return c1.compareTo(t2);
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

    Node<T> getFather(T t) {
        Node<T> best = null;
        Node<T> conc;
        for (T tf : getFathers.getAll(t)) {
            conc = tNode.get(tf);
            if (takeFirst(conc, best)) {
                best = conc;
            }
        }
        return best;
    }

    @Override
    void delEdge(Edge<T> e) {
        getFathers.remove(e.getFather(), e.getSon());
        Node<T> father = getFather(e.getSon());
        Node<T> n = tNode.get(e.getSon());
        if (father == null) {
            tree.remove(n);
        } else if (father != n.getFather()) {
            tree.move(father, n);
        }
    }

    

    @Override
    public Tree<T> lookup() {

        return tree;
    }

    @Override
    public GraphMappPolicy<T> create() {
        GraphShortestInc<T> ret = new GraphShortestInc<T>();
        ret.tree = new HashTree();
        ret.tNode = new HashMap();
        tNode.put(null, tree.getRoot());
        return ret;
    }

    @Override
    void moveEdge(Edge<T> OldFather, Edge<T> moved) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
