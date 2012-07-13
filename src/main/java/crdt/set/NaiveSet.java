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
package crdt.set;

import crdt.OperationBasedOneMessage;
import crdt.PreconditionException;
import crdt.set.CommutativeSetMessage.OpType;
import crdt.set.lastwriterwins.TypedMessage;
import java.util.HashSet;
import java.util.Set;
import jbenchmarker.ot.otset.OTSetOperations;

/**
 *
 * @author urso
 */
public class NaiveSet<T> extends CommutativeSet<T> {
    HashSet<T> set = new HashSet<T>();

    @Override
    protected void applyOneInRemote(CommutativeSetMessage<T> op) {
        if (op.getType() == OpType.add) {
            set.add(op.content);
        } else {
            set.remove(op.content);
        }
    }

    @Override
    public OperationBasedOneMessage innerAdd(T t) throws PreconditionException {
        set.add(t);
        return new OperationBasedOneMessage(new TypedMessage<T>(OpType.add, t));
    }

    @Override
    public OperationBasedOneMessage innerRemove(T t) throws PreconditionException {
        set.remove(t);
        return new OperationBasedOneMessage(new TypedMessage<T>(OpType.del, t));
    }

    @Override
    public CRDTSet<T> create() {
        return new NaiveSet<T>();
    }

    @Override
    public boolean contains(T t) {
        return set.contains(t);
    }

    @Override
    public Set<T> lookup() {
        return set;
    }

   

}
