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
package crdt.tree.edgetree.connectionpolicy;

import collect.HashMapSet;
import crdt.set.CRDTSet;
import crdt.set.SetOperation;
import crdt.tree.edgetree.Edge;
import java.util.Observable;
import java.util.Set;

/**
 *
 * @author Stephane Martin
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
