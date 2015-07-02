package jbenchmarker.rgaTreeSplit;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import crdt.CRDTMessage;
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
		RgaSNode node2b1 = new RgaSNode(s3v2,null,null,input("cd"),false);
		RgaSNode node2b2 = new RgaSNode(s3v2,null,null,input("e"),false);
		RgaSNode node3 = new RgaSNode(s3v3,null,null,input("fg"),false);
		RgaSNode node4 = new RgaSNode(s3v4,null,null,input("hijklmn"),false);
		RgaSNode node5 = new RgaSNode(s3v5,null,null,input("opq"),false);
		RgaSNode node6 = new RgaSNode(s3v2,null,null,input("110"),false);
		



		RgaSTree tree5 = new RgaSTree(node1, null, null);
		RgaSTree tree4 = new RgaSTree(node4, null, null);
		RgaSTree tree3 = new RgaSTree(node2, tree5, null);
		RgaSTree tree2 = new RgaSTree(node5, tree4, null);
		RgaSTree tree1 = new RgaSTree(node3, tree3, tree2);

		rgadoc.setRoot(tree1);

		rgadoc.getHead().setNext(node1);
		node1.setNext(node2);
		node2.setNext(node3);
		node3.setNext(node4);
		node4.setNext(node5);



		System.out.println(rgadoc.view());
		System.out.println(rgadoc.viewWithSeparator());
		System.out.println("\n\n\n");
		rgadoc.viewTreeWithSeparator(rgadoc.getRoot(),0);
		rgadoc.viewTree(rgadoc.getRoot());
		System.out.println("\n\n\n");

		System.out.println(rgadoc.find(15));
		//rgadoc.treeSplit(tree3,node2bis,2);
		
		//rgadoc.insert(4,node6,node2b1,node2b2);
		
		//rgadoc.viewTreeWithSeparator(rgadoc.getRoot(),0);
		//rgadoc.viewTree(rgadoc.getRoot());
		//System.out.println("\n\n\n");

		/*RgaSS3Vector s3v1 = new RgaSS3Vector(0,1,0);
		RgaSS3Vector s3v2 = new RgaSS3Vector(0,2,2);
		RgaSS3Vector s3v3 = new RgaSS3Vector(0,3,5);

		RgaSNode node1 = new RgaSNode(s3v1,null,null,input("abcdefghij"),false);
		RgaSNode node12 = new RgaSNode(s3v1,null,null,input("ab"),false);
		RgaSNode node13 = new RgaSNode(s3v1,null,null,input("cdefghij"),false);
		RgaSNode node14 = new RgaSNode(s3v1,null,null,input("cdefg"),false);
		RgaSNode node15 = new RgaSNode(s3v1,null,null,input("hij"),false);
		RgaSNode node2 = new RgaSNode(s3v2,null,null,input("2"),false);
		RgaSNode node3 = new RgaSNode(s3v3,null,null,input("7"),false);
		
		RgaSTree tree1 = new RgaSTree(node1, null, null);
		rgadoc.setRoot(tree1);
		rgadoc.insert(2,node2,node12,node13);
		
		rgadoc.viewTreeWithSeparator(rgadoc.getRoot(),0);
		rgadoc.viewTree(rgadoc.getRoot());
		System.out.println("\n\n\n");
		
		rgadoc.insert(7,node3,node14,node15);
		
		rgadoc.viewTreeWithSeparator(rgadoc.getRoot(),0);
		rgadoc.viewTree(rgadoc.getRoot());
		System.out.println("\n\n\n");
		*/
		
		/*
		CRDTMessage m1 = replica.applyLocal(SequenceOperation.insert(0, content));
		CRDTMessage m2 = replica.applyLocal(SequenceOperation.insert(2, "2"));
		CRDTMessage m3 = replica.applyLocal(SequenceOperation.insert(7, "7"));
		assertEquals("ab2cdef7ghij", replica.lookup());
*/
	}

}
