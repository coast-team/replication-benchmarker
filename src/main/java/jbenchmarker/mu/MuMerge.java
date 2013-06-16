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
import crdt.Operation;
import crdt.simulator.IncorrectTraceException;
import java.util.List;
import jbenchmarker.core.Document;
import jbenchmarker.core.MergeAlgorithm;
import crdt.Operation;
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
    protected void integrateRemote(crdt.Operation message) {
        getDoc().apply(message);
    }

    @Override
    protected List<Operation> localDelete(SequenceOperation opt) throws IncorrectTraceException {
        return getDoc().delete(opt.getPosition(), opt.getLenghOfADel(), opt);
    }

    @Override
    protected List<Operation> localInsert(SequenceOperation opt) throws IncorrectTraceException {
        return getDoc().insert(opt.getPosition(), opt.getContent(), opt);
    }
    
    @Override
    protected List<Operation> localUpdate(SequenceOperation opt) throws IncorrectTraceException {
        return getDoc().update(opt.getPosition(), opt.getContent(), opt);
    }
    
    @Override
    protected List<? extends Operation> localMove(SequenceOperation opt) throws IncorrectTraceException {
        return handleMoves ? getDoc().move(opt.getPosition(), opt.getDestination(), opt.getContent(), opt) : super.localMove(opt);
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
