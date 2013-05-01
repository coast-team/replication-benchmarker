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

import crdt.tree.graphtree.Edge;
import crdt.tree.graphtree.GraphMappPolicy;
import java.util.Observable;

/**
 *
 * @author Stephane Martin
 */
public abstract class GraphMappPolicyInc<T> extends GraphMappPolicy<T> {

    @Override
    public void update(Observable o, Object op) {

        if (op instanceof MappingUpdateOperation) {
            MappingUpdateOperation<Edge<T>> opm = (MappingUpdateOperation) op;
            switch (opm.getType()) {
                case add:
                    //if (opm.getObject() instanceof Edge) {
                        addEdge((Edge) opm.getObject());
                    /*} else {
                        addNode((T) opm.getObject());
                    }*/
                    break;
                case del:
                    //if (opm.getObject() instanceof Edge) {
                        delEdge((Edge) opm.getObject());
                    /*} else {
                        delNode((T) opm.getObject());
                    }*/
                    break;
                case move:
                    //if (opm.getObject() instanceof Edge) {
                        moveEdge(opm.getOld(),(Edge) opm.getObject());
                    /*} else {
                        throw new UnsupportedOperationException("On ne déplace pas de noeuds !");
                    }*/
            }
        }
    }

    abstract void addEdge(Edge<T> e);

    abstract void delEdge(Edge<T> e);

    /*abstract void addNode(T e);

    abstract void delNode(T e);*/

    abstract void moveEdge(Edge<T> OldFather,Edge<T> moved);
}
