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
package crdt.tree.orderedtree;

import collect.OrderedNode;
import crdt.CRDT;
import crdt.CRDTMessage;
import crdt.PreconditionException;
import java.util.List;
import jbenchmarker.core.LocalOperation;

/**
 *
 * @author score
 */
public abstract class CRDTOrderedTree<T> extends CRDT<OrderedNode<T>> {

    abstract public CRDTMessage add(List<Integer> path, int p, T element) throws PreconditionException;

    abstract public CRDTMessage remove(List<Integer> path) throws PreconditionException;

    @Override
    final public CRDTMessage applyLocal(LocalOperation op) throws PreconditionException {
        OrderedTreeOperation<T> top = (OrderedTreeOperation<T>) op;
        if (top.getType() == OrderedTreeOperation.OpType.add) {
            return add(top.getPath(), top.getPosition(), top.getContent());
        } else {
            return remove(top.getPath());
        }
    }

    public static boolean sameNode(OrderedNode n1, OrderedNode n2) {
        if (n1 == n2 && n1 == null) {
            return true;
        }
        if (n1 != n2 && (n1 == null || n2 == null)) {
            return false;
        }
        if (n1.getValue() != n2.getValue()
                && (n1.getValue() == null || !n1.getValue().equals(n2.getValue()))) {
            return false;
        }
        if (n1.childrenNumber() != n2.childrenNumber()) {
            return false;
        }
        for (int i = 0; i < n1.childrenNumber(); ++i) {
            if (!sameNode(n1.getChild(i), (n2.getChild(i)))) {
                return false;
            }
        }
        return true;
    }

    public OrderedNode<T> getNodeFromPath(List<Integer> path){
        OrderedNode<T> node=this.lookup();
        for (Integer i:path){
            node=node.getChild(i);
        }
        return node;
    }
    public boolean sameLookup(CRDTOrderedTree tree) {
        return sameNode(this.lookup(), (OrderedNode) tree.lookup());
    }
    /*
     * public boolean same(OrderedNode<T> other) { if (other == null) { return
     * false; }
     *
     * }
     */
}
