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
package jbenchmarker.rga;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

import collect.VectorClock;
import java.io.Serializable;

public class RGAPurger implements Serializable{

	private Map<Integer, VectorClock> m_lastVC;
	private Map<Integer, Integer>	  m_minseq;	
	private Map<Integer, Integer>	  m_sums;
	private Map<Integer, Boolean>	  m_purgable;
	private int m_minsum = Integer.MAX_VALUE;

	private RGADocument 	doc;
	//private Map<Integer, LinkedList<RGANode>> cemetery;
	
	
	public RGAPurger(RGADocument doc){
		//cemetery = new HashMap<Integer, LinkedList<RGANode>>();		
		m_lastVC = new HashMap<Integer, VectorClock>();
		m_minseq = new HashMap<Integer, Integer>();
		m_sums   = new HashMap<Integer, Integer>();
		m_purgable= new HashMap<Integer, Boolean>();
		this.doc = doc;
	}
	
	private int getSafeMinSeq(int i){
		Integer ret = m_minseq.get(i);
		if(ret==null) {
			m_sums.put(i, 0);
			return 0;
		}
		return ret.intValue();
	}
	
	private int getSafeSums(int i){
		Integer ret = m_sums.get(i);
		if(ret==null) {
			m_sums.put(i,0);
			return 0;
		}
		return ret.intValue();
	}
	
	/*
	public void enrol(RGANode tau){
		int sid = tau.getTomb().sid;
		LinkedList<RGANode> ar = cemetery.get(sid);
		if(ar==null){
			ar = new LinkedList<RGANode>();
			cemetery.put(sid, ar);
		}
		ar.add(tau);
	}*/
	
	private void evaluateSEQ(int sid){
		int seq=0;
		int minseq = Integer.MAX_VALUE;
		VectorClock vc;
		for(Integer s1 : m_lastVC.keySet()){
			vc = m_lastVC.get(s1);
			seq = vc.getSafe(sid);
			if(minseq > seq) minseq = seq; 
		}
		
		if(this.getSafeMinSeq(sid)!=minseq) {
			m_minseq.put(sid, minseq);
			m_purgable.put(sid, true);
		}		
	}
	
	private void evaluate(int sid, VectorClock vv){
		int sum = 0;
		int s, vve;		
		Iterator<Integer> it=vv.keySet().iterator();
		
		while(it.hasNext()){
			s = it.next();
			vve = vv.getSafe(s);
			if(this.getSafeMinSeq(s) < vve) evaluateSEQ(s);
			sum += vve;
		}
		
		if(this.getSafeSums(sid) != sum) {
			m_sums.put(sid, sum);
			m_purgable.put(sid, true);
		}
		
		m_minsum = Integer.MAX_VALUE;
		
		it = m_sums.keySet().iterator();		
		
		for(Integer i: m_sums.keySet()){
			int isum = m_sums.get(i);
			if(m_minsum > isum) m_minsum = isum; 
		}
	}

	public void setLastVC(int sid, VectorClock newvc){		
		m_lastVC.put(sid, (VectorClock)newvc.clone());
		//evaluate(sid, newvc);
	}
	
	private boolean isPurgable(RGANode tau){
		int sid = tau.getTomb().sid;
		RGANode next = tau.getNext();		
		
		if((tau.getTomb().seq < getSafeMinSeq(sid)) && 
				(next==null || next.getKey().sum < m_minsum))
			return true;
		return false;
	}
	
	/*
	public RGANode tryPurge(){
		
		for(Integer i:m_purgable.keySet()){
			if(m_purgable.get(i)){
				
			}
		}
		return null;
	}
	*/
}
