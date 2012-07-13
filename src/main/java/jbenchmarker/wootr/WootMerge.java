/**
 * Replication Benchmarker
 * https://github.com/score-team/replication-benchmarker/
 * Copyright (C) 2012 LORIA / Inria / SCORE Team
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
package jbenchmarker.wootr;

import crdt.CRDT;
import crdt.CRDTMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import jbenchmarker.core.Document;
import jbenchmarker.core.MergeAlgorithm;
import jbenchmarker.core.SequenceMessage;
import crdt.simulator.IncorrectTraceException;
import jbenchmarker.core.SequenceOperation;

/**
 *
 * @author urso
 */
public class WootMerge extends MergeAlgorithm {
    
    
    public WootMerge(Document doc, int r) {
        super(doc, r);
    }
    
    @Override
    protected void integrateRemote(SequenceMessage op) {
//        WootROperation wop = (WootROperation) op;
//        WootRDocument<? extends WootRNode> wdoc = (WootRDocument<? extends WootRNode>) (this.getDoc());
//        if (wop.getType()==SequenceOperation.OpType.ins && (!wdoc.has(wop.getIp()) || !wdoc.has(wop.getIp())))
//            pending.put(wop.getId(),wop);
        getDoc().apply(op);
    }

    @Override
    protected List<SequenceMessage> generateLocal(SequenceOperation opt) throws IncorrectTraceException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public CRDT<String> create() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
