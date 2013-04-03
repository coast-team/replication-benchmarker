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
package jbenchmarker.ot.ottree;

import collect.OrderedNode;
import collect.SimpleNode;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import jbenchmarker.ot.soct2.OTAlgorithm;
import jbenchmarker.ot.soct2.OTMessage;
import jbenchmarker.ot.soct2.SOCT2;

/**
 *
 * @param <T> 
 * @author Stephane Martin <stephane.martin@loria.fr>
 */
public class TreeOPTTTFNode<T> implements OrderedNode<T>, Serializable {

    int visibleChildren = 0;
    boolean visible;
    TreeOPTTTFNode<T> father;
    T contains;
    OTAlgorithm<TreeOPTTTFNodeOperation> soct2;
    ArrayList<TreeOPTTTFNode<T>> childrens;

    /**
     * Create new node with 
     * @param father father node
     * @param contains label of new node 
     * @param soct2 consistency algorithm
     */
    public TreeOPTTTFNode(TreeOPTTTFNode<T> father, T contains, OTAlgorithm<TreeOPTTTFNodeOperation> soct2) {
        this.father = father;
        this.contains = contains;
        this.soct2 = soct2;
        visible = true;
        childrens = new ArrayList<TreeOPTTTFNode<T>>();
    }

    /**
     * return the visibility flag
     * @return true if this node is visible
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * Get real position element stocked in array
     * @param positionInView position with only visible element 
     * @return return real position
     */
    public int viewToModel(int positionInView) {
        int positionInchildrens = 0;
        int visibleCharacterCount = 0;

        while (positionInchildrens < this.childrens.size() && (visibleCharacterCount < positionInView || (!this.childrens.get(positionInchildrens).isVisible()))) {
            if (this.childrens.get(positionInchildrens).isVisible()) {
                visibleCharacterCount++;
            }
            positionInchildrens++;
        }

        return positionInchildrens;
    }

    /**
     * Local apply of operation 
     * @param path list of position 
     * @param optype ins or del operation 
     * @param contains and element to add
     * @return OT message from consistency algorithm
     */
    public OTMessage localApply(LinkedList<Integer> path, TreeOPTTTFNodeOperation.OpType optype, T contains) {
        TreeOPTTTFNodeOperation operation;
        int pos = viewToModel(path.remove(0));
        if (path.isEmpty()) {
            operation = new TreeOPTTTFNodeOperation(optype, contains, pos, soct2.getReplicaNumber());
            applyOnThis(pos, operation);
        } else {
            OTMessage mess = this.childrens.get(pos).localApply(path, optype, contains);
            operation = new TreeOPTTTFNodeOperation(TreeOPTTTFNodeOperation.OpType.child, mess, pos, soct2.getReplicaNumber());
        }
        return soct2.estampileMessage(operation);
    }

    /**
     * apply from remote replica 
     * @param message message from another replica
     */
    public void remoteApply(OTMessage<TreeOPTTTFNodeOperation> message) {
       
        TreeOPTTTFNodeOperation<T> oop = soct2.integrateRemote(message);
        
        int pos = oop.getPosition();
        if (oop.getType() == TreeOPTTTFNodeOperation.OpType.child) {
            OTMessage opn = (OTMessage) oop.getContain();
            this.childrens.get(pos).remoteApply(opn);
        } else {
            applyOnThis(pos, oop);
        }
       
    }

    /*
     * Apply operation on this node and not recurcively
     */
    private void applyOnThis(int pos, TreeOPTTTFNodeOperation<T> oop) {
        if (oop.getType() == TreeOPTTTFNodeOperation.OpType.del) {
            TreeOPTTTFNode c = this.childrens.get(pos);
            if (c.isVisible()) {
                --visibleChildren;
            }
            c.setVisible(false);
        } else if (oop.getType() == TreeOPTTTFNodeOperation.OpType.ins) {
            TreeOPTTTFNode add = new TreeOPTTTFNode<T>(this, oop.getContain(), soct2.create());
            add.setReplicaNumber(this.soct2.getReplicaNumber());
            this.childrens.add(pos, add);
            ++visibleChildren;
        }
    }
    /*
     * public void apply(Operation op) { TreeOPTTTFNodeOperation<T> oop =
     * (TreeOPTTTFNodeOperation<T>) op; int pos = oop.getPosition();
     *
     * if (oop.getType() == TreeOPTTTFNodeOperation.OpType.del) { TreeOPTTTFNode
     * c = this.childrens.get(pos); if (c.isVisible()) { --visibleChildren; }
     * c.setVisible(false); } else if (oop.getType() ==
     * TreeOPTTTFNodeOperation.OpType.ins) { this.childrens.add(pos, new
     * TreeOPTTTFNode<T>(this, oop.getContain(),
     * (SOCT2<TreeOPTTTFNodeOperation>) soct2.create())); ++visibleChildren; }
     * else if (oop.getType() == TreeOPTTTFNodeOperation.OpType.child) {
     * TreeOPTTTFNodeOperation opn=(TreeOPTTTFNodeOperation)oop.getContain();
     * this.getChild(oop.getPosition()).remoteApply(opn); } }
     */
    /*
     * public OTTreeNode(OTTreeNode<T> father, T contains) { this.father =
     * father; this.contains = contains; this.visible = true; }
     */

    /**
     * Make new visible node 
     * @param father father node
     * @param contains label of node
     * @param soct2 and consistancy algorithm
     */
    public TreeOPTTTFNode(TreeOPTTTFNode<T> father, T contains, SOCT2<TreeOPTTTFNodeOperation> soct2) {
        this.father = father;
        this.contains = contains;
        this.visible = true;
        this.soct2 = soct2;
    }

    /**
     * change the visible flag
     * @param visible
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    /*
     * ----
     */
    @Override
    public int getChildrenNumber() {
        return visibleChildren;
    }

    @Override
    public TreeOPTTTFNode<T> getChild(int p) {
        return childrens.get(viewToModel(p));
    }

    @Override
    public T getValue() {
        return contains;
    }

    @Override
    public List<? extends OrderedNode<T>> getElements() {
        LinkedList ret = new LinkedList();
        for (TreeOPTTTFNode n : childrens) {
            if (n.isVisible()) {
                ret.add(n);
            }
        }
        return ret;
    }

    @Override
    public TreeOPTTTFNode<T> createNode(T elem) {
        throw new UnsupportedOperationException("createNode is not supported yet.");
    }

    @Override
    public void setReplicaNumber(int replicaNumber) {
        soct2.setReplicaNumber(replicaNumber);
        for (TreeOPTTTFNode node : childrens) {
            node.setReplicaNumber(replicaNumber);
        }
    }

    @Override
    public String toString() {
        return "TreeOPTTTFNode(" + "visibleChildren=" + visibleChildren + ", visible=" + visible + ", contains=" + contains + /*
                 * ", soct2=" + soct2 +
                 */ ",) {" + childrens + '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TreeOPTTTFNode<T> other = (TreeOPTTTFNode<T>) obj;
        if (this.visibleChildren != other.visibleChildren) {
            return false;
        }
        if (this.visible != other.visible) {
            return false;
        }
        if (this.contains != other.contains && (this.contains == null || !this.contains.equals(other.contains))) {
            return false;
        }
        if (this.childrens != other.childrens && (this.childrens == null || !this.childrens.equals(other.childrens))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + this.visibleChildren;
        hash = 29 * hash + (this.visible ? 1 : 0);
        hash = 29 * hash + (this.contains != null ? this.contains.hashCode() : 0);
        hash = 29 * hash + (this.childrens != null ? this.childrens.hashCode() : 0);
        return hash;
    }

    @Override
    public SimpleNode<T> getFather() {
        return father;
    }

    @Override
    public Iterator<? extends SimpleNode<T>> iterator() {
        return childrens.iterator();
    }

    @Override
    public boolean isChildren(SimpleNode<T> n) {
        TreeOPTTTFNode node=(TreeOPTTTFNode)n;
        return node.visible && this.childrens.contains(node);
    }
}
