/**
 * Replication Benchmarker
 * https://github.com/score-team/replication-benchmarker/
 * Copyright (C) 2013 LORIA / Inria / SCORE Team
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
package jbenchmarker.rgaTreeList;

import collect.TreeList;
import crdt.PreconditionException;
import crdt.simulator.IncorrectTraceException;
import java.util.ArrayList;
import java.util.List;
import jbenchmarker.core.SequenceOperation;
import jbenchmarker.core.SequenceOperation.OpType;
import jbenchmarker.factories.RGATreeListFactory;
import org.junit.Test;

import static org.junit.Assert.*;
import org.junit.Before;

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
		replica = (RGAMerge) new RGATreeListFactory().create(REPLICA_ID);
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

	public void test(){
		TreeList list = new TreeList();
		for (int i=0; i<8; i++){
			list.add(new RGANode(null, i));
		}
		
		System.out.println(list);
		list.treeViewWithSeparator(list.getRoot(),0);
	}
}
