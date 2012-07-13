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
        this.root = new OTTreeNode(null, null);
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
    @Override
    public void setReplicaNumber(int replica){
        super.setReplicaNumber(replica);
        soct2.setReplicaNumber(replica);
    }
    @Override
    public String toString(){
        return ""+this.soct2.getReplicaNumber()+"["+root+"]";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final OTTree<T> other = (OTTree<T>) obj;
        if (this.root != other.root && (this.root == null || !this.root.equals(other.root))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 13 * hash + (this.root != null ? this.root.hashCode() : 0);
        return hash;
    }
    
}
