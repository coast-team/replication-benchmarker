package jbenchmarker.rgalocal;

import jbenchmarker.rgalocal.RGAMerge;
import jbenchmarker.rgalocal.RGADocument;
import jbenchmarker.rgalocal.RGAFFactory;
import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.List;
import jbenchmarker.core.SequenceOperation;
import jbenchmarker.core.SequenceOperation.OpType;
import org.junit.Before;
import org.junit.Test;
import crdt.PreconditionException;
import crdt.simulator.IncorrectTraceException;



public class RGADocumentTest {
	
	static List<String> input(String A){
		List<String> a= new ArrayList<String>();
		for (int i=0; i<A.length(); i++){
			a.add((A.substring(i,i+1)));	
		}
		return a;			
	}
	
	static void apply(RGADocument rgadoc, SequenceOperation so, RGAMerge merge) throws IncorrectTraceException{
		if (so.getType()==OpType.insert){
			merge.localInsert(so);
		}
		else if (so.getType()==OpType.delete){
			merge.localDelete(so);
		}
	}

	private static final int REPLICA_ID = 7;
	private RGAMerge replica;

	@Before
	public void setUp() throws Exception {
		replica = (RGAMerge) new RGAFFactory().create(REPLICA_ID);
	}

	@Test
	public void testEmpty() {
		assertEquals("", replica.lookup());
	}


	@Test
	public void testRemoteInsert() throws PreconditionException {     
		replica.applyLocal(SequenceOperation.insert(0, "abcdejk"));
		assertEquals("Insertion du contenu initial", "abcdejk", replica.lookup());

		replica.applyLocal(SequenceOperation.insert(3, "fghi"));
		assertEquals("Insertion au milieu","abcfghidejk", replica.lookup()); 

		replica.applyLocal(SequenceOperation.insert(11, "lmnop"));
		assertEquals("Insertion à la fin","abcfghidejklmnop", replica.lookup()); 

	}


	@Test
	public void testRemoteDelete() throws PreconditionException {
		replica.applyLocal(SequenceOperation.insert(0,"abcdefghijklmnopq"));
		assertEquals("Insertion du contenu","abcdefghijklmnopq", replica.lookup());
		
		replica.applyLocal(SequenceOperation.delete(1, 4));
		assertEquals("Suppression en début","afghijklmnopq", replica.lookup());

		replica.applyLocal(SequenceOperation.delete(5, 8));
		assertEquals("Suppression au milieu","afghi", replica.lookup());

		replica.applyLocal(SequenceOperation.delete(1, 3));
		assertEquals("Suppression au milieu","ai", replica.lookup());
		
		replica.applyLocal(SequenceOperation.delete(1, 1));
		assertEquals("Suppression à la fin","a", replica.lookup());

		replica.applyLocal(SequenceOperation.delete(0, 1));
		assertEquals("Suppression au début","", replica.lookup());
	}

}
