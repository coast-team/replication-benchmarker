/**
 *   This file is part of ReplicationBenchmark.
 *
 *   ReplicationBenchmark is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   ReplicationBenchmark is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with ReplicationBenchmark.  If not, see <http://www.gnu.org/licenses/>.
 *
 **/

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jbenchmarker.trace;

import jbenchmarker.core.VectorClock;

/**
 *
 * @author urso
 */
public class TraceOperation {


    public enum OpType { ins, del }; 
    
    final private int replica;                  // replica number
    final private OpType type;                  // type of operation : insert or delete
    final private int position;                 // position in the document
    final private int offset;                   // length of a del
    final private String content;          // content of an ins
    final private VectorClock VC;               // Vector clock 

    
    public VectorClock getVC() {
        return VC;
    }

    public String getContent() {
        return content;
    }

    public int getOffset() {
        return offset;
    }

    public int getPosition() {
        return position;
    }


    public int getReplica() {
        return replica;
    }


    public OpType getType() {
        return type;
    }

    public TraceOperation(OpType type, int replica, int position, int offset, String content, VectorClock VC) {
        this.type = type;
        this.replica = replica;
        this.position = position;
        this.offset = offset;
        this.content = content;
        this.VC = VC;
    }

    /*
     * Construction of an insert operation 
     */
    static public TraceOperation insert(int replica, int position, String content, VectorClock VC) {
        return new TraceOperation(OpType.ins, replica, position, 0, content, VC);
    }
    
    /*
     * Construction of an insert operation 
     */
    static public TraceOperation delete(int replica, int position, int offset, VectorClock VC) {
        return new TraceOperation(OpType.del, replica, position, offset, null, VC);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TraceOperation other = (TraceOperation) obj;
        if (this.replica != other.replica) {
            return false;
        }
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
        if (this.VC != other.VC && (this.VC == null || !this.VC.equals(other.VC))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + this.replica;
        hash = 53 * hash + (this.type != null ? this.type.hashCode() : 0);
        hash = 53 * hash + this.position;
        hash = 53 * hash + this.offset;
        hash = 53 * hash + (this.content != null ? this.content.hashCode() : 0);
        hash = 53 * hash + (this.VC != null ? this.VC.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "TraceOperation{" + "replica=" + replica + ", VC=" + VC + ", type=" + type + ", position=" + position + (type==OpType.del ? ", offset=" + offset : ", content=" + content) + '}';
    }    
    
    public int getRange() {
        return (type == OpType.ins) ? content.length() : offset;  
    }
}
