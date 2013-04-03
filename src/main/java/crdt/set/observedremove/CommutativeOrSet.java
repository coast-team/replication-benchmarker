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
package crdt.set.observedremove;

import crdt.OperationBasedOneMessage;
import crdt.PreconditionException;
import crdt.set.CRDTSet;
import crdt.set.CommutativeSet;
import crdt.set.CommutativeSetMessage;
import java.util.*;

/**
 *
 * @author score
 */
public class CommutativeOrSet<T> extends CommutativeSet<T> {

    private HashMap<T, HashSet<Tag>> mapA;
    private int numOp;

    public CommutativeOrSet() {
        this.mapA = new HashMap<T, HashSet<Tag>>();
        numOp = 0;
    }

    @Override
    public Set<T> lookup() {
        return mapA.keySet();
    }

    @Override
    protected void applyOneInRemote(CommutativeSetMessage<T> op) {

        OrMessage<T> orOp = (OrMessage<T>) op;

        T t = orOp.getContent(); // element received
        Set<Tag> setop = orOp.getTags(); // collection of tags received
        HashSet<Tag>    tags = mapA.get(t);
        if (orOp.getType() == OrMessage.OpType.add) {
            if (tags == null) {
                tags = new HashSet<Tag>();
                mapA.put(t, tags);
            }
            tags.addAll(setop);
        } else //remove
        {
            if (mapA.get(t) != null) {
                tags.removeAll(setop);
                if (tags.isEmpty()) {
                    mapA.remove(t);
                }
            }
        }
    }

    HashMap getMapA() {
        return mapA;
    }

    void setMapA(HashMap<T, HashSet<Tag>> t) {
        mapA.clear();
        mapA = t;
    }

    @Override
    public OperationBasedOneMessage innerAdd(T t) {
         numOp++;//increment numOp
        final Tag tag = new Tag(getReplicaNumber(), numOp); // create new tag

        OrMessage newOp = new OrMessage(OrMessage.OpType.add, t, new HashSet<Tag>(){{add(tag);}});// create op
        mapA.put(t, new HashSet<Tag>(){{add(tag);}});
        return new  OperationBasedOneMessage(newOp);
    }

    @Override
    public OperationBasedOneMessage  innerRemove(T t) throws PreconditionException {
        OrMessage newOp = new OrMessage(OrMessage.OpType.del, t, (Set<Tag>) mapA.get(t).clone());
        mapA.remove(t);
        return new  OperationBasedOneMessage(newOp);
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
        return new CommutativeOrSet();
    }

  
}
