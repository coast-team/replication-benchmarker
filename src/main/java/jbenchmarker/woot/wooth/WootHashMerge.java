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
package jbenchmarker.woot.wooth;

import crdt.CRDT;
import crdt.Operation;
import crdt.simulator.IncorrectTraceException;
import java.util.ArrayList;
import java.util.List;
import jbenchmarker.core.Document;
import jbenchmarker.core.MergeAlgorithm;
import jbenchmarker.core.SequenceOperation;
import jbenchmarker.woot.WootIdentifier;
import jbenchmarker.woot.WootOperation;

/**
 *
 * @author urso
 */
public class WootHashMerge<T> extends MergeAlgorithm {
    // logical clock

    public WootHashMerge(Document doc, int r) {
        super(doc, r);
    }

    @Override
    protected void integrateRemote(crdt.Operation message) {
        getDoc().apply(message);
    }

    @Override
    protected List<Operation> localDelete(SequenceOperation opt) throws IncorrectTraceException {
        List<Operation> lop = new ArrayList<Operation>();
        WootHashDocument wdoc = this.getDoc();
        int p = opt.getPosition();
        LinkedNode<T> w = wdoc.getVisible(p);
        for (int i = 0; i < opt.getLenghOfADel(); i++) {
            WootOperation wop = wdoc.delete(opt, w.getId());
            lop.add(wop);
            wdoc.apply(wop);
            if (i + 1 < opt.getLenghOfADel()) {
                w = wdoc.nextVisible(w);
            }
        }
        return lop;
    }

    @Override
    protected List<Operation> localInsert(SequenceOperation opt) throws IncorrectTraceException {
        List<Operation> lop = new ArrayList<Operation>();
        WootHashDocument wdoc = this.getDoc();
        int p = opt.getPosition();
        LinkedNode<T> ip = wdoc.getPrevious(p), in = wdoc.getNext(ip);
        WootIdentifier idp = ip.getId(), idn = in.getId();
        for (int i = 0; i < opt.getContent().size(); i++) {
            WootOperation wop = wdoc.insert(opt, idp, idn, opt.getContent().get(i));
            idp = wop.getId();
            lop.add(wop);
            wdoc.apply(wop);
        }
        return lop;
    }

    @Override
    protected List<? extends Operation> localUpdate(SequenceOperation opt) throws IncorrectTraceException {
        return localReplace(opt);
    }

    @Override
    public WootHashDocument getDoc() {
        return (WootHashDocument) super.getDoc();
    }

    @Override
    public void setReplicaNumber(int replicaNumber) {
        super.setReplicaNumber(replicaNumber);
        this.getDoc().setReplicaNumber(replicaNumber);
    }

    @Override
    public CRDT<String> create() {
        return new WootHashMerge(getDoc().create(), -1);
    }
}
