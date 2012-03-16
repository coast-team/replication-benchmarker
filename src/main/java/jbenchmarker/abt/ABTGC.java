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

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import collect.VectorClock;

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
				//System.out.println()
				it.remove();
			}
		}
	}
	
}
