package jbenchmarker.abt;

import java.util.ArrayList;
import java.util.List;

import jbenchmarker.core.Document;
import jbenchmarker.core.MergeAlgorithm;
import jbenchmarker.core.Operation;
import jbenchmarker.core.VectorClock;
import jbenchmarker.trace.IncorrectTrace;
import jbenchmarker.trace.TraceOperation;

public class ABTMerge extends MergeAlgorithm {
	
	private VectorClock siteVC;
	private ABTLog		abtlog;
	public ABTMerge(Document doc, int r){
		super(doc, r);
		siteVC = new VectorClock();
		abtlog = new ABTLog(this.getReplicaNb());
		
	}
	
	@Override
	protected void integrateLocal(Operation op) throws IncorrectTrace {
		// TODO Auto-generated method stub
		
		ABTOperation abtop = (ABTOperation)op.clone();
		ABTOperation top = null;
		ABTDocument abtdoc = (ABTDocument)(this.getDoc());
		
		//if(true) return;
		this.siteVC.inc(abtop.getOriginalOp().getReplica());
	/*
		if(abtop.sid==8){
			if(abtop.pos==4995) abtlog.printHistory();
			System.out.print(abtop+" ==> ");// at site "+this.getReplicaNb());
		}
		*/
		top = abtlog.updateHR(abtop);		
		//if(abtop.sid==8) System.out.println(top+" at site "+this.getReplicaNb()); 
		if(top != null) abtdoc.apply(top);
		
		//System.out.println(abtdoc.model);
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
		
			//System.out.println("!at site "+this.getReplicaNb()+" "+abtop);
			abtdoc.apply(abtop);
			abtop=abtlog.updateHL(abtop);
			//System.out.println("==>"+abtop);
			//abtlog.printHistory();
			lop.add(abtop);
		}
		return lop;
	}
}
