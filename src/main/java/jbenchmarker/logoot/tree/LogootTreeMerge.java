/**
 * Replication Benchmarker
 * https://github.com/score-team/replication-benchmarker/ Copyright (C) 2016
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
package jbenchmarker.logoot.tree;

import crdt.CRDT;
import crdt.simulator.IncorrectTraceException;
import jbenchmarker.core.MergeAlgorithm;
import crdt.Operation;
import jbenchmarker.core.SequenceOperation;
import jbenchmarker.core.Document;

import java.util.*;
import jbenchmarker.logoot.ListIdentifier;
import jbenchmarker.logoot.LogootOperation;

/**
 *
 * @author mehdi urso
 */
public class LogootTreeMerge<T> extends MergeAlgorithm {

    public LogootTreeMerge(Document doc, int r) {
        super(doc, r);
    }

    @Override
    public LogootTreeDocument<T> getDoc() {
        return (LogootTreeDocument<T>) super.getDoc();
    }

    @Override
    protected void integrateRemote(Operation op) {
        getDoc().apply(op);
    }

    @Override
    protected List<Operation> localDelete(SequenceOperation opt) throws IncorrectTraceException {
        List<Operation> lop = new ArrayList<Operation>();
        int offset = opt.getLenghOfADel(), position = opt.getPosition();

        for (int k = 1; k <= offset; k++) {
            LogootOperation<T> wop = LogootOperation.delete(getDoc().getId(position + k).getDigit());
            lop.add(wop);
        }
        getDoc().remove(position, offset);
        return lop;
    }

    @Override
    protected List<? extends Operation> localInsert(SequenceOperation opt) throws IncorrectTraceException {

        List<LogootOperation<T>> lop = new ArrayList<LogootOperation<T>>();
        int N = opt.getContent().size(), position = opt.getPosition();

        List<T> content = opt.getContent();
        List<ListIdentifier> patch = getDoc().generateIdentifiers(position, N);

        for (int cmpt = 0; cmpt < patch.size(); cmpt++) {
            T c = content.get(cmpt);
            LogootOperation log = LogootOperation.insert(patch.get(cmpt), c);
            lop.add(log);
        }

        getDoc().insert(position, lop);
        return lop;
    }

    @Override
    protected List<? extends Operation> localUpdate(SequenceOperation opt) throws IncorrectTraceException {
        return super.localUpdate(opt);
    }

    @Override
    public void setReplicaNumber(int replicaNumber) {
        super.setReplicaNumber(replicaNumber);
        getDoc().setReplicaNumber(replicaNumber);
    }

    @Override
    public CRDT<String> create() {
        return new LogootTreeMerge(getDoc().create(), 0);
    }
}