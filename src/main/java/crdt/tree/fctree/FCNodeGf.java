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
package crdt.tree.fctree;

import collect.OrderedNode;
import collect.SimpleNode;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @param <T>
 * @author Stephane Martin <stephane.martin@loria.fr>
 */
public class FCNodeGf<T> extends FCNode<T> implements Serializable {

    // private ArrayList<FCNode<T>> childrens;
    
    
    private FCLabel<FCPosition> priorite;
    private FCLabel<T> contain;
    private FCLabel<FCIdentifier> fatherID;
    
    private FCNodeGf<T> oldFather;
    //private FCNodeGf<T> headFather;
    private WeakReference<FCNodeGf<T>> wheadFather;
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
    
    @Override
    public FCNodeGf createNode(FCNode<T> father, T contain, FCPosition position, FCIdentifier id){
        return new FCNodeGf(father,contain,position,id);
    }

    /**
     *
     * @param fatherId
     * @param contain
     * @param position
     * @param id
     */
    public FCNodeGf(FCNode<T> father, T contain, FCPosition position, FCIdentifier id) {
        super(father, id);
        this.fatherID = new FCLabel(id, father == null ? null : father.getId());
        //new TreeSet(new FCComparator());
        this.contain = new FCLabel(id, contain);
        this.priorite = new FCLabel(id, position);
        
    }

    
    

    public FCNodeGf<T> getHeadFather() {
        return wheadFather==null?null:wheadFather.get();
    }

    public void setHeadFather(FCNodeGf<T> headFather) {
        this.wheadFather = headFather==null?null:new WeakReference(headFather);
    }

    public FCNodeGf<T> getOldFather() {
        return oldFather;
    }

    public void setOldFather(FCNodeGf<T> oldFather) {
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

   

   

    

    

    @Override
    public FCNodeGf<T> createNode(T elem) {
        return new FCNodeGf(null, elem, null, null);
    }

   

    @Override
    public T getValue() {
        return contain.getLabel();
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
         if(!super.equals(obj)){
            return false;
        }
        final FCNodeGf<T> other = (FCNodeGf<T>) obj;
       
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

    
    public void setFather(FCNode<T> father) {
        this.father = father;
    }

   
}
