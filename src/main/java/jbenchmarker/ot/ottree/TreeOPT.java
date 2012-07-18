/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker.ot.ottree;

import collect.OrderedNode;
import crdt.*;
import crdt.tree.orderedtree.CRDTOrderedTree;
import java.util.List;
import jbenchmarker.ot.soct2.OTAlgorithm;
import jbenchmarker.ot.soct2.OTMessage;

/**
 *
 * @author Stephane Martin
 */
public class TreeOPT<T> extends CRDTOrderedTree<T> {

    TreeOPTTTFNode root;

    @Override
    public CRDTMessage add(List<Integer> path, int p, T element) throws PreconditionException {
        return new OperationBasedOneMessage(root.localApply(path, TreeOPTTTFNodeOperation.OpType.ins, element));
    }
    
    @Override
    public CRDTMessage remove(List<Integer> path) throws PreconditionException {
        return new OperationBasedOneMessage(root.localApply(path, TreeOPTTTFNodeOperation.OpType.del, null));
    }
    
    @Override
    public void applyOneRemote(CRDTMessage op) {
        OTMessage mess = (OTMessage) ((OperationBasedOneMessage) op).getOperation();
        root.remoteApply(mess);
    }
    
    @Override
    public OrderedNode<T> lookup() {
        return root;
    }
    
    public TreeOPT(Factory<OTAlgorithm> algo) {
        root = new TreeOPTTTFNode(null, null, algo.create());
    }
    
    @Override
    public void setReplicaNumber(int replicaNumber) {
        super.setReplicaNumber(replicaNumber);
        root.setReplicaNumber(replicaNumber);
    }
    
    @Override
    public CRDT<OrderedNode<T>> create() {
        return new TreeOPT<T>(this.root.soct2);        
    }
}
