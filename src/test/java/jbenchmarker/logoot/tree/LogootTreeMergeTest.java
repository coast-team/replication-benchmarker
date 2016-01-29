/**
 * Replication Benchmarker
 * https://github.com/score-team/replication-benchmarker/ Copyright (C) 2013
 * LORIA / Inria / SCORE Team
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package jbenchmarker.logoot.tree;

import jbenchmarker.rgaTreeList.*;
import crdt.CRDTMessage;
import crdt.Factory;
import crdt.PreconditionException;
import crdt.simulator.IncorrectTraceException;
import crdt.simulator.random.StandardSeqOpProfile;

import java.io.IOException;

import jbenchmarker.core.MergeAlgorithm;
import jbenchmarker.core.SequenceOperation;
import jbenchmarker.factories.LogootTreeFactory;
import jbenchmarker.factories.RGATreeListFactory;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class LogootTreeMergeTest {

    private static final int REPLICA_ID = 7;
    private LogootTreeMerge replica;

    @Before
    public void setUp() throws Exception {
        replica = (LogootTreeMerge) new LogootTreeFactory().create(REPLICA_ID);
    }

    @Test
    public void testEmptyTree() {
        assertEquals("", replica.lookup());
    }

    @Test
    public void testInsert() throws PreconditionException {
        String content = "abcdejk", c2 = "fghi";
        int pos = 3;
        replica.applyLocal(SequenceOperation.insert(0, content));
        assertEquals(content, replica.lookup());
        replica.applyLocal(SequenceOperation.insert(pos, c2));
        assertEquals(content.substring(0, pos) + c2 + content.substring(pos), replica.lookup());
    }

	@Test
	public void testUpdate() throws PreconditionException {
		String content = "abcdefghijk", upd = "xy";
		int pos = 3, off = 5;       
		replica.applyLocal(SequenceOperation.insert(0, content));
		assertEquals(content, replica.lookup());
		replica.applyLocal(SequenceOperation.replace(pos, off, upd));
		assertEquals(content.substring(0, pos) + upd + content.substring(pos+off), replica.lookup());        
	}

	
	@Test
	public void testConcurrentDelete() throws PreconditionException {
		String content = "abcdefghij";
		
		CRDTMessage m1 = replica.applyLocal(SequenceOperation.insert(0, content));
		assertEquals(content, replica.lookup());
		
		replica.applyLocal(SequenceOperation.insert(2, "2"));
		assertEquals("ab2cdefghij", replica.lookup());
		
		replica.applyLocal(SequenceOperation.insert(7, "7"));
		assertEquals("ab2cdef7ghij", replica.lookup());

		MergeAlgorithm replica2 = (MergeAlgorithm) new RGATreeListFactory().create();
		replica2.setReplicaNumber(2);
		m1.execute(replica2);
		assertEquals(content, replica2.lookup());
		
		CRDTMessage m2 = replica2.applyLocal(SequenceOperation.delete(1, 8));
		assertEquals("aj", replica2.lookup());
		
		m2.execute(replica);
		assertEquals("a27j", replica.lookup());
	}

	
	@Test
	public void testMultipleDeletions() throws PreconditionException {

		String content = "abcdefghij";
		CRDTMessage m1 = replica.applyLocal(SequenceOperation.insert(0, content));

		CRDTMessage m2 = replica.applyLocal(SequenceOperation.insert(2, "28"));
		assertEquals("ab28cdefghij", replica.lookup());

		CRDTMessage m3 = replica.applyLocal(SequenceOperation.insert(10, "73"));
		assertEquals("ab28cdefgh73ij", replica.lookup());

		CRDTMessage m4 = replica.applyLocal(SequenceOperation.delete(3, 8));
		assertEquals("ab23ij", replica.lookup());

		MergeAlgorithm replica2 = (MergeAlgorithm) new RGATreeListFactory().create();
		replica2.setReplicaNumber(2);
		m1.execute(replica2);
		m2.execute(replica2);
		m3.execute(replica2);		
		assertEquals("ab28cdefgh73ij", replica2.lookup());

		m4.execute(replica2);
		CRDTMessage m5 = replica2.applyLocal(SequenceOperation.insert(4, "01"));
		m5.execute(replica);
		assertEquals("ab2301ij", replica.lookup());
		assertEquals("ab2301ij", replica2.lookup());
	}

	
	@Test
	public void testMultipleUpdates() throws PreconditionException {
		System.out.println("\n\n\nBEGIN");
		String content = "abcdefghij";

		CRDTMessage m1 = replica.applyLocal(SequenceOperation.insert(0, content));
		CRDTMessage m2 = replica.applyLocal(SequenceOperation.insert(2, "2"));
		CRDTMessage m3 = replica.applyLocal(SequenceOperation.insert(7, "7"));
		assertEquals("ab2cdef7ghij", replica.lookup());

		CRDTMessage m4 = replica.applyLocal(SequenceOperation.replace(1, 10,"test"));
		assertEquals("atestj", replica.lookup());

		MergeAlgorithm replica2 = (MergeAlgorithm) new RGATreeListFactory().create();
		replica2.setReplicaNumber(2);
		m1.execute(replica2);
		m2.execute(replica2);
		m3.execute(replica2);
		CRDTMessage m5 =replica2.applyLocal(SequenceOperation.insert(4, "01"));
		m4.execute(replica2);
		assertEquals("atest01j", replica2.lookup()); 

		m5.execute(replica);
		assertEquals("atest01j", replica.lookup()); 
	} 


	@Test
	public void testConcurrentUpdate() throws PreconditionException{
		System.out.println("END\n\n\n");
		String content = "abcdefghij";
		CRDTMessage m1 = replica.applyLocal(SequenceOperation.insert(0, content));

		replica.applyLocal(SequenceOperation.replace(2, 4, "27"));
		assertEquals("ab27ghij", replica.lookup());

		MergeAlgorithm replica2 = (MergeAlgorithm) new RGATreeListFactory().create();
		replica2.setReplicaNumber(2);
		m1.execute(replica2);
		CRDTMessage m2 = replica2.applyLocal(SequenceOperation.replace(1, 8, "test"));
		m2.execute(replica);
		assertEquals("atestj", replica2.lookup());
		assertEquals("atest27j", replica.lookup());
	}

    @Test
    public void testDelete() throws PreconditionException {
        String content = "abcdefghijk";
        int pos = 3, off = 4;
        replica.applyLocal(SequenceOperation.insert(0, content));
        assertEquals(content, replica.lookup());
        replica.applyLocal(SequenceOperation.delete(pos, off));
        assertEquals(content.substring(0, pos) + content.substring(pos + off), replica.lookup());
    }

    

    @Test
    public void testRun() throws IncorrectTraceException, PreconditionException, IOException {
        crdt.simulator.CausalDispatcherSetsAndTreesTest.testRun((Factory) new RGATreeListFactory(), 500, 500, StandardSeqOpProfile.BASIC);
    }
}
