/**
 * Replication Benchmarker
 * https://github.com/score-team/replication-benchmarker/ Copyright (C) 2012
 * LORIA / Inria / SCORE Team
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package crdt.tree.fctree;

import collect.OrderedNode;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

/**
 *
 * @param <T>
 * @author Stephane Martin <stephane.martin@loria.fr>
 */
public class FCNode<T> implements OrderedNode<T>, Iterable<FCNode<T>> {

    // private ArrayList<FCNode<T>> childrens;
    private ArrayList<FCNode<T>> childrens;
    private FCIdentifier id;
    private FCLabel<FCPosition> priorite;
    private FCLabel<T> contain;
    private FCLabel<FCIdentifier> fatherID;
    private FCNode<T> father;
    private FCNode<T> oldFather;
    //private FCNode<T> headFather;
    private WeakReference<FCNode<T>> wheadFather;
    private boolean deleted=false;

    public boolean isDeleted() {
        return deleted;
    }

    public void delete(){
        this.deleted=true;
    }
    
    public enum FcLabels {

        contain, fatherId, priority
    };

    /**
     *
     * @param fatherId
     * @param contain
     * @param position
     * @param id
     */
    public FCNode(FCNode<T> father, T contain, FCPosition position, FCIdentifier id) {
        this.fatherID = new FCLabel(id, father == null ? null : father.getId());
        this.father = father;
        this.childrens = new ArrayList();
        //new TreeSet(new FCComparator());
        this.contain = new FCLabel(id, contain);
        this.priorite = new FCLabel(id, position);
        this.id = id;
    }

    /**
     *
     * @param path
     * @return
     */
    public FCNode<T> getNodeFromPath(List<Integer> path) {
        FCNode<T> ret = this;
        for (Integer i : path) {
            ret = ret.getChild(i);
        }
        return ret;
    }

    public FCNode<T> getHeadFather() {
        return wheadFather==null?null:wheadFather.get();
    }

    public void setHeadFather(FCNode<T> headFather) {
        this.wheadFather = headFather==null?null:new WeakReference(headFather);
    }

    public FCNode<T> getOldFather() {
        return oldFather;
    }

    public void setOldFather(FCNode<T> oldFather) {
        this.oldFather = oldFather;
    }

    public FCLabel getLabelOf(FcLabels fclabels) {
        switch (fclabels) {
            case contain:
                return contain;
            case fatherId:
                return fatherID;
            case priority:
                return priorite;
            default:
                return null;
        }
    }

    public void setLabelOf(FcLabels fclabels, FCLabel newlabel) {
        switch (fclabels) {
            case contain:
                contain = newlabel;
                break;
            case fatherId:
                fatherID = newlabel;
                break;
            case priority:
                priorite = newlabel;
                break;
        }
    }

    /**
     *
     * @return Identifier of node
     */
    public FCIdentifier getId() {
        return id;
    }

    @Override
    public int childrenNumber() {
        return childrens.size();
    }

    @Override
    public FCNode<T> getChild(int p) {
        if (p < 0 || p >= childrens.size()) {
            return null;
        }
        return childrens.get(p);
        /*
         if (p < 0 || p >= childrens.size()) {
         return null;
         }
         Iterator<FCNode<T>> it = childrens.iterator();
         while (p > 0) {
         it.next();
         p--;
         }
         return it.next();
         */
        //return p >= 0 && p < childrens.size() ? childrens.get(p) : null;
    }

    @Override
    public List getElements() {
        //return Arrays.asList(childrens.toArray());
        return childrens;
    }

    @Override
    public FCNode<T> createNode(T elem) {
        return new FCNode(null, elem, null, null);
    }

    @Override
    public void setReplicaNumber(int replicaNumber) {
    }

    @Override
    public T getValue() {
        return contain.getLabel();
    }

    /**
     *
     * @param node
     */
    public void addChildren(FCNode node) {
        int min = 0;
        int max = childrens.size() - 1;

        while (min <= max) {
            int i = (int) (min + max) / 2;
            int p = childrens.get(i).getPosition().compareTo(node.getPosition());
            if (p < 0) {
                min = i + 1;
            } else if (p > 0) {
                max = i - 1;
            } else {
                break;
            }

        }
        childrens.add(min, node);
        // this.childrens.add(node);
    }

    /**
     *
     * @param node
     * @return
     */
    /*public int nodePositionCompareTo(FCNode node) {
     return this.getPosition().compareTo(this.getId(), node.getId(), node.getPosition());
     }*/
    /**
     *
     * @param node
     */
    public void delChildren(FCNode node) {
        childrens.remove(node);
    }

    /**
     *
     * @return
     */
    public FCNode<T> getFather() {
        return father;
    }

    public void setFather(FCNode fcnode) {
        this.father = fcnode;
    }

    /**
     *
     * @return
     */
    public FCPosition getPosition() {
        return this.priorite.getLabel();
    }

    /**
     *
     * @return
     */
    public FCLabel<T> getLContain() {
        return contain;
    }

    /**
     *
     * @return
     */
    public FCLabel<FCIdentifier> getLFatherID() {
        return fatherID;
    }

    /**
     *
     * @return
     */
    public FCLabel<FCPosition> getLPriorite() {
        return priorite;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final FCNode<T> other = (FCNode<T>) obj;
        if (this.childrens != other.childrens && (this.childrens == null || !this.childrens.equals(other.childrens))) {
            return false;
        }
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        if (this.priorite != other.priorite && (this.priorite == null || !this.priorite.equals(other.priorite))) {
            return false;
        }
        if (this.contain != other.contain && (this.contain == null || !this.contain.equals(other.contain))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "(" + id + ", " + priorite + ", " + contain + "){" + childrens + '}';
    }

    @Override
    public Iterator<FCNode<T>> iterator() {
        return childrens.iterator();
    }

    public int size() {
        return childrens.size();
    }

    public String nodetail() {
        StringBuilder str = new StringBuilder();
        str.append(this.getValue());
        if (size() > 0) {
            str.append('{');
            for (FCNode node : childrens) {
                str.append(node.nodetail());
                str.append(',');
            }
            str.append('}');

        }
        return str.toString();
    }
}
