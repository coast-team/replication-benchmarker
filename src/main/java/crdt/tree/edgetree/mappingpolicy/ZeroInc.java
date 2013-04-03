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
package crdt.tree.edgetree.mappingpolicy;

import collect.HashMapSet;
import collect.HashTree;
import collect.Node;
import collect.Tree;
import crdt.tree.edgetree.Edge;
import crdt.tree.edgetree.connectionpolicy.EdgeConnectionPolicy;
import java.util.HashMap;

/**
 *
 * @author Stephane Martin
 */
public class ZeroInc<T> extends EdgeMappPolicy<T> {

    HashTree tree;
    HashMapSet<T, Edge<T>> tEdges;
    HashMap<T, Node<T>> nodes;
    HashMap<T, Node<T>> out;

    @Override
    public Tree<T> getTree() {
        return tree;
    }

    @Override
    public EdgeMappPolicy<T> create() {
        ZeroInc ret = new ZeroInc();
        ret.tree = new HashTree();
        ret.tEdges = new HashMapSet<T, Edge<T>>();

        return ret;
    }

    @Override
    public void add(Edge<T> e, EdgeConnectionPolicy<T> ecp) {
        if (e.isVisible()) {
            tEdges.put(e.getSon(), e);
            if (tEdges.getAll(e.getSon()).size() == 1) {

                Node<T> n = tree.add(nodes.get(e.getFather()), e.getSon());
                /*
                 * if (nodes.get(e.getFather())==null){ l'ajout d'un père non
                 * existant peut-il être possible ?
                 *
                 * }
                 */
                nodes.put(n.getValue(), n);
            } else {
                Node<T> n = nodes.get(e.getSon());
                if (n != null) {
                    tree.move(null, n);

                }
            }
        }

    }

    @Override
    public void del(Edge<T> e, EdgeConnectionPolicy<T> ecp) {
        if (e.isVisible()) {
            assert (tEdges.remove(e.getSon(), e));
            if (tEdges.getAll(e.getSon()).size() == 1) {
                Node<T> n = nodes.get(e.getSon());
                tree.move(nodes.get(e.getFather()), n);
            }
        }
    }

    /*@Override
    public void modif(Edge<T> e, EdgeConnectionPolicy<T> ecp) {
        /*
         * une ex invisible devient visible
         *
        if (e.isVisible() && !tEdges.containsValue(e.getSon(), e)) {
            this.add(e, ecp);
        } else {
            /*
             * ex visible devient invisible
             *
            if (!e.isVisible() && tEdges.containsValue(e.getSon(), e)) {
                e.setVisible(true);
                del(e, ecp);
                e.setVisible(false);
            }
        }
    }*/
    @Override
    public void moved(T OdFather, Edge<T> e, EdgeConnectionPolicy<T> ecp){
        Node <T> n=nodes.get(e.getSon());
        Node <T> f=nodes.get(e.getFather());
        if (n!=null && e.isVisible()){
            if (f==null)
                f=tree.getRoot();
            tree.move(f, n);
        }
    }
    
}
