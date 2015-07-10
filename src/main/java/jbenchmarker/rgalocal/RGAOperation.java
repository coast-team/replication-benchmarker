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
package jbenchmarker.rgalocal;

import crdt.Operation;
import java.util.LinkedList;
import java.util.List;
import jbenchmarker.core.SequenceOperation;
import jbenchmarker.core.SequenceOperation.OpType;

/**
 *
 * @author Roh
 */
public class RGAOperation<T> implements Operation {

    private RGAS2Vector s4vpos;
    private RGAS2Vector s4vtms;
    private List<T> block;

    private OpType type;

    public RGAOperation() {
    }


    @Override
    public String toString() {
        String ret = new String();
        if (getType() == SequenceOperation.OpType.delete) {
            ret += "del(";
        } else {
            ret += "ins(\'" + block + "\',";
        }
        String s4va = s4vpos == null ? "null" : s4vpos.toString();
        String s4vb = s4vtms == null ? "null" : s4vtms.toString();
        ret += s4vpos + ") with " + s4vtms;

        return ret;
    }

    public RGAOperation(OpType type, RGAS2Vector s4vpos, RGAS2Vector s4vtms, List<T> block) {
        this.s4vpos = s4vpos;
        this.s4vtms = s4vtms;
        this.block = block;
        this.type = type;
    }
    
    /*
     * for block insert
     */
    public RGAOperation(RGAS2Vector s4vpos, List<T> block, RGAS2Vector s4vtms) {
        this.type = OpType.insert;
        this.s4vpos = s4vpos;
        this.s4vtms = s4vtms;
        this.block = block;
    }
    
    /*
     * for delete
     */
    public RGAOperation(RGAS2Vector s4vpos) {
        this.type = OpType.delete;
        this.s4vpos = s4vpos;
        this.s4vtms = null;
        this.block = null;
    }

    public RGAS2Vector getS4VPos() {
        return this.s4vpos;
    }

    public RGAS2Vector getS4VTms() {
        return this.s4vtms;
    }

    public List<T> getBlock() {
        return block;
    }

    public OpType getType() {
        return type;
    }

    @Override
    public Operation clone() {
        return new RGAOperation(type, 
                s4vpos == null ? s4vpos : s4vpos.clone(),
                s4vtms == null ? s4vtms : s4vtms.clone(),
                block == null ? block : new LinkedList(block)
        );
    }

    int getReplica() {
        return s4vtms.sid;
    }
}
