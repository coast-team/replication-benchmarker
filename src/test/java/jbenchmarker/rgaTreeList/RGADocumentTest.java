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

import crdt.Factory;
import crdt.PreconditionException;
import crdt.simulator.IncorrectTraceException;
import crdt.simulator.random.StandardDiffProfile;

import java.io.IOException;
import jbenchmarker.factories.RGATreeListFactory;
import org.junit.Test;

import static org.junit.Assert.*;

public class RGADocumentTest {


//
	@Test
	public void testview() 	{
		System.out.println("Test RGADocument ...");
		RGADocument doc = new RGADocument();

		assertEquals("", doc.view());
	}
	
	@Test
	public void test(){
		TreeList list = new TreeList();
		for (int i=0; i<8; i++){
			list.add(new RGANode(null, i));
		}
		
		System.out.println(list);
		list.treeViewWithSeparator(list.getRoot(),0);
	}
	
	@Test
	public void testRunRGA() throws IncorrectTraceException, PreconditionException, IOException {
		StandardDiffProfile SMALL = new StandardDiffProfile(0.1, 0.8, 0.8, 20, 3.0, 4, 3);
		crdt.simulator.CausalDispatcherSetsAndTreesTest.testRun((Factory) new RGATreeListFactory(), 10, 10, SMALL);
	}
	
}
