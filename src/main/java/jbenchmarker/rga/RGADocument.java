package jbenchmarker.rga;

import jbenchmarker.core.Document;
import jbenchmarker.core.Operation;
import jbenchmarker.trace.TraceOperation;
import java.util.HashMap;
import java.util.NoSuchElementException; 

/**
*
* @author Roh
*/
public class RGADocument implements Document {
	
	private HashMap<RGAS4Vector, RGANode> 	hash;
	private RGANode											head;
	
	public RGADocument(){
		super();
		head = new RGANode();		
		hash	= new HashMap<RGAS4Vector, RGANode>(); 
	}
	
	public String view() {
		StringBuilder s = new StringBuilder();
		RGANode node = head.getNext();
		while(node!=null){
			if(node.isVisible()) s.append(node.getContent());
			node = node.getNext();
		}
		return s.toString();
	}
	
	
	public void apply(Operation op) {
		RGAOperation rgaop = (RGAOperation)op;
//		if(rgaop.getLoR() == RGAOperation.LOCAL){
//			if(rgaop.getType() == TraceOperation.OpType.del) LocalDelete(rgaop);
//			else LocalInsert(rgaop);			
//		} else {
			if(rgaop.getType() == TraceOperation.OpType.del) RemoteDelete(rgaop);
			else RemoteInsert(rgaop);
//		}
	}

	private void LocalInsert(RGAOperation op){
		RGANode newnd = new RGANode(op.getS4VTms(), op.getContent());
		
		if(op.getIntPos() == 0){			
			newnd.setNext(head.getNext());	
			head.setNext(newnd);
		} else {
			RGANode target 	= getVisibleNode(op.getIntPos());  
			if(target == null)  throw new NoSuchElementException("Don't find " + op.getIntPos());			
			newnd.setNext(target.getNext());			
			target.setNext(newnd);
		}		
		hash.put(op.getS4VTms(), newnd);
	}
	
	private void LocalDelete(RGAOperation op){
		RGANode node = getVisibleNode(op.getIntPos()); 
		if(node == null)  throw new NoSuchElementException("Don't find " + op.getIntPos());
		node.makeTombstone();
	}
	
	private void RemoteInsert(RGAOperation op){
		RGANode newnd = new RGANode(op.getS4VTms(), op.getContent());
		RGANode prev, next;
		RGAS4Vector		s4v = op.getS4VTms();
		if(op.getS4VPos() == null) prev = head;
		else prev = hash.get(op.getS4VPos());
		if(prev == null) throw new NoSuchElementException("RemoteInsert");
		next = prev.getNext();
		
		while(next!=null) {
			if(s4v.compareTo(next.getKey()) == RGAS4Vector.AFTER) break;
			prev = next;
			next = next.getNext();
		}
		
		newnd.setNext(next);
		prev.setNext(newnd);
		hash.put(op.getS4VTms(), newnd);
	}
	
	private void RemoteDelete(RGAOperation op){
		RGANode node = hash.get(op.getS4VPos());
		if(node == null) throw new NoSuchElementException("Cannot find" + op.getS4VPos());
		node.makeTombstone();
	}
	
	public RGAS4Vector getVisibleS4V(int v){
		RGANode node = getVisibleNode(v);
		if(node == null) throw new NoSuchElementException("getVisibleS4V"); 
		return node.getKey();
	}
		
	public RGANode getVisibleNode(int v){
		RGANode 	node = head;
		int 			j 		= 0;
		
		while(j < v && node != null){
			node = node.getNext();
			if(node != null && node.isVisible()) j++;
		}		
		
		if(node == null || !node.isVisible()) throw new NoSuchElementException("getVisibleNode"); 
		return node;		
	}
}
