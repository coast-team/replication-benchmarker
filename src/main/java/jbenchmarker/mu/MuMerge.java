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
import java.util.List;
import jbenchmarker.core.Document;
import jbenchmarker.core.MergeAlgorithm;
import jbenchmarker.core.SequenceMessage;
import jbenchmarker.core.SequenceOperation;

/**
 *
 * @author mehdi urso
 */
public class MuMerge<T> extends MergeAlgorithm {
    private final boolean handleMoves;

    // nbBit <= 64
    public MuMerge(Document doc, int r, boolean handleMoves) {
        super(doc, r);
        this.handleMoves = handleMoves;
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
    protected List<SequenceMessage> localMove(SequenceOperation opt) throws IncorrectTraceException {
        return handleMoves ? super.localMove(opt) : getDoc().move(opt.getPosition(), opt.getDestination(), opt.getContent(), opt);
    }
    
    @Override
    public CRDT<String> create() {
        return new MuMerge(getDoc().create(), 0, handleMoves);
    }

    @Override
    public void setReplicaNumber(int replicaNumber) {
        super.setReplicaNumber(replicaNumber);
        getDoc().setReplicaNumber(replicaNumber);
    }
}
