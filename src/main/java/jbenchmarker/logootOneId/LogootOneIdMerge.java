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
/**
 * Replication Benchmarker
 * https://github.com/score-team/replication-benchmarker/ Copyright (C) 2011
 * INRIA / LORIA / SCORE Team
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
package jbenchmarker.logootOneId;

import crdt.CRDT;
import crdt.RemoteOperation;
import crdt.simulator.IncorrectTraceException;
import java.math.BigInteger;
import jbenchmarker.core.MergeAlgorithm;
import jbenchmarker.core.SequenceMessage;
import jbenchmarker.core.SequenceOperation;
import jbenchmarker.core.Document;

import java.util.*;

/**
 *
 * @author mehdi urso
 */
public class LogootOneIdMerge<T> extends MergeAlgorithm {

    public LogootOneIdMerge(Document doc, int r) {
        super(doc, r);
    }

    @Override
    public LogootOneIdDocument<T> getDoc() {
        return (LogootOneIdDocument<T>) super.getDoc();
    }

    @Override
    protected void integrateRemote(RemoteOperation op) {
        getDoc().apply(op);
    }

    @Override
    protected List<SequenceMessage> localDelete(SequenceOperation opt) throws IncorrectTraceException {
        List<SequenceMessage> lop = new ArrayList<SequenceMessage>();
        int offset = opt.getLenghOfADel(), position = opt.getPosition();

        for (int k = 1; k <= offset; k++) {
            LogootOneIdOperation<T> wop = LogootOneIdOperation.Delete(opt, getDoc().getId(position + k));
            lop.add(wop);
        }
        getDoc().remove(position, offset);
        return lop;
    }

    @Override
    protected List<SequenceMessage> localInsert(SequenceOperation opt) throws IncorrectTraceException {

        List<SequenceMessage> lop = new ArrayList<SequenceMessage>();
        int N = opt.getContent().size(), position = opt.getPosition();

        List<T> content = opt.getContent();
        ArrayList<LogootOneIdentifier> patch = getDoc().generateIdentifiers(position, N);

        ArrayList<T> lc = new ArrayList<T>(patch.size());
        for (int cmpt = 0; cmpt < patch.size(); cmpt++) {
            T c = content.get(cmpt);
            LogootOneIdOperation<T> log = LogootOneIdOperation.insert(opt, patch.get(cmpt), c);
            lop.add(log);
            lc.add(c);
        }

        getDoc().insert(position, patch, lc);

        return lop;
    }

    @Override
    protected List<? extends RemoteOperation> localUpdate(SequenceOperation opt) throws IncorrectTraceException {
        return super.localUpdate(opt);
    }

    @Override
    public void setReplicaNumber(int replicaNumber) {
        super.setReplicaNumber(replicaNumber);
        getDoc().setReplicaNumber(replicaNumber);
    }

    @Override
    public CRDT<String> create() {
        return new LogootOneIdMerge(getDoc().create(), 0);
    }
}