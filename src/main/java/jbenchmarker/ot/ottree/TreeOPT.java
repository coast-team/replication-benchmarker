/**
 * Replication Benchmarker
 * https://github.com/score-team/replication-benchmarker/ Copyright (C) 2012
 * LORIA / Inria / SCORE Team
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package jbenchmarker.ot.ottree;

import collect.OrderedNode;
import crdt.*;
import crdt.tree.orderedtree.CRDTOrderedTree;
import java.util.LinkedList;
import java.util.List;
import jbenchmarker.ot.soct2.OTAlgorithm;
import jbenchmarker.ot.soct2.OTMessage;

/**
 *
 * @param <T> label of tree
 * @author Stephane Martin
 */
public class TreeOPT<T> extends CRDTOrderedTree<T> {

    TreeOPTTTFNode root;

    /**
     * Local Add on the tree
     * @param path element is added at end of path
     * @param p in p position
     * @param element 
     * @return returns message to another replicas
     * @throws PreconditionException already added
     */
    @Override
    public CRDTMessage add(List<Integer> path, int p, T element) throws PreconditionException {
        LinkedList path2 = new LinkedList(path);
        path2.add(p);
        return new OperationBasedOneMessage(root.localApply(path2, TreeOPTTTFNodeOperation.OpType.ins, element));
    }

    /**
     * Local delete of element 
     * @param path the element is at end of path
     * @return return message to another replica 
     * @throws PreconditionException if element is already deleted
     */
    @Override
    public CRDTMessage remove(List<Integer> path) throws PreconditionException {
        return new OperationBasedOneMessage(root.localApply(new LinkedList(path), TreeOPTTTFNodeOperation.OpType.del, null));
    }

    /**
     * Apply one remote operation
     * @param op
     */
    @Override
    public void applyOneRemote(CRDTMessage op) {
        OTMessage mess = (OTMessage) ((OperationBasedOneMessage) op).getOperation();
        root.remoteApply(mess);
    }

    /**
     * get tree
     * @return root of tree
     */
    @Override
    public OrderedNode<T> lookup() {
        return root;
    }

    /**
     * constructor with consistency algorithm
     * @param algo
     */
    public TreeOPT(Factory<OTAlgorithm> algo) {
        root = new TreeOPTTTFNode(null, null, algo.create());
    }

    /**
     * set replica identifier 
     * @param replicaNumber
     */
    @Override
    public void setReplicaNumber(int replicaNumber) {
        super.setReplicaNumber(replicaNumber);
        root.setReplicaNumber(replicaNumber);
    }

    /**
     * create new replica
     * @return
     */
    @Override
    public CRDT<OrderedNode<T>> create() {
        return new TreeOPT<T>(this.root.soct2);
    }
}
