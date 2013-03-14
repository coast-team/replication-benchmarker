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
package crdt;

import crdt.simulator.TraceOperation;
import java.io.Serializable;
import java.util.LinkedList;

/**
 *
 *  
 * @author urso, Stephane Martin
 * 
 * A message that contains several other one messages.
 * @see OperationBasedOneMessage
 */
public final class OperationBasedMessagesBag implements OperationBasedMessage,Cloneable, Serializable {
    private LinkedList<OperationBasedOneMessage> ops = new LinkedList<OperationBasedOneMessage>();

    /*TraceOperation traceOperation;*/
    OperationBasedMessagesBag(OperationBasedMessage aThis, OperationBasedMessage msg) {
        addMessage(aThis);
        addMessage(msg);
    }

    public OperationBasedMessagesBag() {
    }
    
    void addMessage(OperationBasedMessage mess){
        if (mess==null)
            return;
        if (mess instanceof OperationBasedMessagesBag){
            ops.addAll(((OperationBasedMessagesBag)mess).getOps());
        }else{
            ops.add((OperationBasedOneMessage )mess);
        }
            
    }

    /**
     * return all messages operations
     * @return
     */
    public LinkedList<OperationBasedOneMessage> getOps() {
        return ops;
    }
    
    /**
     * Concatenation of message.
     * @param msg
     * @return
     */
    @Override
    public CRDTMessage concat(CRDTMessage msg) {
        OperationBasedMessagesBag ret=new OperationBasedMessagesBag();
        ret.addMessage(this);
        ret.addMessage((OperationBasedMessage)msg);
        return ret;
    } 
    
    /**
     * 
     * @return cloned operationBased message
     */
    @Override
    public OperationBasedMessagesBag clone() {
        OperationBasedMessagesBag clone = new OperationBasedMessagesBag();
        for (OperationBasedOneMessage o : ops) {
            clone.ops.add((OperationBasedOneMessage)o.clone());
        }
        return clone;
    }

    /**
     * 
     * @return a representation of message
     */
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("BAG:");
        for (OperationBasedOneMessage m : ops) {
            s.append(" + ").append(m.toString());
        }
        return s.toString();
    }

    //abstract protected String toString();
    
    //abstract protected OperationBasedOneMessage clone();

    /**
     * 
     * @return number of messages.
     */
    @Override
    public int size() {
        return ops.size();
    }

   

    @Override
   public void execute(CRDT cmrdt){
        for (OperationBasedOneMessage o : ops) {
            cmrdt.applyOneRemote(o);
        }
    }

   /* @Override
    public void setTraceOperation(TraceOperation traceOperation) {
        this.traceOperation=traceOperation;
    }

    @Override
    public TraceOperation getTraceOperation() {
        return traceOperation;
    }*/

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final OperationBasedMessagesBag other = (OperationBasedMessagesBag) obj;
        if (this.ops != other.ops && (this.ops == null || !this.ops.equals(other.ops))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + (this.ops != null ? this.ops.hashCode() : 0);
        return hash;
    }   
}
