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
import crdt.tree.orderedtree.PositionIdentifier;
import crdt.tree.orderedtree.Positioned;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import jbenchmarker.core.Operation;

/**
 *
 * @author Stephane Martin <stephane.martin@loria.fr>
 */
public class OTTreeNode<T> implements OrderedNode<T> {

    private int visibleChildren = 0;
    private boolean visible;
    private OTTreeNode<T> father;
    private T elem;
    private ArrayList<OTTreeNode<T>> childrens;

    public boolean isVisible() {
        return visible;
    }

    public List<Integer> viewToModelRecurcive(List<Integer> list) {
        LinkedList<Integer> ret = new LinkedList();
        ret.add(viewToModel(list.get(0)));
        if (list.size() > 1) {
            ret.addAll(childrens.get(ret.get(0)).viewToModelRecurcive(list.subList(1, list.size())));
        }
        return ret;
    }

    public int viewToModel(int positionInView) {
        //System.out.print("posview:"+positionInView);
        int positionInchildrens = 0;
        int visibleCharacterCount = 0;

        while (positionInchildrens < this.childrens.size() && (visibleCharacterCount < positionInView || (!this.childrens.get(positionInchildrens).isVisible()))) {
            if (this.childrens.get(positionInchildrens).isVisible()) {
                visibleCharacterCount++;
            }
            positionInchildrens++;
        }

        //System.out.println("->"+positionInchildrens);
        return positionInchildrens;
    }
    /*
     * public void remoteApply(Operation op){
     *
     * }
     */

    public void apply(Operation op, int level) {
        OTTreeRemoteOperation<T> oop = (OTTreeRemoteOperation<T>) op;
        int pos = oop.getPath().get(level);
        if (level == oop.getPath().size() - 1) {
            if (oop.getType() == OTTreeRemoteOperation.OpType.del) {
                OTTreeNode c = this.childrens.get(pos);
                if (c.isVisible()) {
                    --visibleChildren;
                }
                c.setVisible(false);
            } else if (oop.getType() == OTTreeRemoteOperation.OpType.ins) {
                this.childrens.add(pos, new OTTreeNode<T>(this, oop.getContain()));
                ++visibleChildren;
            }
        } else {
            childrens.get(pos).apply(op, level + 1);
        }

    }

    /*
     * public OTTreeNode(OTTreeNode<T> father, T contains) { this.father =
     * father; this.contains = contains; this.visible = true;
     */
    public OTTreeNode(OTTreeNode<T> father, T contains) {
        this.father = father;
        this.elem = contains;
        this.visible = true;
        this.childrens = new ArrayList();
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    /*
     * ----
     */
    @Override
    public int childrenNumber() {
        return this.visibleChildren;
    }

    @Override
    public OrderedNode<T> getChild(int p) {
        return childrens.get(viewToModel(p));
    }

    @Override
    public T getValue() {
        return elem;
    }

    @Override
    public List<? extends OrderedNode<T>> getElements() {
        LinkedList ret = new LinkedList();
        for (OTTreeNode n : childrens) {
            if (n.isVisible()) {
                ret.add(n);
            }
        }
        return ret;
    }

    @Override
    public OrderedNode<T> createNode(T elem) {
        throw new UnsupportedOperationException("createNode is not supported yet.");
    }

    @Override
    public void setReplicaNumber(int replicaNumber) {
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final OTTreeNode<T> other = (OTTreeNode<T>) obj;
        if (this.visibleChildren != other.visibleChildren) {
            return false;
        }
        if (this.visible != other.visible) {
            return false;
        }
        if (this.elem != other.elem && (this.elem == null || !this.elem.equals(other.elem))) {
            return false;
        }
        if (this.childrens != other.childrens && (this.childrens == null || !this.childrens.equals(other.childrens))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + this.visibleChildren;
        hash = 29 * hash + (this.visible ? 1 : 0);
        hash = 29 * hash + (this.elem != null ? this.elem.hashCode() : 0);
        hash = 29 * hash + (this.childrens != null ? this.childrens.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder t = new StringBuilder();
        t.append(/*
                 * "OTTreeNode{" + /*"visibleChildren=" + visibleChildren + ",
                 * visible=" + visible + /*", father=" +
                 * father==null?"null":father.contains +
                 */
                /*
                 * ", contains=" +
                 */"OTTreeNode " + elem + (this.isVisible() ? "" : "*") + "{ ");
        for (OTTreeNode n : this.childrens) {
            if (n.isVisible()) {
                t.append(n);
            }
        }
        t.append("}");

        return t.toString();
    }
}
