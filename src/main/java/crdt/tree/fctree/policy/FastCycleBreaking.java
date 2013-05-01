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
 
package crdt.tree.fctree.policy;

import crdt.tree.fctree.FCIdentifier;
import crdt.tree.fctree.FCNode;
import crdt.tree.fctree.FCPosition;
import crdt.tree.fctree.FCTree;
import crdt.tree.fctree.Operations.Add;
import crdt.tree.fctree.Operations.ChX;
import crdt.tree.fctree.Operations.Del;
import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;

/**
 *
 * @author Stephane Martin <stephane.martin@loria.fr>
 */
public class FastCycleBreaking<T> implements PostAction,Serializable {

    static final FCIdentifier garbageAddress = new FCIdentifier(-1, 1);
    FCNode garbage;
    FCTree tree;
    T garbageName;
    
    public FastCycleBreaking() {
    }

    public FastCycleBreaking(T e) {
       garbageName=e; 
    }

    @Override
    public void setTree(FCTree tree) {
        this.tree = tree;
        this.garbage = tree.getNodeById(garbageAddress);
        if (garbage == null) {
            FCPosition pos = tree.getPositionFactory().createBetweenNode(null, null, garbageAddress);
            garbage = new FCNode(tree.getRoot(), garbageName, pos, garbageAddress);
            tree.getRoot().addChildren(garbage);
        }
    }

    /**
     * *
     * We assume that at a move close a cycle. Because if it is not a case it
     * will be fixed previously and it doesn't a cycle. If a move break a cycle,
     * the headfather will be breaked and
     */
    @Override
    public void postMove(ChX operation, FCNode node) {
        FCNode hf=node.getHeadFather();
        if(hf!=null && !hf.isDeleted() && hf.getFather()==garbage){
            garbage.delChildren(hf);
            FCNode oldFather=hf.getOldFather();
            hf.setFather(oldFather);
            hf.setOldFather(null);
            oldFather.addChildren(hf);
            
        }
        LinkedList<FCNode> pile = new LinkedList<FCNode>();
        FCNode n = node.getFather();
        pile.add(node);
        FCNode lowerID = node;
        while (n != tree.getRoot() && n != node) {
            //pile.push(n);
            pile.add(n);
            if (n.getId().compareTo(lowerID.getId()) > 0) {
                lowerID = n;
            }
            n = n.getFather();
        }
        if (n != tree.getRoot()) {
            /* Root lower id*/
            lowerID.setOldFather(lowerID.getFather());
            lowerID.getFather().delChildren(lowerID);
            lowerID.setFather(garbage);
            garbage.addChildren(lowerID);
            for (FCNode nodehf : pile) {
                nodehf.setHeadFather(lowerID);
            }
        }
    }

    @Override
    public void postDel(Del operation, FCNode node) {
        Iterator<FCNode> it = node.iterator();
        while (it.hasNext()) {
            FCNode no = it.next();
            no.setOldFather(null);
            no.setFather(garbage);
            garbage.addChildren(no);
            FCNode hf = no.getHeadFather();
            if (hf != null && hf.getOldFather() != null) {
                garbage.delChildren(hf);
                hf.setFather(hf.getOldFather());
            }
        }
    }

    @Override
    public void postAdd(Add operation, FCNode node) {
        if (node.getFather() == null) {
            node.setFather(garbage);
            garbage.addChildren(node);
        }
    }

    @Override
    public PostAction clone() {
        FastCycleBreaking ret = new FastCycleBreaking();
        ret.tree = this.tree;
        ret.garbage = this.garbage;
        return ret;
    }
}
