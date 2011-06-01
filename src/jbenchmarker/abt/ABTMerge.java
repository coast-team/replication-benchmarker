package jbenchmarker.abt;

import java.util.ArrayList;
import java.util.List;

import jbenchmarker.core.Document;
import jbenchmarker.core.MergeAlgorithm;
import jbenchmarker.core.Operation;
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
	protected void integrateLocal(Operation op) throws IncorrectTrace {
		// TODO Auto-generated method stub
		
		ABTOperation abtop = (ABTOperation)op;		
		ABTOperation top = null;
		ABTDocument abtdoc = (ABTDocument)(this.getDoc());
		
		this.siteVC.inc(abtop.getOriginalOp().getReplica());
		top = abtlog.updateHR(abtop);		
		if(top != null) abtdoc.apply(top);
		this.abtgc.collect(abtop);
	}

	@Override
	protected List<Operation> generateLocal(TraceOperation opt)
			throws IncorrectTrace {
		// TODO Auto-generated method stub
		List<Operation> lop		= new ArrayList<Operation>();
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
}
