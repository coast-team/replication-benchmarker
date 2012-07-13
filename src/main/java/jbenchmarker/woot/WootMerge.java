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
package jbenchmarker.woot;

import crdt.CRDT;
import crdt.Factory;
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
public class WootMerge<T> extends MergeAlgorithm {
    Map<WootIdentifier, WootOperation> pending;
    
    private int clock;
    
    public WootMerge(Document doc, int r) {
        super(doc, r);
        clock = 0;
    }
    
    @Override
    protected void integrateRemote(SequenceMessage op) {
//        WootOperation wop = (WootOperation) op;
//        WootDocument<? extends WootNode> wdoc = (WootDocument<? extends WootNode>) (this.getDoc());
//        if (wop.getType()==SequenceOperation.OpType.ins && (!wdoc.has(wop.getIp()) || !wdoc.has(wop.getIp())))
//            pending.put(wop.getId(),wop);
        getDoc().apply(op);
    }

    @Override
    protected List<SequenceMessage> generateLocal(SequenceOperation opt) throws IncorrectTraceException {
        List<SequenceMessage> lop = new ArrayList<SequenceMessage>();
        WootDocument<? extends WootNode> wdoc = (WootDocument<? extends WootNode>) (this.getDoc());
        int p = opt.getPosition();
        if (opt.getType() == SequenceOperation.OpType.del) {
            int v = wdoc.getVisible(p);
            for (int i = 0; i < opt.getLenghOfADel(); i++) {
                WootOperation wop = wdoc.delete(opt, wdoc.getElement(v).getId());
                wdoc.setInvisible(v);
                lop.add(wop);
                if (i+1 < opt.getLenghOfADel()) v = wdoc.nextVisible(v);
            }         
        } else {
           int ip = wdoc.getPrevious(p);
           int in = wdoc.getNext(ip);
           WootIdentifier idp =  wdoc.getElement(ip).getId(),
                   idn =  wdoc.getElement(in).getId();
           for (int i = 0; i < opt.getContent().size(); i++) {
                WootIdentifier id = nextIdentifier();
                WootOperation wop = wdoc.insert(opt, id, idp, idn, opt.getContent().get(i));
                wdoc.insertLocal(wop, ip, idp, in+i);
                idp = id;
                lop.add(wop);
           } 
        }
        return lop;
    }

    private WootIdentifier nextIdentifier() {
        clock++;
        return new WootIdentifier(this.getReplicaNumber(), clock);
    }

    @Override
    public CRDT<String> create() {
        return new WootMerge(((Factory<Document>) this.getDoc()).create(), -1);
    }
}
