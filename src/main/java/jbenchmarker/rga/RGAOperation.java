/**
 * Replication Benchmarker
 * https://github.com/score-team/replication-benchmarker/ Copyright (C) 2013
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
package jbenchmarker.rga;

import crdt.Operation;
import jbenchmarker.core.SequenceOperation;
import jbenchmarker.core.SequenceOperation.OpType;

/**
 *
 * @author Roh
 */
public class RGAOperation<T> implements Operation {

    public static boolean LOCAL = true;
    public static boolean REMOTE = false;
    private RGAS4Vector s4vpos;
    private RGAS4Vector s4vtms;
    private T content;
    private boolean lor;  // to be local or remote
    private int intpos;
    private OpType type;

    public RGAOperation() {
        lor = LOCAL;
    }

    public void setLocal() {
        lor = LOCAL;
    }

    public void setRemote() {
        lor = REMOTE;
    }

    public boolean getLoR() {
        return lor;
    }

    @Override
    public String toString() {
        String ret = new String();
        if (getType() == SequenceOperation.OpType.delete) {
            ret += "del(";
        } else {
            ret += "ins(\'" + content + "\',";
        }
        String s4va = s4vpos == null ? "null" : s4vpos.toString();
        String s4vb = s4vtms == null ? "null" : s4vtms.toString();
        ret += intpos + "," + s4vpos + ") with " + s4vtms;

        return ret;
    }

    public RGAOperation(OpType type, int pos, RGAS4Vector s4vpos, T c, RGAS4Vector s4vtms) {
        this.type = type;
        this.s4vpos = s4vpos;
        this.s4vtms = s4vtms;
        this.intpos = pos;
        this.content = c;
        this.lor = LOCAL;
        this.type = type;
    }

    /*
     * for insert
     */
    public RGAOperation(int pos, RGAS4Vector s4vpos, T c, RGAS4Vector s4vtms) {
        this(OpType.insert, pos, s4vpos, c, s4vtms);
    }

    /*
     * for delete
     */
    public RGAOperation(int pos, RGAS4Vector s4vpos, RGAS4Vector s4vtms) {
        this(OpType.delete, pos, s4vpos, null, s4vtms);
    }

    public int getIntPos() {
        return this.intpos;
    }

    public RGAS4Vector getS4VPos() {
        return this.s4vpos;
    }

    public RGAS4Vector getS4VTms() {
        return this.s4vtms;
    }

    public T getContent() {
        return this.content;
    }

    public OpType getType() {
        return type;
    }

    @Override
    public Operation clone() {
        return new RGAOperation(type, intpos,
                s4vpos == null ? s4vpos : s4vpos.clone(), content,
                s4vtms == null ? s4vtms : s4vtms.clone());
    }

    int getReplica() {
        return s4vtms.sid;
    }
}
