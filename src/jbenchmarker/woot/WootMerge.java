/**
 *   This file is part of ReplicationBenchmark.
 *
 *   ReplicationBenchmark is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   ReplicationBenchmark is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with ReplicationBenchmark.  If not, see <http://www.gnu.org/licenses/>.
 *
 **/

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jbenchmarker.woot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import jbenchmarker.core.Document;
import jbenchmarker.core.MergeAlgorithm;
import jbenchmarker.core.Operation;
import jbenchmarker.trace.IncorrectTrace;
import jbenchmarker.trace.TraceOperation;

/**
 *
 * @author urso
 */
public class WootMerge extends MergeAlgorithm {
    Map<WootIdentifier, WootOperation> pending;
    
    private int clock;
    
    public WootMerge(Document doc, int r) {
        super(doc, r);
        clock = 0;
    }
    
    @Override
    protected void integrateLocal(Operation op) {
//        WootOperation wop = (WootOperation) op;
//        WootDocument<? extends WootNode> wdoc = (WootDocument<? extends WootNode>) (this.getDoc());
//        if (wop.getType()==TraceOperation.OpType.ins && (!wdoc.has(wop.getIp()) || !wdoc.has(wop.getIp())))
//            pending.put(wop.getId(),wop);
        getDoc().apply(op);
    }

    @Override
    protected List<Operation> generateLocal(TraceOperation opt) throws IncorrectTrace {
        List<Operation> lop = new ArrayList<Operation>();
        WootDocument<? extends WootNode> wdoc = (WootDocument<? extends WootNode>) (this.getDoc());
        int p = opt.getPosition();
        if (opt.getType() == TraceOperation.OpType.del) {
            int v = wdoc.getVisible(p);
            for (int i = 0; i < opt.getOffset(); i++) {
                WootOperation wop = wdoc.delete(opt, wdoc.getElements().get(v).getId());
                lop.add(wop);
                wdoc.apply(wop);
                if (i+1 < opt.getOffset()) v = wdoc.nextVisible(v);
            }         
        } else {
           int ip = wdoc.getPrevious(p);
           int in = wdoc.getNext(ip);
           WootIdentifier idp =  wdoc.getElements().get(ip).getId(),
                   idn =  wdoc.getElements().get(in).getId();
           for (int i = 0; i < opt.getContent().length(); i++) {
                WootIdentifier id = nextIdentifier();
                WootOperation wop = wdoc.insert(opt, id, idp, idn, opt.getContent().charAt(i));
                idp = id;
                lop.add(wop);
                wdoc.apply(wop);
           } 
        }
        return lop;
    }

    private WootIdentifier nextIdentifier() {
        clock++;
        return new WootIdentifier(this.getReplicaNb(), clock);
    }
}
