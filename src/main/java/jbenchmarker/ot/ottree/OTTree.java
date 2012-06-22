/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
 * @author Stephane Martin
 */
public class OTTree<T> extends CRDTOrderedTree<T> {

    OTTreeNode root;
    OTAlgorithm soct2;

    public OTTree(OTAlgorithm soct2) {
        this.root = new OTTreeNode(null, this);
        this.soct2 = soct2;
    }

    @Override
    public CRDTMessage add(List<Integer> path, int p, T element) throws PreconditionException {
        List<Integer> npath = new LinkedList<Integer>();
        npath.addAll(path);
        npath.add(p);
        npath = root.viewToModelRecurcive(npath);
        OTTreeRemoteOperation<T> n = new OTTreeRemoteOperation<T>(npath, element, this.soct2.getReplicaNumber(), OTTreeRemoteOperation.OpType.ins);
        root.apply(n, 0);
        return new OperationBasedOneMessage(soct2.estampileMessage(n));
    }

    @Override
    public CRDTMessage remove(List<Integer> path) throws PreconditionException {
        List<Integer> npath;
        npath = root.viewToModelRecurcive(path);
        OTTreeRemoteOperation<T> n = new OTTreeRemoteOperation<T>(npath, this.soct2.getReplicaNumber(), OTTreeRemoteOperation.OpType.del);
        root.apply(n, 0);
        return new OperationBasedOneMessage(soct2.estampileMessage(n));
    }

    @Override
    public void applyOneRemote(CRDTMessage op) {
        OTTreeRemoteOperation opt = (OTTreeRemoteOperation) soct2.integrateRemote((OTMessage) ((OperationBasedOneMessage) op).getOperation());
        root.apply(opt, 0);
    }

    @Override
    public OrderedNode<T> lookup() {
        return root;
    }

    @Override
    public CRDT<OrderedNode<T>> create() {
        return new OTTree<T>((OTAlgorithm)soct2.create());
    }
}
