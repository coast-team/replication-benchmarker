/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
