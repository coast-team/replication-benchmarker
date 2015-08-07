package jbenchmarker.rgaTreeSplitBalanced;

import crdt.CRDT;

import java.util.List;
import java.util.ArrayList;

import collect.VectorClock;
import jbenchmarker.core.MergeAlgorithm;
import crdt.Operation;
import crdt.simulator.IncorrectTraceException;
import jbenchmarker.core.SequenceOperation;
import jbenchmarker.rgaTreeSplitBalanced.RgaSDocument;
import jbenchmarker.rgaTreeSplitBalanced.RgaSNode;
import jbenchmarker.rgaTreeSplitBalanced.RgaSInsertion;
import jbenchmarker.rgaTreeSplitBalanced.RgaSS3Vector;
import jbenchmarker.rgaTreeSplitBalanced.RgaSDocument.Position;



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
		if (rgaop.getType()== SequenceOperation.OpType.insert){
			this.siteVC.inc(rgaop.getReplica());
		}
		rgadoc.apply(rgaop);
	}


	@Override
	protected List<? extends Operation> localInsert(SequenceOperation so) throws IncorrectTraceException {

		List<Operation> lop = new ArrayList<Operation>();
		RgaSDocument rgadoc = (RgaSDocument) (this.getDoc());
		RgaSS3Vector s3vtms, s3vpos = null;
		Operation rgaop;

		Position position = rgadoc.findPosInLocalTree(so.getPosition());

		if (so.getPosition() <= 0 || rgadoc.getRoot()==null) {
			s3vpos = null;
		} else {
			s3vpos = position.node.getKey().clone();
		}

		this.siteVC.inc(this.getReplicaNumber());
		s3vtms = new RgaSS3Vector(this.getReplicaNumber(), this.siteVC, 0);
		rgaop = new RgaSInsertion(so.getContent(), s3vpos, s3vtms, position.offset);

		lop.add(rgaop);
		rgadoc.apply(rgaop);

		return lop;

	}


	@Override
	protected List<Operation> localDelete(SequenceOperation so) throws IncorrectTraceException {

		List<Operation> lop = new ArrayList<Operation>();
		RgaSDocument rgadoc = (RgaSDocument) (this.getDoc());
		RgaSOperation rgaop;

		Position positionStart = rgadoc.findPosInLocalTree(so.getPosition()+1);    
		Position positionEnd = rgadoc.findPosInLocalTree(so.getPosition() +  so.getLenghOfADel()); 

		RgaSNode node = positionStart.node;
		RgaSNode target = positionEnd.node;

		int offsetStart = positionStart.offset-1;
		int offsetEnd = positionEnd.offset;

		if (node.equals(target)){
			rgaop = new RgaSDeletion(node.getKey().clone(), offsetStart, offsetEnd);
			rgadoc.apply(rgaop);
			lop.add(rgaop);

		} else {
			rgaop = new RgaSDeletion(node.getKey().clone(), positionStart.offset-1, node.size() + node.getOffset());
			rgadoc.apply(rgaop);
			lop.add(rgaop);
			node=node.getNextVisible();

			while (node!=null && !node.equals(target)){
				rgaop = new RgaSDeletion(node.getKey().clone(), 0, node.size()  + node.getOffset());
				rgadoc.apply(rgaop);
				lop.add(rgaop);
				node=node.getNextVisible();
			} 	

			if (positionEnd.offset!=0) {
				rgaop = new RgaSDeletion(target.getKey().clone(), 0, offsetEnd);
				rgadoc.apply(rgaop);
				lop.add(rgaop);
			}
		}

		return lop;
	}

}
