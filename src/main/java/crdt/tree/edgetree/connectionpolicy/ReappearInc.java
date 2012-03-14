/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.tree.edgetree.connectionpolicy;

import collect.HashMapSet;
import crdt.set.CRDTSet;
import crdt.set.SetOperation;
import crdt.tree.edgetree.Edge;
import java.util.Observable;
import java.util.Set;

/**
 *
 * @author moi
 */
public class ReappearInc<T> extends EdgeConnectionPolicy<T> {
    /*
     * TODO : check me if u can !
     *
     */

    HashMapSet<T, Edge<T>> orphans;/*
     * father est la clef
     */

    HashMapSet<T, Edge<T>> tEdges;
    HashMapSet<T, Edge<T>> fEdges;
    //HashTree tree;

    @Override
    public EdgeConnectionPolicy<T> create() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void update(Observable o, Object o1) {
        if (o instanceof CRDTSet
                && o1 instanceof SetOperation) {
            SetOperation o2 = (SetOperation) o1;
            Edge<T> edge = ((Edge<T>) o2.getContent());
            if (o2.getType() == SetOperation.OpType.add) {
                /*
                 * Si c'est un Add
                 */

                if (!tEdges.getAll(edge.getSon()).contains(edge)) {
                    tEdges.put(edge.getSon(), edge);
                    fEdges.put(edge.getFather(), edge);
                }
                edge.setVisibleInCRDT(true);
                recurcifReappear(edge);




            } else {
                /*
                 * Si C'est un Del
                 */
                edge.setVisibleInCRDT(false);
                recurcifDeletion(edge);


            }
        }

    }

    private void recurcifDeletion(Edge<T> e) {
        if (e.isVisible() && !e.isVisibleInCRDT()) {
            /*
             * Aucun fils est visible donc on va le rendre invisible. et checker
             * les pères Si on a un fils visible on sort de la fonction
             */
            for (Edge eChild : fEdges.getAll(e.getSon())) {
                if (eChild.isVisible()) {
                    return;
                }
            }
            e.setVisible(false);
            emp.del(e, this);
            for (Edge<T> ef : tEdges.getAll(e.getFather())) {
                recurcifDeletion(ef);
            }
        }
    }

    private void recurcifReappear(Edge<T> e) {
        if (!e.isVisible()) {
            emp.add(e, this);
            e.setVisible(true);
            for (Edge<T> e2 : tEdges.getAll(e.getFather())) {
                recurcifReappear(e2);
            }
        }
    }

    @Override
    public Set<Edge<T>> getEdges() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
