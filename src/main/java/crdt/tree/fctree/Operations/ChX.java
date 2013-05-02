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
package crdt.tree.fctree.Operations;

import crdt.tree.fctree.FCIdentifier;
import crdt.tree.fctree.FCLabel;
import crdt.tree.fctree.FCNodeGf;
import crdt.tree.fctree.FCNodeGf.FcLabels;
import crdt.tree.fctree.FCOperation;
import crdt.tree.fctree.FCTree;
import jbenchmarker.core.Operation;

/**
 *
 * @author Stephane Martin <stephane.martin@loria.fr>
 */
public class ChX<T> extends FCOperation {

    FCNodeGf.FcLabels whichChange;
    FCLabel<T> newLabel;
    FCIdentifier nodeId;

    public ChX(FCIdentifier id, FCIdentifier nodeId, FCLabel newLabel, FCNodeGf.FcLabels whichChange) {
        super(id);
        this.nodeId = nodeId;
        this.newLabel = newLabel;
        this.whichChange = whichChange;
    }

    public ChX(FCIdentifier id,FCNodeGf<T> fcnode,T newValue,FCNodeGf.FcLabels whichChange){
        super(id);
        this.nodeId=fcnode.getId();
        this.newLabel = new FCLabel<T>(id, newValue);
        this.newLabel.setVersion(fcnode.getLabelOf(whichChange).getVersion()+1);
        this.whichChange = whichChange;
    }
    @Override
    public Operation clone() {
        return new ChX(this.getId(), nodeId, newLabel, whichChange);
    }

    @Override
    public void apply(FCTree tree) {
        FCNodeGf node =(FCNodeGf) tree.getNodeById(nodeId);
        apply(node,tree);
    }
    public void apply(FCNodeGf node,FCTree tree ){
        if (node != null) {
            FCLabel label = node.getLabelOf(whichChange);
            if (label.getVersion() < newLabel.getVersion()
                    || (label.getVersion() == newLabel.getVersion()
                    && label.getId().compareTo(newLabel.getId()) > 0)) {
                node.setLabelOf(whichChange, newLabel);
                if(whichChange==FcLabels.fatherId){
                    FCNodeGf newFather=(FCNodeGf)tree.getNodeById((FCIdentifier)this.newLabel.getLabel());
                    FCNodeGf oldFather =(FCNodeGf)node.getFather();
                    if(oldFather !=null){
                        oldFather.delChildren(node);
                    }
                    node.setFather(newFather);
                    if(newFather!=null){
                        newFather.addChildren(node);
                    } 
                    if(tree.getPostAction()!=null){
                        tree.getPostAction().postMove(this, node);
                    }
                }else if(whichChange==FcLabels.priority){
                    FCNodeGf father=(FCNodeGf)node.getFather();
                    father.delChildren(node);
                    father.addChildren(node);
                }
            }
        }
    }

    @Override
    public FCIdentifier[] DependOf() {
        FCIdentifier[] ret = {nodeId};
        return ret;
    }
}
