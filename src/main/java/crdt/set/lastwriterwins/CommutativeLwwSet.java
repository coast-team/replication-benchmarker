/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.set.lastwriterwins;

import crdt.set.CRDTSet;
import crdt.set.CommutativeSet;
import crdt.set.CommutativeSetMessage;
import java.util.*;

/**
 *
 * @author score
 */
public class CommutativeLwwSet<T> extends CommutativeSet<T>{
    
    private HashMap<T, Integer> mapA; //add element
    private HashMap<T, Integer> mapR; //for removing elements

    public CommutativeLwwSet() {
        this.mapA = new HashMap<T, Integer>();
        this.mapR = new HashMap<T, Integer>();
    }

    @Override
    public Set<T> lookup() {

        return mapA.keySet();
    }

    @Override
    protected void applyOneRemote(CommutativeSetMessage<T> op) {

        LwwMessage lw = (LwwMessage) op;
        int timeReceiv = lw.getime();
        T elemReceiv = (T) lw.getContent();

        if (lw.getType() == LwwMessage.OpType.add) {
            if ((!mapA.containsKey(elemReceiv) || timeReceiv >= mapA.get(elemReceiv))
                    && (!mapR.containsKey(elemReceiv) || timeReceiv >= mapR.get(elemReceiv))) {
                mapA.put(elemReceiv, timeReceiv);
                mapR.remove(elemReceiv);
            }

        } else { //del reçu
            if ((!mapA.containsKey(elemReceiv) || timeReceiv >= mapA.get(elemReceiv))
                    && (!mapR.containsKey(elemReceiv) || timeReceiv >= mapR.get(elemReceiv))) {
                mapR.put(elemReceiv, timeReceiv);
                mapA.remove(elemReceiv);
            }
        }
    }

    public HashMap getmapA() {
        return mapA;
    }

    public void setmapA(HashMap<T, Integer> t) {
        mapA.clear();
        mapA = t;
    }

    public HashMap getmapR() {
        return mapR;
    }

    public void setmapR(HashMap<T, Integer> t) {
        mapR.clear();
        mapR = t;
    }

    @Override
    public LwwMessage innerAdd(T t) {
        LwwMessage newOp = new LwwMessage(LwwMessage.OpType.add, t, 1);

        if (mapR.containsKey(t)) {
            newOp.settime(mapR.get(t) + 1);
            mapR.remove(t);
        }
        mapA.put(t, newOp.getime());

        return  newOp;

    }

    @Override
    public LwwMessage innerRemove(T t) {
        LwwMessage newOp = new LwwMessage(LwwMessage.OpType.del, t, mapA.get(t) + 1);

        mapA.remove(t);
        mapR.put(t, newOp.getime());

        return newOp;
    }

     @Override
    public boolean contains(T t) {
        return(this.lookup().contains(t));
    }

    @Override
    public CRDTSet<T> create() {
        return new CommutativeLwwSet();
    }  
}