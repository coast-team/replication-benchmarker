/**
 * Replication Benchmarker
 * https://github.com/score-team/replication-benchmarker/ Copyright (C) 2012
 * LORIA / Inria / SCORE Team
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package jbenchmarker.core;

import collect.VectorClock;
import crdt.CRDT;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * local operation of document. T is a Character or a String.
 *
 * @author urso
 */
public class SequenceOperation<T> implements LocalOperation, Serializable {

    @Override
    public SequenceOperation clone() {
        throw new UnsupportedOperationException("Not implemented yet");
        /*  return new SequenceOperation(type, this.getReplica(), position, numberOf, 
         new ArrayList(content), new VectorClock(this.getVectorClock()));*/

    }

    @Override
    public LocalOperation adaptTo(CRDT replica) {

        int sizeDoc = ((MergeAlgorithm) replica).getDoc().viewLength();
//
        if (this.getType() == OpType.ins && this.position > sizeDoc) {
            this.position = sizeDoc;//a position exceeds document size
        } else if (this.getType() != OpType.ins) {
            if (this.position >= sizeDoc) {
                this.position = sizeDoc - 1;//a position exceeds document size
            }
            if ((this.position + this.lenghOfADel) > sizeDoc) {
                this.lenghOfADel = sizeDoc - this.position; //delete document at position exceeds document size
            }
        }
        return this;
    }

    public enum OpType {
        ins, del, replace, update, move, unsupported, noop
    };
    
    private OpType type;                  // type of operation : insert or delete
    private int position;                 // position in the document
    private int lenghOfADel;                   // length of a del
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

    public int getLenghOfADel() {
        return lenghOfADel;
    }

    public int getPosition() {
        return position;
    }

    public OpType getType() {
        return type;
    }

    public SequenceOperation(OpType type, int position, int offset, List<T> content) {
        //super(replica, VC);
        this.type = type;
        this.position = position;
        this.lenghOfADel = offset;
        this.content = content;
    }

    /*
     * Construction of an insert operation (character)
     */
    static public SequenceOperation<Character> insert(int position, String content) {
        List<Character> l = new ArrayList<Character>();
        for (int i = 0; i < content.length(); ++i) {
            l.add(content.charAt(i));
        }
        return new SequenceOperation(OpType.ins, position, 0, l);
    }

    /*
     * Construction of an delete operation
     */
    static public SequenceOperation delete(int position, int offset) {
        return new SequenceOperation(OpType.del, position, offset, null);
    }

    /*
     * Construction of an update operation
     */
    static public SequenceOperation<Character> update(int position, int offset, String content) {
        List<Character> l = new ArrayList<Character>();
        for (int i = 0; i < content.length(); ++i) {
            l.add(content.charAt(i));
        }
        return new SequenceOperation<Character>(OpType.replace, position, offset, l);
    }

    static public <T> SequenceOperation<T> update(int position, int offset, List<T> content) {
        return new SequenceOperation(OpType.replace, position, offset, content);
    }

    /**
     * Construction of a noop operation (usefull for pure merge)
     */
    public static SequenceOperation noop() {
        return new SequenceOperation(OpType.noop, -1, -1, null);
    }

    /*
     * Construction of a stylage operation
     */
    static public SequenceOperation<Character> unsupported() {
        return new SequenceOperation(OpType.unsupported, -1, -1, null);
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
        if (this.lenghOfADel != other.lenghOfADel) {
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
        hash = 89 * hash + this.lenghOfADel;
        hash = 89 * hash + (this.content != null ? this.content.hashCode() : 0);
        return 89 * hash + super.hashCode();
    }

    @Override
    public String toString() {
        return "SequenceOperation{" + "type=" + type + ", position=" + position + ", numberOf=" + lenghOfADel + ", content=" + content + '}';
    }
}
