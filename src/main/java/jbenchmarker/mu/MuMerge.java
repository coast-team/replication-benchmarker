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
package jbenchmarker.mu;

import crdt.CRDT;
import crdt.simulator.IncorrectTraceException;
import java.util.LinkedList;
import java.util.List;
import jbenchmarker.core.Document;
import jbenchmarker.core.MergeAlgorithm;
import jbenchmarker.core.SequenceMessage;
import jbenchmarker.core.SequenceOperation;
import jbenchmarker.core.SequenceOperation.OpType;
import jbenchmarker.logoot.*;

/**
 *
 * @author mehdi urso
 */
public class MuMerge<T> extends MergeAlgorithm {

    // nbBit <= 64
    public MuMerge(Document doc, int r) {
        super(doc, r);

    }

    @Override
    public MuDocument<T> getDoc() {
        return (MuDocument<T>) super.getDoc();
    }
   
    @Override
    protected void integrateRemote(SequenceMessage op) {
        getDoc().apply(op);
    }

    @Override
    protected List<SequenceMessage> localDelete(SequenceOperation opt) throws IncorrectTraceException {
        return getDoc().delete(opt.getPosition(), opt.getLenghOfADel(), opt);
    }

    @Override
    protected List<SequenceMessage> localInsert(SequenceOperation opt) throws IncorrectTraceException {
        return getDoc().insert(opt.getPosition(), opt.getContent(), opt);
    }
    
    @Override
    protected List<SequenceMessage> localUpdate(SequenceOperation opt) throws IncorrectTraceException {
        return getDoc().update(opt.getPosition(), opt.getContent(), opt);
    }
    
    @Override
    public CRDT<String> create() {
        return new MuMerge(getDoc().create(), 0);
    }

    @Override
    public void setReplicaNumber(int replicaNumber) {
        super.setReplicaNumber(replicaNumber);
        getDoc().setReplicaNumber(replicaNumber);
    }
}
