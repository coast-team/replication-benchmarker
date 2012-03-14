/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.set.lastwriterwins;

import crdt.CRDTMessage;
import crdt.PreconditionException;
import crdt.set.CRDTSet;
import crdt.set.ConvergentSet;
import java.util.*;

/**
 *
 * @author score
 */
public class ConvergentLwwSet<T> extends ConvergentSet<T>  {

    private HashMap<T, Integer> mapA; //add element
    private HashMap<T, Integer> mapR; //for removing elements
    HashSet<T> lookup;

    public ConvergentLwwSet() {
        this.mapA = new HashMap<T, Integer>();
        this.mapR = new HashMap<T, Integer>();
        lookup = new HashSet();
    }

    @Override
    public Set lookup() {    
        return lookup;
    }

     @Override
    public void applyRemote(CRDTMessage stat) {
        ConvergentLwwSet lSC = (ConvergentLwwSet) stat;

        HashMap<T, Integer> statA = lSC.getMapA();
        HashMap<T, Integer> statR = lSC.getMapR();
        boolean before,after;
        
         for (T t : statA.keySet()) {
             before = lookup.contains(t);

             if (mapA.get(t) == null
                     || (mapA.get(t) != null && statA.get(t) > mapA.get(t))) {
                 mapA.put(t, statA.get(t));
             }

             if (statR.containsKey(t)
                     && (mapR.get(t) == null || statR.get(t) > mapR.get(t))) {
                 mapR.put(t, statR.get(t));
             }

             //element does not exist in mapR or existe in mapR but counter in mapA is the biggest
             if (!mapR.keySet().contains(t)
                     || (mapR.keySet().contains(t) && mapA.get(t) > mapR.get(t))) {
                 lookup.add(t);
             }
             //element exist in mapR and his counter in mapR is greater thant in mapA 
             if (mapR.keySet().contains(t) && mapR.get(t) >= mapA.get(t)) {
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

    public HashMap getMapA() {
        return mapA;
    }

    public void setMapA(HashMap<T, Integer> t) {
        mapA.clear();
        mapA = t;
    }

    public HashMap getMapR() {
        return mapR;
    }

    public void setMapR(HashMap<T, Integer> t) {
        mapR.clear();
        mapR = t;
    }

    @Override
    public ConvergentLwwSet<T> innerAdd(T t) {
        int time=0;
       
        if (mapA.containsKey(t)) {
            time= mapA.get(t);
        }
        mapA.put(t,time + 1);
        lookup.add(t);

        return this;

    }

    @Override
    public ConvergentLwwSet<T> innerRemove(T t) throws PreconditionException {
        int time=0;

        if (mapR.containsKey(t)) {
            time= mapR.get(t);
        }
        mapR.put(t,time + 1);
        //LwwOperation newOp = new LwwOperation(SetOperation.OpType.del, t, 0);
        lookup.remove(t);
        return this;
    }

    @Override
    public boolean contains(T t) {
        return(this.lookup().contains(t));
    }

    @Override
    public CRDTSet<T> create() {
        return new ConvergentLwwSet();
    }  
    
    @Override
    public ConvergentLwwSet<T> clone() {
        ConvergentLwwSet<T> clone = new ConvergentLwwSet<T>();
        clone.mapA = (HashMap<T, Integer>) this.mapA.clone();
        clone.mapR = (HashMap<T, Integer>) this.mapR.clone();
        clone.lookup = (HashSet<T>) this.lookup.clone();
        return clone;
    }
}