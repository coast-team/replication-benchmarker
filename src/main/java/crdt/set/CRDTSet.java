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

import crdt.CRDT;
import crdt.CRDTMessage;
import java.util.*;
import crdt.PreconditionException;
import jbenchmarker.core.LocalOperation;

/**
 *
 * @author score
 */
public abstract class CRDTSet<T> extends CRDT<Set<T>>  {

    public CRDTSet() {
    }

    public CRDTSet(int replicaNumber) {
        super(replicaNumber);
    }
    
    @Override
    public abstract CRDTSet<T> create();
    
    abstract protected CRDTMessage innerAdd(T t) throws PreconditionException;
    
    abstract protected CRDTMessage innerRemove(T t) throws PreconditionException;
        
    abstract public boolean contains (T t);
    
    @Override
    final public CRDTMessage applyLocal(LocalOperation op) throws PreconditionException {
        
        SetOperation<T> s = (SetOperation<T>) op;
        CRDTMessage msg;
        if (s.getType() == SetOperation.OpType.add) {
            msg = add(s.getContent());
        }else{
            msg = remove(s.getContent()); 
        }
        return msg;
    }

    /**
     * Adds an element to the set
     * @param t the element
     * @return a message
     * @throws PreconditionException if elment is already in lookup (or other crdt specific condition)
     */
    final public CRDTMessage add(T t) throws PreconditionException {
        if (lookup().contains(t)) {
            throw new PreconditionException("Add : the element " + t + " already exists in the set");
        }
        CRDTMessage msg;
        msg = innerAdd(t);
        notifyAdd(t);
        return msg;        
    }
    
    /**
     * Removes an element to the set
     * @param t the element
     * @return a message
     * @throws PreconditionException if elment is already in lookup (or other crdt specific condition)
     */
    final public CRDTMessage remove(T t) throws PreconditionException {
        if (!lookup().contains(t)) {
            throw new PreconditionException("Remove : the element " + t + " does not exist in the set");
        }
        CRDTMessage msg;
        msg = innerRemove(t);
        notifyDel(t);
        return msg; 
    }

    /**
     * Notifies the observers that an element is added.
     * Should be called when lookup is changed by applyRemote.
     * @param t the element added
     */
    protected void notifyAdd(T t) {
        this.setChanged();
        this.notifyObservers(new SetOperation<T>(SetOperation.OpType.add, t));
    }
    
    /**
     * Notifies the observers that an element is remove.
     * Should be called when lookup is changed by applyRemote.
     * @param t the element removed
     */
    protected void notifyDel(T t) {      
        this.setChanged();
        this.notifyObservers(new SetOperation<T>(SetOperation.OpType.del, t));
    }
    public String view(){
        return this.lookup().toString();
    }
}
