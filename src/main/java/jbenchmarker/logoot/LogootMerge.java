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
package jbenchmarker.logoot;

import crdt.CRDT;
import crdt.Operation;
import crdt.simulator.IncorrectTraceException;
import java.util.ArrayList;
import java.util.List;
import jbenchmarker.core.Document;
import jbenchmarker.core.MergeAlgorithm;
import crdt.Operation;
import jbenchmarker.core.SequenceOperation;

/**
 *
 * @author mehdi urso
 */
public class LogootMerge<T> extends MergeAlgorithm {

    // nbBit <= 64
    public LogootMerge(Document doc, int r) {
        super(doc, r);

    }

    @Override
    public LogootDocument<T> getDoc() {
        return (LogootDocument<T>) super.getDoc();
    }

    @Override
    protected void integrateRemote(crdt.Operation message) {
        getDoc().apply(message);
    }

    @Override
    protected List<Operation> localDelete(SequenceOperation opt) throws IncorrectTraceException {
        List<Operation> lop = new ArrayList<Operation>();
        int offset = opt.getLenghOfADel(), position = opt.getPosition();

        for (int k = 1; k <= offset; k++) {
            LogootOperation<T> wop = LogootOperation.delete(getDoc().getId(position + k));
            lop.add(wop);
        }
        getDoc().remove(position, offset);
        return lop;
    }

    @Override
    protected List<Operation> localInsert(SequenceOperation opt) throws IncorrectTraceException {
        List<Operation> lop = new ArrayList<Operation>();
        int N = opt.getContent().size(), position = opt.getPosition();

        List<T> content = opt.getContent();
        List<ListIdentifier> patch = getDoc().generateIdentifiers(position, N);

        ArrayList<T> lc = new ArrayList<T>(patch.size());
        for (int cmpt = 0; cmpt < patch.size(); cmpt++) {
            T c = content.get(cmpt);
            LogootOperation<T> log = LogootOperation.insert(patch.get(cmpt), c);
            lop.add(log);
            lc.add(c);
        }

        getDoc().insert(position, patch, lc);
        return lop;
    }

    // For tests
    @Override
    protected List<? extends Operation> localUpdate(SequenceOperation opt) throws IncorrectTraceException {
        return super.localUpdate(opt);
    }

    @Override
    public CRDT<String> create() {
        return new LogootMerge(getDoc().create(), 0);
    }

    @Override
    public void setReplicaNumber(int replicaNumber) {
        super.setReplicaNumber(replicaNumber);
        getDoc().setReplicaNumber(replicaNumber);
    }
}
