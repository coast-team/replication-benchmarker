/*
 *  Replication Benchmarker
 *  https://github.com/score-team/replication-benchmarker/
 *  Copyright (C) 2013 LORIA / Inria / SCORE Team
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package crdt.tree.fctree;

import crdt.CRDTMessage;
import crdt.OperationBasedOneMessage;
import crdt.tree.fctree.Operations.ChX;
import crdt.tree.fctree.Operations.Nop;
import crdt.tree.fctree.policy.PostAction;
import java.io.Serializable;
import java.util.List;

/**
 *
 * @author Stephane Martin <stephane@stephanemartin.fr>
 */
public class FCTreeGf<T> extends FCTree<T> implements Serializable{
     @Override
    public CRDTMessage rename(List<Integer> path, T newValue) {
        FCNodeGf node =(FCNodeGf) root.getNodeFromPath(path);
        ChX operation = new ChX(this.idFactory.createId(), node, newValue, FCNodeGf.FcLabels.contain);
        operation.apply(node, this);
        return new OperationBasedOneMessage(operation);
    }

    @Override
    public CRDTMessage move(List<Integer> from, List<Integer> to, int p) {
        FCNodeGf node =(FCNodeGf) root.getNodeFromPath(from);
        if (node.getId().getReplicaNumber() < 0) {
            return new OperationBasedOneMessage(new Nop(this.idFactory.createId()));
        }
        FCNodeGf nFather;
        if (to.isEmpty()) {
            nFather = (FCNodeGf)getRoot();
        } else {
            //List<Integer> toF = to.subList(0, to.size() - 1);
            nFather =(FCNodeGf)root.getNodeFromPath(to);
        }

        //int p = to.get(to.size() - 1);


        if (nFather.getId().equals(node.getFather().getId()) && p > from.get(from.size() - 1)) {
            p++;
        }
        FCNode gnode = nFather.getChild(p - 1);
        FCNode lnode = nFather.getChild(p);
        FCIdentifier id = idFactory.createId();

        ChX op = new ChX(id, node, positionFactory.createBetweenNode(gnode, lnode, id), FCNodeGf.FcLabels.priority);

        CRDTMessage ret = new OperationBasedOneMessage(op);
        if (!nFather.getId().equals(node.getFather().getId())) {
            ChX move = new ChX(idFactory.createId(), node, nFather.getId(), FCNodeGf.FcLabels.fatherId);
            move.apply(node, this);
            ret = ret.concat(new OperationBasedOneMessage(move));
        }
        op.apply(node, this);
        return ret;
    }
    
     public FCTreeGf() {
        this(false);
    }
    /**
     * 
     * @param action Action trigger after add/del/move operation
     * @param removeEntireTree Remove entire subtree on local remove
     */
    public FCTreeGf(PostAction action,boolean removeEntireTree) {
        this(removeEntireTree);
        this.postAction = action;
        if (postAction != null) {
            postAction.setTree(this);
        }
    }

    /**
     * 
     * @param removeEntireTree Remove entire subtree on local remove
     */
    public FCTreeGf(boolean removeEntireTree) {
        super(removeEntireTree);
        FCIdentifier idroot = new FCIdentifier(-1, 0);
        root = new FCNodeGf(null, null, null, idroot);
        map.put(idroot,(FCNode) root);
        this.removeEntireSubtree = removeEntireTree;
    }
    
    public FCTreeGf(PostAction action) {
        this(action,false);
    }
    
   /**
     * factory of fctree
     *
     * @return
     */
    @Override
    public FCTreeGf<T> create() {
        return new FCTreeGf(postAction == null ? null : postAction.clone());
    }
}
