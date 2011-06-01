package jbenchmarker.abt;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import jbenchmarker.core.VectorClock;

public class ABTGC {
	private ABTMerge merge;
	private Map<Integer, VectorClock> allVC = new TreeMap<Integer, VectorClock>();
	private static final int GC_FREQUENCY_IN_OPERATIONS = 20;
	private int cntdown = GC_FREQUENCY_IN_OPERATIONS;
	
	public ABTGC(ABTMerge m){
		this.merge = m;
	}
	
	public void collect(ABTOperation op){
		this.allVC.put(op.sid, op.vc);
		
		this.cntdown--;
		if(this.cntdown == 0) {
			gc();
			this.cntdown = this.GC_FREQUENCY_IN_OPERATIONS;
		}
	}
	
	private void gc(){
		VectorClock commonAncesstorVC = merge.siteVC.min(allVC.values());
		Iterator<ABTOperation> it = this.merge.abtlog.Hi.iterator();
		
		while(it.hasNext()){
			if(!it.next().vc.greaterThan(commonAncesstorVC)){
				it.remove();
			}
		}
	}
	
}
