package jbenchmarker.rgaTreeSplit;

import crdt.CRDT;

import java.util.List;
import java.util.ArrayList;

import collect.VectorClock;
import jbenchmarker.core.MergeAlgorithm;
import crdt.Operation;
import crdt.simulator.IncorrectTraceException;
import jbenchmarker.core.SequenceOperation;
import jbenchmarker.rgaTreeSplit.RgaSDocument.Position;



public class RgaSMerge extends MergeAlgorithm {

	private VectorClock siteVC;

	
	
	
	public RgaSMerge(RgaSDocument doc, int siteID) {
		super(doc, siteID);
		siteVC = new VectorClock();
	}
	
	@Override
	public CRDT<String> create() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void setReplicaNumber(int r){
		super.setReplicaNumber(r);
	}

	
	
	
	@Override
	protected void integrateRemote(crdt.Operation message) throws IncorrectTraceException {
		RgaSOperation rgaop = (RgaSOperation) message;
		RgaSDocument rgadoc = (RgaSDocument) (this.getDoc());
		this.siteVC.inc(rgaop.getReplica());
		rgadoc.apply(rgaop);
	}

	
	
	
	
	@Override
	protected List<? extends Operation> localInsert(SequenceOperation so) throws IncorrectTraceException {

		List<Operation> lop = new ArrayList<Operation>();
		RgaSDocument rgadoc = (RgaSDocument) (this.getDoc());
		RgaSS3Vector s3vtms, s3vpos = null;
		RgaSOperation rgaop;
				
		Position position = rgadoc.find(so.getPosition());

		if (so.getPosition() <= 0 || rgadoc.getRoot()==null) {
			s3vpos = null;
		} else {
			s3vpos = position.node.getKey().clone();
		}

		this.siteVC.inc(this.getReplicaNumber());
		s3vtms = new RgaSS3Vector(this.getReplicaNumber(), this.siteVC, 0);
		rgaop = new RgaSOperation(so.getContent(), s3vpos, s3vtms, position.offset, so.getPosition());
		lop.add(rgaop);
		rgadoc.apply(rgaop);

		return lop;

	}



	@Override
	protected List<Operation> localDelete(SequenceOperation so) throws IncorrectTraceException {

		List<Operation> lop = new ArrayList<Operation>();
		RgaSDocument rgadoc = (RgaSDocument) (this.getDoc());
		RgaSOperation rgaop;
		RgaSNode node, target;

		int start = so.getPosition();
		int end = so.getPosition() + so.getLenghOfADel();
		Position positionStart, positionEnd ;
				
		positionStart = rgadoc.find(so.getPosition()+1);    //rgadoc.getPosition(rgadoc.getHead(),start);
		node = positionStart.node;
		
		positionEnd = rgadoc.find(so.getPosition() +  so.getLenghOfADel());     //rgadoc.getPosition(node,end-start+positionStart.offset);
		target = positionEnd.node;


		if (node.equals(target)){
			rgaop = new RgaSOperation(node.getKey().clone(),positionStart.offset, positionEnd.offset,0,0);
			rgadoc.apply(rgaop);
			lop.add(rgaop);

		} else {
			rgaop = new RgaSOperation(node.getKey().clone(), positionStart.offset, node.size(),0,0);
			rgadoc.apply(rgaop);
			lop.add(rgaop);
			node=node.getNextVisible();

			while (node!=null && !node.equals(target)){
				rgaop = new RgaSOperation(node.getKey().clone(), 0, node.size(),0,0);
				rgadoc.apply(rgaop);
				lop.add(rgaop);
				node=node.getNextVisible();
			} 	

			if (positionEnd.offset!=0) {
				rgaop = new RgaSOperation(target.getKey().clone(), 0, positionEnd.offset,0,0);
				rgadoc.apply(rgaop);
				lop.add(rgaop);
			}
		}
		return lop;
	}

}
