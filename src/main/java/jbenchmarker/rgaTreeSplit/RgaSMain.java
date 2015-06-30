package jbenchmarker.rgaTreeSplit;

import java.util.ArrayList;
import java.util.List;

import crdt.simulator.IncorrectTraceException;
import jbenchmarker.core.SequenceOperation;
import jbenchmarker.core.SequenceOperation.OpType;
import jbenchmarker.rgaTreeSplit.RgaSDocument;
import jbenchmarker.rgaTreeSplit.RgaSNode;
import jbenchmarker.rgaTreeSplit.RgaSS3Vector;
import jbenchmarker.rgaTreeSplit.RgaSDocument.Position;
import jbenchmarker.rgaTreeSplit.RgaSMerge;

public class RgaSMain {

	
	static List<String> input(String A){
		List<String> a= new ArrayList<String>();
		for (int i=0; i<A.length(); i++){
			a.add((A.substring(i,i+1)));	
		}
		return a;			
	}
	
	static void apply(RgaSDocument rgadoc, SequenceOperation so, RgaSMerge merge) throws IncorrectTraceException{
		if (so.getType()==OpType.insert){
			merge.localInsert(so);
		}
		else if (so.getType()==OpType.delete){
			merge.localDelete(so);
		}
	}
	
	public static void main(String[] args) {

		RgaSDocument rgadoc = new RgaSDocument();
		Position position;
		RgaSNode test;
		RgaSS3Vector s3v1 = new RgaSS3Vector(0,1,0);
		RgaSS3Vector s3v2 = new RgaSS3Vector(0,2,2);
		RgaSS3Vector s3v3 = new RgaSS3Vector(0,3,5);
		RgaSS3Vector s3v4 = new RgaSS3Vector(0,4,7);
		RgaSS3Vector s3v5 = new RgaSS3Vector(0,5,14);

		RgaSNode node1 = new RgaSNode(s3v1,null,null,input("ab"),false);
		RgaSNode node2 = new RgaSNode(s3v2,null,null,input("cde"),false);
		RgaSNode node3 = new RgaSNode(s3v3,null,null,input("fg"),false);
		RgaSNode node4 = new RgaSNode(s3v4,null,null,input("hijklmn"),false);
		RgaSNode node5 = new RgaSNode(s3v5,null,null,input("opq"),false);

		/*
		RgaSTree tree5 = new RgaSTree(node1, null, null);
		RgaSTree tree4 = new RgaSTree(node2, tree2, tree3)
		RgaSTree tree3 = new RgaSTree(node1, tree2, tree3)
		RgaSTree tree2 = new RgaSTree(node1, tree2, tree3)
		RgaSTree tree1 = new RgaSTree(node1, tree2, tree3)
*/
		rgadoc.getHead().setNext(node1);
		node1.setNext(node2);
		node2.setNext(node3);
		node3.setNext(node4);
		node4.setNext(node5);
		
		
		System.out.println(rgadoc.view());
		System.out.println(rgadoc.viewWithSeparator());
	}

}
