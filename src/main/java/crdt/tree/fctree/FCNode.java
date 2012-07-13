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
package crdt.tree.fctree;

import collect.OrderedNode;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @param <T> 
 * @author Stephane Martin <stephane.martin@loria.fr>
 */
public class FCNode<T> implements OrderedNode<T> {

    private FCLabel<FCNode<T>> father;
    private ArrayList<FCNode<T>> childrens;
    private FCIdentifier id;
    private FCLabel<FCPosition> priorite;
    private FCLabel<T> contain;

    /**
     * 
     * @param father
     * @param contain
     * @param position
     * @param id
     */
    public FCNode(FCNode<T> father, T contain, FCPosition position, FCIdentifier id) {
        this.father = new FCLabel(id, father);
        this.childrens = new ArrayList<FCNode<T>>();
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
            ret = ret.childrens.get(i);
        }
        return ret;
    }

    /**
     * 
     * @return
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
        return p>=0&&p<childrens.size()?childrens.get(p):null;
    }

    @Override
    public List getElements() {
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
    public void addChildren(FCNode node){
        int i=0;
        int min=0;
        int max=childrens.size()-1;
        
        while (min<=max){
            i=(int)(min+max)/2;
            int p=childrens.get(i).nodePositionCompareTo(node);
            if (p<0){
                min=i+1;
            }else if(p>0){
                max=i-1;
            }else{
                break;
            }
                
        }
        childrens.add(min, node);
    }
    /**
     * 
     * @param node
     * @return
     */
    public int nodePositionCompareTo(FCNode node){
        return this.getPosition().compareTo(this.getId(),node.getId(),node.getPosition());
    }
    /**
     * 
     * @param node
     */
    public void delChildren(FCNode node){
        childrens.remove(node);
    }

    /**
     * 
     * @return
     */
    public FCNode<T> getFather() {
        return father.getLabel();
    }
    /**
     * 
     * @return
     */
    public FCPosition getPosition(){
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
    public FCLabel<FCNode<T>> getLFather() {
        return father;
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
    public String toString() {
        return "(" + id + ", " + priorite + ", " + contain+ "){" +  childrens  + '}';
    }

    
    
    
}
