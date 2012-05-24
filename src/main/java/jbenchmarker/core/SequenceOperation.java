/**
 * Replication Benchmarker
 * https://github.com/score-team/replication-benchmarker/
 * Copyright (C) 2011 INRIA / LORIA / SCORE Team
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
package jbenchmarker.core;

import collect.VectorClock;
import crdt.CRDT;
import crdt.Operation;
import crdt.simulator.TraceOperation;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author urso
 */
public class SequenceOperation<T> extends TraceOperation implements crdt.Operation, Serializable {

    @Override
    public Operation getOperation(CRDT replica) {
        
        int sizeDoc = ((MergeAlgorithm) replica).getDoc().viewLength();
//
        if (this.getType() == OpType.ins && this.position > sizeDoc) {
            this.position = sizeDoc;//a position exceeds document size
        } else if (this.getType() == OpType.del) {
            if (this.position >= sizeDoc) {
               this.position = sizeDoc - 1;//a position exceeds document size
            }
            if ((this.position + this.offset) > sizeDoc) {
                this.offset = sizeDoc - this.position; //delete document at position exceeds document size
            }
        }
        return this;
    }


    public enum OpType {ins, del,up}; 
    
    private OpType type;                  // type of operation : insert or delete
    private int position;                 // position in the document
    private int offset;                   // length of a del
    private List<T> content;          // content of an ins
    
    public List<T> getContent() {
        return content;
    }
    
    public String getContentAsString() {
        StringBuilder s = new StringBuilder();
        for (T t : content) {
            s.append(t.toString());
        }
        return s.toString();
    }

    public int getOffset() {
        return offset;
    }

    public int getPosition() {
        return position;
    }

    public OpType getType() {
        return type;
    }

    SequenceOperation(OpType type, int replica, int position, int offset, List<T> content, VectorClock VC) {
        super(replica, VC);
        this.type = type;
        this.position = position;
        this.offset = offset;
        this.content = content;
    }

    /*
     * Construction of an insert operation (character)
     */
    static public SequenceOperation<Character> insert(int replica, int position, String content, VectorClock VC) {
        List<Character> l = new ArrayList<Character>();
        for (int i = 0; i < content.length(); ++i) {
            l.add(content.charAt(i));
        }        
        return new SequenceOperation(OpType.ins, replica, position, 0, l, VC);
    }
    
    /*
     * Construction of an delete operation
     */
    static public SequenceOperation delete(int replica, int position, int offset, VectorClock VC) {
        return new SequenceOperation(OpType.del, replica, position, offset, null, VC);
    }
    
    /*
     * Construction of an update operation
     */
     static public SequenceOperation<Character> update(int replica, int position, int offset, String content, VectorClock VC){
         List<Character> l = new ArrayList<Character>();
         for (int i = 0; i < content.length(); ++i) {
            l.add(content.charAt(i));
         }        
         return new SequenceOperation(OpType.up, replica, position, offset,l, VC);
     }    
     
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof SequenceOperation)) {
            return false;
        }
        final SequenceOperation other = (SequenceOperation) obj;
        if (this.type != other.type) {
            return false;
        }
        if (this.position != other.position) {
            return false;
        }
        if (this.offset != other.offset) {
            return false;
        }
        if ((this.content == null) ? (other.content != null) : !this.content.equals(other.content)) {
            return false;
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + (this.type != null ? this.type.hashCode() : 0);
        hash = 89 * hash + this.position;
        hash = 89 * hash + this.offset;
        hash = 89 * hash + (this.content != null ? this.content.hashCode() : 0);
        return 89 * hash + super.hashCode();
    }



    @Override
    public String toString() {
        return "SequenceOperation{" + "replica=" + getReplica() + ", VC=" + getVectorClock() + ", type=" + type + ", position=" + position + (type==OpType.del ? ", offset=" + offset : ", content=" + content) + '}';
    }    
    
    public int getRange() {
        return (type == OpType.ins) ? content.size() : offset;  
    }
}
