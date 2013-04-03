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
package crdt.set;

import crdt.CRDTMessage;
import crdt.OperationBasedOneMessage;
import crdt.PreconditionException;
import crdt.RemoteOperation;

/**
 *
 * @author urso
 */
public abstract class CommutativeSet<T> extends CRDTSet<T>  {        
    //@Override
    /*final public void applyRemote(CRDTMessage msg) {
        CommutativeSetMessage<T> op = (CommutativeSetMessage<T>) msg;
        applyOneRemoteNotify(op);
        for (OperationBasedOneMessage<T> m : op.getMsgs()) {
            applyOneRemoteNotify((CommutativeSetMessage<T>) m);
        }
    }*/
    
    
    abstract protected void applyOneInRemote(CommutativeSetMessage<T> op);
    
    @Override
    abstract public OperationBasedOneMessage innerAdd(T t) throws PreconditionException;
    
    @Override
    abstract public OperationBasedOneMessage innerRemove(T t) throws PreconditionException;

    @Override
    public void applyOneRemote(CRDTMessage opm) {
        CommutativeSetMessage<T> op =(CommutativeSetMessage)((OperationBasedOneMessage)opm).getOperation();
        T t = op.getContent();
        boolean before = lookup().contains(t);
        applyOneInRemote(op);
        boolean after = lookup().contains(t);
        if (before && !after)
            notifyDel(t);
        else if (!before && after)
            notifyAdd(t);    
    }
}
