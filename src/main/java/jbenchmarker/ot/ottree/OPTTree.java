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
import collect.UnorderedNode;
import crdt.CRDT;
import crdt.CRDTMessage;
import crdt.OperationBasedOneMessage;
import crdt.PreconditionException;
import crdt.tree.CRDTTree;
import crdt.tree.orderedtree.CRDTOrderedTree;
import java.util.List;

/**
 *
 * @author Stephane Martin
 */
public class OPTTree<T> extends CRDTOrderedTree<T> {
    OTTreeNode root;
    @Override
    public CRDTMessage add(List<Integer> path, int p, T element) throws PreconditionException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public CRDTMessage remove(List<Integer> path) throws PreconditionException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void applyOneRemote(CRDTMessage op) {
        OTTreeRemoteOperation opt=(OTTreeRemoteOperation) ((OperationBasedOneMessage)op).getOperation();
        
    }

    @Override
    public OrderedNode<T> lookup() {
        return root;
    }

    @Override
    public CRDT<OrderedNode<T>> create() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    

    
}
