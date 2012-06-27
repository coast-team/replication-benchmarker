/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.tree.fctree;

import collect.OrderedNode;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Stephane Martin <stephane.martin@loria.fr>
 */
public class FCNode<T> implements OrderedNode<T> {

    private FCLabel<FCNode<T>> father;
    private ArrayList<FCNode<T>> childrens;
    private FCIdentifier id;
    private FCLabel<FCPosition> priorite;
    private FCLabel<T> contain;

    public FCNode(FCNode<T> father, T contain, FCPosition position, FCIdentifier id) {
        this.father = new FCLabel(id, father);
        this.childrens = new ArrayList<FCNode<T>>();
        this.contain = new FCLabel(id, contain);
        this.priorite = new FCLabel(id, position);
        this.id = id;
    }

    public FCNode<T> getNodeFromPath(List<Integer> path) {
        FCNode<T> ret = this;
        for (Integer i : path) {
            ret = ret.childrens.get(i);
        }
        return ret;
    }

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
    public void addChildren(FCNode node){
        int i=0;
        for (FCNode nodec:childrens){
            if (node.getPosition().compareTo(node.getId(),nodec.getId(),nodec.getPosition())<0){
                break;
            }
            i++;
        }
        childrens.add(i, node);
    }
    public void delChildren(FCNode node){
        childrens.remove(node);
    }

    public FCNode<T> getFather() {
        return father.getLabel();
    }
    public FCPosition getPosition(){
        return this.priorite.getLabel();
    }

    public FCLabel<T> getLContain() {
        return contain;
    }

    public FCLabel<FCNode<T>> getLFather() {
        return father;
    }

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
