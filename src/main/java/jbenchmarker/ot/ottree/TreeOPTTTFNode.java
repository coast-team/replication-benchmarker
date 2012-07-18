/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker.ot.ottree;

import collect.OrderedNode;
import crdt.tree.orderedtree.PositionIdentifier;
import crdt.tree.orderedtree.Positioned;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import jbenchmarker.core.Operation;
import jbenchmarker.ot.soct2.OTAlgorithm;
import jbenchmarker.ot.soct2.OTMessage;
import jbenchmarker.ot.soct2.SOCT2;

/**
 *
 * @author Stephane Martin <stephane.martin@loria.fr>
 */
public class TreeOPTTTFNode<T> implements OrderedNode<T> {
    
    int visibleChildren = 0;
    boolean visible;
    TreeOPTTTFNode<T> father;
    T contains;
    OTAlgorithm<TreeOPTTTFNodeOperation> soct2;
    ArrayList<TreeOPTTTFNode<T>> childrens;
    
    public TreeOPTTTFNode(TreeOPTTTFNode<T> father, T contains, OTAlgorithm<TreeOPTTTFNodeOperation> soct2) {
        this.father = father;
        this.contains = contains;
        this.soct2 = soct2;
        visible = true;
        childrens = new ArrayList<TreeOPTTTFNode<T>>();
    }
    
    public boolean isVisible() {
        return visible;
    }
    
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
    
    public OTMessage localApply(List<Integer> path, TreeOPTTTFNodeOperation.OpType optype, T contains) {
        TreeOPTTTFNodeOperation operation;
        int pos = viewToModel(path.remove(0));
        if (path.isEmpty()) {
            operation = new TreeOPTTTFNodeOperation(optype, contains, pos, soct2.getReplicaNumber());
            applyOnThis(pos, operation);
        } else {
            OTMessage mess = this.getChild(pos).localApply(path, optype, contains);
            operation = new TreeOPTTTFNodeOperation(TreeOPTTTFNodeOperation.OpType.child, mess, pos, soct2.getReplicaNumber());
        }
        return soct2.estampileMessage(operation);
    }
    
    public void remoteApply(OTMessage<TreeOPTTTFNodeOperation> message) {
        TreeOPTTTFNodeOperation<T> oop = soct2.integrateRemote(message);
        int pos = oop.getPosition();
        if (oop.getType() == TreeOPTTTFNodeOperation.OpType.child) {
            OTMessage opn = (OTMessage) oop.getContain();
            this.getChild(oop.getPosition()).remoteApply(opn);
        }else{
            applyOnThis(pos,oop);
        }
    }
 private void applyOnThis(int pos,TreeOPTTTFNodeOperation<T> oop){
     if (oop.getType() == TreeOPTTTFNodeOperation.OpType.del) {
            TreeOPTTTFNode c = this.childrens.get(pos);
            if (c.isVisible()) {
                --visibleChildren;
            }
            c.setVisible(false);
        } else if (oop.getType() == TreeOPTTTFNodeOperation.OpType.ins) {
            this.childrens.add(pos, new TreeOPTTTFNode<T>(this, oop.getContain(), soct2.create()));
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
     * this.getChild(oop.getPosition()).remoteApply(opn); }
    }
     */
    /*
     * public OTTreeNode(OTTreeNode<T> father, T contains) { this.father =
     * father; this.contains = contains; this.visible = true; }
     */
    public TreeOPTTTFNode(TreeOPTTTFNode<T> father, T contains, SOCT2<TreeOPTTTFNodeOperation> soct2) {
        this.father = father;
        this.contains = contains;
        this.visible = true;
        this.soct2 = soct2;
    }
    
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    /*
     * ----
     */
    @Override
    public int childrenNumber() {
        throw new UnsupportedOperationException("Not supported yet.");
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
}
