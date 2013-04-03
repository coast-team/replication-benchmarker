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
package crdt.set.lastwriterwins;

import crdt.CRDTMessage;
import crdt.OperationBasedOneMessage;
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
    protected void applyOneInRemote(CommutativeSetMessage<T> op) {

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
    public OperationBasedOneMessage innerAdd(T t) {
        LwwMessage newOp = new LwwMessage(LwwMessage.OpType.add, t, 1);

        if (mapR.containsKey(t)) {
            newOp.settime(mapR.get(t) + 1);
            mapR.remove(t);
        }
        mapA.put(t, newOp.getime());

        return new  OperationBasedOneMessage(newOp);
    }

    @Override
    public OperationBasedOneMessage innerRemove(T t) {
        LwwMessage newOp = new LwwMessage(LwwMessage.OpType.del, t, mapA.get(t) + 1);

        mapA.remove(t);
        mapR.put(t, newOp.getime());

        return new  OperationBasedOneMessage(newOp);
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
