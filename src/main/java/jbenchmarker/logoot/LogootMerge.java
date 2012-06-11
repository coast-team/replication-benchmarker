/**
 * Replication Benchmarker
 * https://github.com/score-team/replication-benchmarker/
 * Copyright (C) 2011 INRIA / LORIA / SCORE Team
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
package jbenchmarker.logoot;

import collect.OrderedNode;
import crdt.CRDT;
import crdt.tree.orderedtree.PositionIdentifier;
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
public class LogootMerge<T> extends MergeAlgorithm {



    // nbBit <= 64
    public LogootMerge(Document doc, int r) {
        super(doc, r);

    }

    @Override
    protected void integrateRemote(SequenceMessage op) {
        getDoc().apply(op);
    }

    @Override
    protected List<SequenceMessage> generateLocal(SequenceOperation opt) {
        List<SequenceMessage> lop = new ArrayList<SequenceMessage>();
        LogootDocument<T> lg = (LogootDocument<T>) (this.getDoc());
        int N = 0, offset = 0;
        int position = opt.getPosition();

        if (opt.getType() == SequenceOperation.OpType.ins) {
            N = opt.getContent().size();
            List<T> content = opt.getContent();
            ArrayList<LogootIdentifier> patch = lg.generateIdentifiers(position, N);

            ArrayList<T> lc = new ArrayList<T>(patch.size());
            for (int cmpt = 0; cmpt < patch.size(); cmpt++) {
                T c = content.get(cmpt);
                LogootOperation<T> log = LogootOperation.insert(opt, patch.get(cmpt), c);
                lop.add(log);
                lc.add(c);
            }
            lg.insert(position, patch, lc);

        } else {
            offset = opt.getNumberOf();
            for (int k = 1; k <= offset; k++) {
                LogootOperation<T> wop = LogootOperation.Delete(opt, lg.getId(position + k));
                lop.add(wop);
            }
            lg.remove(position, offset);
        }      
        return lop;
    }



    @Override
    public CRDT<String> create() {
        return new LogootMerge(((LogootDocument) getDoc()).create(), 0);
    }

    @Override
    public void setReplicaNumber(int replicaNumber) {
        super.setReplicaNumber(replicaNumber);
        ((LogootDocument) getDoc()).setReplicaNumber(replicaNumber);
    }
}
