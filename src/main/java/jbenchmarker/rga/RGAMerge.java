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

package jbenchmarker.rga;

import java.util.List;
import java.util.ArrayList;
import jbenchmarker.core.VectorClock;
import jbenchmarker.core.Document;
import jbenchmarker.core.MergeAlgorithm;
import jbenchmarker.core.Operation;
import jbenchmarker.trace.IncorrectTrace;
import jbenchmarker.trace.TraceOperation;

/**
*
* @author Roh
*/
public class RGAMerge extends MergeAlgorithm {

	private VectorClock siteVC;
	//private int prt = 104;
	
	public RGAMerge(Document doc, int r){
		super(doc, r);
		siteVC = new VectorClock();
	}
	
	@Override
	protected void integrateLocal(Operation op) throws IncorrectTrace {
		RGAOperation 	rgaop = (RGAOperation) op;
		RGADocument	rgadoc = (RGADocument)(this.getDoc());
		rgadoc.apply(rgaop);
		this.siteVC.inc(rgaop.getOriginalOp().getReplica());		 
	}

	@Override
	protected List<Operation> generateLocal(TraceOperation opt) throws IncorrectTrace {
		List<Operation> lop 		= new ArrayList<Operation>();
		RGADocument 	rgadoc 	= (RGADocument)(this.getDoc());
		RGAS4Vector 		s4vtms, s4vpos = null;
		RGAOperation 	rgaop;		
		RGANode			target = null;
		
		int	 p			= opt.getPosition();
		int offset; 	
		
		if(opt.getType()==TraceOperation.OpType.del) {
			offset = opt.getOffset();
			target = rgadoc.getVisibleNode(p+1);
		} else {
			offset = opt.getContent().length();
			if(p==0) s4vpos = null;
			else s4vpos	= rgadoc.getVisibleS4V(p); // if head, s4vpos = null; if after tail, s4vpos= the last one. 		
		}		
		
		for(int i=0; i < offset ; i++) {
			s4vtms = new RGAS4Vector(this.getReplicaNb(), this.siteVC);
			if(opt.getType() == TraceOperation.OpType.del) {
				rgaop = new RGAOperation(opt, p+1, target.getKey(), s4vtms);
				target = target.getNextVisible();
			} else {
				rgaop = new RGAOperation(opt, p+i, s4vpos, opt.getContent().charAt(i), s4vtms);
				s4vpos = s4vtms; // The s4v of the current insert becomes the s4vpos of next insert.
			}
			lop.add(rgaop);
			rgadoc.apply(rgaop);
			this.siteVC.inc(this.getReplicaNb());
		}

		return lop;
	}	
}
