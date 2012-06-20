/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.set.observedremove;

import crdt.CRDTMessage;
import crdt.set.CRDTSet;
import crdt.set.ConvergentSet;
import java.util.*;


/**
 *
 * @author score
 */
public class ConvergentOrSet<T> extends ConvergentSet<T> {

    private HashMap<T, HashSet<Tag>> mapA;
    private HashMap<T, HashSet<Tag>> mapR;
    private int numOp;
    HashSet<T> lookup;

    public ConvergentOrSet() {
        mapA = new HashMap<T, HashSet<Tag>>();
        mapR = new HashMap<T, HashSet<Tag>>();
        numOp = 0;
        lookup = new HashSet();
    }

    @Override
    public Set<T> lookup() {
        return lookup;
    }

     @Override
    public void applyOneRemote(CRDTMessage set) {

        ConvergentOrSet oSetC = ((ConvergentOrSet) set).clone();
        HashMap<T, HashSet<Tag>> statA = oSetC.getMapA();//for add
        HashMap<T, HashSet<Tag>> statR = oSetC.getMapR();//for remove
        boolean before,after;
        
         for (T t : statA.keySet()) {
             before = lookup.contains(t);

             if (mapA.get(t) == null) { //receive first add
                 mapA.put(t, statA.get(t));
             } else {
                 mapA.get(t).addAll(statA.get(t));
             }

             if (statR.containsKey(t)) {
                 if (mapR.get(t) == null) { //receive first remove
                     mapR.put(t, statR.get(t));
                 } else {//remove elements with several tags
                     mapR.get(t).addAll(statR.get(t));
                 }
             }
             if (!mapR.keySet().contains(t)
                     || (mapR.keySet().contains(t) && mapA.get(t).size() > mapR.get(t).size())) {
                 lookup.add(t);
             }

             if (mapR.keySet().contains(t) && mapR.get(t).size() == mapA.get(t).size()) {
                 lookup.remove(t);
             }
             after = lookup.contains(t);
             if (!before && after) {
                 notifyAdd(t);
             }
             if (before && !after) {
                 notifyDel(t);
             }
         }
    }

    HashMap getMapA() {
        return mapA;
    }
    HashMap getMapR() {
        return mapR;
    }

    void setMapA(HashMap<T, HashSet<Tag>> t) {
        mapA.clear();
        mapA = t;
    }
    
    void setMapR(HashMap<T, HashSet<Tag>> t) {
        mapR.clear();
        mapR = t;
    }

    @Override
    public ConvergentOrSet<T> innerAdd(T t) {
        final Tag tag = new Tag(getReplicaNumber(), ++numOp); // creat new tag
        
        if (!mapA.containsKey(t)) {
            mapA.put(t, new HashSet<Tag>());
        }
        mapA.get(t).add(tag);
        lookup.add(t);
        return this;
    }

    @Override
    public ConvergentOrSet<T> innerRemove(T t) {
        if (!mapR.containsKey(t)) {
            mapR.put(t, new HashSet<Tag>());
        }
        mapR.get(t).addAll(mapA.get(t));
        lookup.remove(t);
        return this;
    }

    @Override
    public boolean contains(T t) {
        return(this.lookup().contains(t));
    }

    int getNumOp() {
        return numOp;
    }

    void setNumOp(int o) {
        this.numOp = o;
    }
    
    @Override
    public CRDTSet<T> create() {
        return new ConvergentOrSet();
    }
   
    @Override
    public ConvergentOrSet<T> clone() {
        ConvergentOrSet<T> clone = new ConvergentOrSet<T>();

        clone.mapA = (HashMap<T, HashSet<Tag>>) this.mapA.clone();
        clone.mapR = (HashMap<T, HashSet<Tag>>) this.mapR.clone();

        clone.numOp = this.numOp;
        clone.lookup = (HashSet<T>) this.lookup.clone();
        return clone;
    }
}
