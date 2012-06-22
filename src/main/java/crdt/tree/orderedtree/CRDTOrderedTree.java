/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.tree.orderedtree;

import collect.OrderedNode;
import crdt.CRDT;
import crdt.CRDTMessage;
import jbenchmarker.core.Operation;
import crdt.PreconditionException;
import java.util.List;

/**
 *
 * @author score
 */
public abstract class CRDTOrderedTree<T> extends CRDT<OrderedNode<T>> {

    abstract public CRDTMessage add(List<Integer> path, int p, T element) throws PreconditionException;

    abstract public CRDTMessage remove(List<Integer> path) throws PreconditionException;

    @Override
    final public CRDTMessage applyLocal(Operation op) throws PreconditionException {
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
