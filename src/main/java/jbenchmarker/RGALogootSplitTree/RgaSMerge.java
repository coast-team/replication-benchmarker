package jbenchmarker.RGALogootSplitTree;

import crdt.CRDT;

import java.util.List;
import java.util.ArrayList;

import collect.VectorClock;
import jbenchmarker.core.MergeAlgorithm;
import crdt.Operation;
import crdt.simulator.IncorrectTraceException;
import jbenchmarker.core.SequenceOperation;
import jbenchmarker.RGALogootSplitTree.RgaSDocument;
import jbenchmarker.RGALogootSplitTree.RgaSNode;
import jbenchmarker.RGALogootSplitTree.RgaSOperation;
import jbenchmarker.RGALogootSplitTree.RgaSS3Vector;
import jbenchmarker.RGALogootSplitTree.RgaSDocument.Position;
import jbenchmarker.RGALogootSplitTree.Identifier;
import jbenchmarker.RGALogootSplitTree.IDFactory;
import jbenchmarker.RGALogootSplitTree.IdentifierInterval;



public class RgaSMerge extends MergeAlgorithm {

	private VectorClock siteVC;
	private List<Integer> baseMin=new ArrayList<Integer>();
	private List<Integer> baseMax =new ArrayList<Integer>();
	
	


	public RgaSMerge(RgaSDocument doc, int siteID) {
		super(doc, siteID);
		siteVC = new VectorClock();
		baseMin.add(Integer.MIN_VALUE);
		baseMax.add(Integer.MAX_VALUE);
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
		rgadoc.apply(rgaop);
	}


	@Override
	protected List<? extends Operation> localInsert(SequenceOperation so) throws IncorrectTraceException {
		List<Operation> lop = new ArrayList<Operation>();
		RgaSDocument rgadoc = (RgaSDocument) (this.getDoc());
		RgaSOperation rgaop;
		IdentifierInterval nodeIdPos;
		Identifier id1 = new Identifier();
		Identifier id2 = new Identifier();
		
		Position position = rgadoc.findPosInLocalTree(so.getPosition());

		//System.out.println("Pos: " + position);
		if (so.getPosition() <= 0 || rgadoc.getRoot()==null) {
			nodeIdPos = null;

		} else {
			nodeIdPos = position.node.getNodeID().clone();

		}
		
		
		if(position.node==null && rgadoc.getHead().getNext()==null){
			id1 = new Identifier(baseMin, 0);
			id2 = new Identifier(baseMax, 0);
			
		} else if(position.node==null){
			id1 = new Identifier(baseMin, 0);
			id2 = new Identifier(rgadoc.getHead().getNext().getBase(), rgadoc.getHead().getNext().getBegin()); 
		
		} else if (position.node.getNext() == null){
			
			id1 = new Identifier(position.node.getBase(), position.node.getEnd());
			id2 = new Identifier(baseMax, 0);
	    
		} else {
			id1 = new Identifier(position.node.getBase(), position.node.getEnd());
			id2 = new Identifier(position.node.getNext().getBase(), position.node.getNext().getBegin());
		}

		List<Integer> base = IDFactory.createBetweenPosition(id1, id2, this.getReplicaNumber(), rgadoc.incClock());
		
		LogootBaseFamily baseFamily = new LogootBaseFamily(base, new ArrayList());
		IdentifierInterval newNodeID = new IdentifierInterval(baseFamily, 0, so.getContent().size() - 1);

		rgaop = new RgaSOperation(so.getContent(), nodeIdPos, newNodeID, position.offset, so.getPosition());
		lop.add(rgaop);
		rgadoc.apply(rgaop);
		
		return lop;

	}


	@Override
	protected List<Operation> localDelete(SequenceOperation so) throws IncorrectTraceException {
		List<Operation> lop = new ArrayList<Operation>();
		
		RgaSDocument rgadoc = (RgaSDocument) (this.getDoc());
		RgaSOperation rgaop;

		Position positionStart = rgadoc.findPosInLocalTree(so.getPosition()+1);    //rgadoc.getPosition(rgadoc.getHead(),start);
		Position positionEnd = rgadoc.findPosInLocalTree(so.getPosition() +  so.getLenghOfADel());     //rgadoc.getPosition(node,end-start+positionStart.offset);

		RgaSNode node = positionStart.node;
		RgaSNode target = positionEnd.node;

		if (node.equals(target)){
			rgaop = new RgaSOperation(node.getNodeID().clone(),positionStart.offset, positionEnd.offset,0,0);
			rgadoc.apply(rgaop);
			lop.add(rgaop);

		} else {
			rgaop = new RgaSOperation(node.getNodeID().clone(), positionStart.offset, node.size(),0,0);
			rgadoc.apply(rgaop);
			lop.add(rgaop);
			node=node.getNext();

			while (node!=null && !node.equals(target)){
				rgaop = new RgaSOperation(node.getNodeID().clone(), 0, node.size(),0,0);
				rgadoc.apply(rgaop);
				lop.add(rgaop);
				node=node.getNext();
			} 	

			if (positionEnd.offset!=0) {
				rgaop = new RgaSOperation(target.getNodeID().clone(), 0, positionEnd.offset,0,0);
				rgadoc.apply(rgaop);
				lop.add(rgaop);
			}
		}

		return lop;
	}

}
