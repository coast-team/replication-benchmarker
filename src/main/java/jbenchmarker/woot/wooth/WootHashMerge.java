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


package jbenchmarker.woot.wooth;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import jbenchmarker.core.Document;
import jbenchmarker.core.MergeAlgorithm;
import jbenchmarker.core.Operation;
import jbenchmarker.trace.IncorrectTrace;
import jbenchmarker.trace.TraceOperation;
import jbenchmarker.woot.WootIdentifier;
import jbenchmarker.woot.WootOperation;

/**
 *
 * @author urso
 */
public class WootHashMerge extends MergeAlgorithm {
    // logical clock
    private int clock;
    
    public WootHashMerge(Document doc, int r) {
        super(doc, r);
        clock = 0;
    }
    
    @Override
    protected void integrateLocal(Operation op) {
        getDoc().apply(op);
    }

    @Override
    protected List<Operation> generateLocal(TraceOperation opt) throws IncorrectTrace {
        List<Operation> lop = new ArrayList<Operation>();
        WootHashDocument wdoc = (WootHashDocument) (this.getDoc());
        int p = opt.getPosition();
        if (opt.getType() == TraceOperation.OpType.del) {
            WootHashNode w = wdoc.getVisible(p);
            for (int i = 0; i < opt.getOffset(); i++) {
                WootOperation wop = wdoc.delete(opt, w.getId());
                lop.add(wop);
                wdoc.apply(wop);
                if (i+1 < opt.getOffset()) w = wdoc.nextVisible(w);
            }         
        } else {
           WootHashNode ip = wdoc.getPrevious(p), in = wdoc.getNext(ip);
           WootIdentifier idp =  ip.getId(), idn =  in.getId();
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
