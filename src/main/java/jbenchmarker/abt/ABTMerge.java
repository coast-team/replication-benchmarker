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
package jbenchmarker.abt;

import crdt.CRDT;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jbenchmarker.core.Document;
import jbenchmarker.core.MergeAlgorithm;
import jbenchmarker.core.SequenceMessage;
import jbenchmarker.core.VectorClock;
import jbenchmarker.trace.IncorrectTrace;
import jbenchmarker.trace.TraceOperation;

/**
*
* @author Roh
*/
public class ABTMerge extends MergeAlgorithm {
	
	protected 	VectorClock siteVC;
	protected 	ABTLog		abtlog;
	private 	ABTGC		abtgc;
	
	public ABTMerge(Document doc, int r){
		super(doc, r);
		siteVC = new VectorClock();
		abtlog = new ABTLog(this.getReplicaNb());
		abtgc  = new ABTGC(this);
	}
	
	@Override
	protected void integrateLocal(SequenceMessage op) throws IncorrectTrace {
		// TODO Auto-generated method stub
		
		ABTOperation abtop = (ABTOperation)op;		
		ABTOperation top = null;
		ABTDocument abtdoc = (ABTDocument)(this.getDoc());
		if (this.readyFor(abtop.sid, abtop.vc)) throw new RuntimeException("it seems causal reception is broken");
		this.siteVC.inc(abtop.getOriginalOp().getReplica());

		top = abtlog.updateHR(abtop);		

		if(top != null) abtdoc.apply(top);
		//this.abtgc.collect(abtop);
	}

	@Override
	protected List<SequenceMessage> generateLocal(TraceOperation opt)
			throws IncorrectTrace {
		// TODO Auto-generated method stub
		List<SequenceMessage> lop		= new ArrayList<SequenceMessage>();
		ABTDocument		abtdoc	= (ABTDocument)(this.getDoc());
		ABTOperation	abtop;
		

		int offset;
		int p = opt.getPosition();
		if(opt.getType() ==  TraceOperation.OpType.del) offset = opt.getOffset();
		else offset = opt.getContent().length();
		
		for(int i=0;i<offset;i++){
			this.siteVC.inc(this.getReplicaNb());
			
			if(opt.getType() == TraceOperation.OpType.del){
				abtop = new ABTOperation(opt,p+1, siteVC);
			} else {
				abtop = new ABTOperation(opt,p+i, opt.getContent().charAt(i), siteVC);
			}
			
			abtdoc.apply(abtop);
			abtop = abtlog.updateHL(abtop);
			
			lop.add(abtop);
		}
		return lop;
	}
	
    public boolean readyFor(int r, VectorClock op) {
        if (this.siteVC.getSafe(r) != op.getSafe(r)) {
            return false;
        }
        for (Map.Entry<Integer, Integer> e : op.entrySet()) {
            if ((e.getKey() != r) && (this.siteVC.getSafe(e.getKey()) < e.getValue())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public CRDT<String> create() {
        return new ABTMerge(new ABTDocument(), -1);
    }
	
}
