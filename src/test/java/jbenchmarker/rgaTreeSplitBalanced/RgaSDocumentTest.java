package jbenchmarker.rgaTreeSplitBalanced;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import jbenchmarker.core.SequenceOperation;
import jbenchmarker.core.SequenceOperation.OpType;
import jbenchmarker.factories.RgaTreeSplitBalancedFactory;

import org.junit.Before;
import org.junit.Test;

import crdt.PreconditionException;
import crdt.simulator.IncorrectTraceException;
import jbenchmarker.rgaTreeSplitBalanced.RgaSDocument.Position;



public class RgaSDocumentTest {
	
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

	private static final int REPLICA_ID = 7;
	private RgaSMerge replica;

	@Before
	public void setUp() throws Exception {
		replica = (RgaSMerge) new RgaTreeSplitBalancedFactory().create(REPLICA_ID);
	}

	@Test
	public void testEmpty() {
		assertEquals("", replica.lookup());
	}

	@Test
	public void testGetPosition() {
		RgaSDocument rgadoc = new RgaSDocument();
		Position position;

		RgaSS3Vector s3v1 = new RgaSS3Vector(0,1,0);
		RgaSS3Vector s3v2 = new RgaSS3Vector(0,2,0);
		RgaSS3Vector s3v3 = new RgaSS3Vector(0,3,0);
		RgaSNode node1 = new RgaSNode(s3v1,null,null,input("abcdef"),false, null);
		RgaSNode node2 = new RgaSNode(s3v2,null,null,input("ghijkl"),false, null);
		RgaSNode node3 = new RgaSNode(s3v3,null,null,input("mnopq"),false, null);

		rgadoc.getHead().setNext(node1);
		node1.setNext(node2);
		node2.setNext(node3);

		position= rgadoc.getPosition(rgadoc.getHead(),0);
		assertEquals("abcdef,0", position.node.getContentAsString()+","+position.offset);

		position= rgadoc.getPosition(rgadoc.getHead(),4);
		assertEquals("abcdef,4", position.node.getContentAsString()+","+position.offset);

		position= rgadoc.getPosition(rgadoc.getHead(),6);
		assertEquals("ghijkl,0", position.node.getContentAsString()+","+position.offset);

		position= rgadoc.getPosition(rgadoc.getHead(),7);
		assertEquals("ghijkl,1", position.node.getContentAsString()+","+position.offset);

		position= rgadoc.getPosition(rgadoc.getHead(),12);
		assertEquals("mnopq,0", position.node.getContentAsString()+","+position.offset);

		position= rgadoc.getPosition(rgadoc.getHead(),15);
		assertEquals("mnopq,3", position.node.getContentAsString()+","+position.offset);
	}

	


	@Test
	public void testRemoteSplit() throws PreconditionException {

		RgaSDocument rgadoc = new RgaSDocument();
		RgaSMerge merge0 = new RgaSMerge(rgadoc,0); 
		SequenceOperation so1  = new SequenceOperation (OpType.insert, 0, 0, input("abcdefghijklmnopq"));

		apply(rgadoc, so1, merge0);
		assertEquals("Insertion au début","abcdefghijklmnopq", rgadoc.view());

		rgadoc.remoteSplit(rgadoc.getHead().getNextVisible(), 0);
		assertEquals("Split au début","->|abcdefghijklmnopq|", rgadoc.viewWithSeparator());
		assertEquals("Split au début",0, rgadoc.getHead().getNextVisible().getKey().getOffset());

		rgadoc.remoteSplit(rgadoc.getHead().getNextVisible(), 17);
		assertEquals("Split à la fin","->|abcdefghijklmnopq|", rgadoc.viewWithSeparator());
		assertEquals("Split au début",0, rgadoc.getHead().getNextVisible().getKey().getOffset());

		rgadoc.remoteSplit(rgadoc.getHead().getNextVisible(), 9);
		assertEquals("Split au milieu","->|abcdefghi|->|jklmnopq|", rgadoc.viewWithSeparator());
		assertEquals("Verif offset",9, rgadoc.getHead().getNextVisible().getNextVisible().getKey().getOffset());

		rgadoc.remoteSplit(rgadoc.getHead().getNextVisible().getNextVisible(), 14);
		assertEquals("Split au milieu","->|abcdefghi|->|jklmn|->|opq|", rgadoc.viewWithSeparator());
		assertEquals("Verif offset",9, rgadoc.getHead().getNextVisible().getNextVisible().getKey().getOffset());
		assertEquals("Verif offset",14, rgadoc.getHead().getNextVisible().getNextVisible().getNextVisible().getKey().getOffset());



	}


	@Test
	public void testRemoteInsert() throws PreconditionException {    
		
		
		replica.applyLocal(SequenceOperation.insert(0, "abcdejk"));
		assertEquals("Insertion du contenu initial", "abcdejk", replica.lookup());

		replica.applyLocal(SequenceOperation.insert(3, "fg"));
		assertEquals("Insertion au milieu","abcfgdejk", replica.lookup()); 

		
		replica.applyLocal(SequenceOperation.insert(5, "hi"));
	
		assertEquals("Insertion au milieu","abcfghidejk", replica.lookup());
		
		
		replica.applyLocal(SequenceOperation.insert(11, "lmnop"));
		
		
		assertEquals("Insertion à la fin","abcfghidejklmnop", replica.lookup()); 
		
	}


	@Test
	public void testRemoteDelete() throws PreconditionException {
		replica.applyLocal(SequenceOperation.insert(0,"abcdefghijklmnopq"));
		assertEquals("Insertion du contenu","abcdefghijklmnopq", replica.lookup());
		System.out.println("\n\nINSERT1");
		replica.applyLocal(SequenceOperation.delete(1, 4));
		assertEquals("Suppression en début","afghijklmnopq", replica.lookup());

		System.out.println("\n\nINSERT2");
		
		replica.applyLocal(SequenceOperation.delete(5, 8));
		assertEquals("Suppression au milieu","afghi", replica.lookup());

		System.out.println("\nENDINSERT\n\n");
		replica.applyLocal(SequenceOperation.delete(1, 3));
		assertEquals("Suppression au milieu","ai", replica.lookup());
		
		replica.applyLocal(SequenceOperation.delete(1, 1));
		assertEquals("Suppression à la fin","a", replica.lookup());

		replica.applyLocal(SequenceOperation.delete(0, 1));
		assertEquals("Suppression au début","", replica.lookup());
	}

}
