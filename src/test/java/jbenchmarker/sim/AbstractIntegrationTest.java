/**
 * Replication Benchmarker
 * https://github.com/score-team/replication-benchmarker/
 * Copyright (C) 2011 INRIA / LORIA / SCORE Team
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
package jbenchmarker.sim;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import jbenchmarker.core.MergeAlgorithm;
import jbenchmarker.core.ReplicaFactory;
import jbenchmarker.trace.TraceGenerator;

import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author mzawirski
 */
public abstract class AbstractIntegrationTest {
	protected OldCausalDispatcher cd;

	@Before
	public void setUp() {
		cd = new OldCausalDispatcher(createFactory());
	}

	protected abstract ReplicaFactory createFactory();

	protected void assertConsistentViews() {
		String referenceView = null;
		for (final MergeAlgorithm replica : cd.getReplicas().values()) {
			final String view = replica.getDoc().view();
			if (referenceView == null)
				referenceView = view;
			else
				assertEquals(referenceView, view);
		}
		assertNotNull(referenceView);
	}

	@Test
	public void testExempleRun() throws Exception {
		cd.run(TraceGenerator.traceFromXML("../../traces/xml/exemple.xml", 1));
		assertConsistentViews();
	}

	@Test
	public void testG1Run() throws Exception {
		cd.run(TraceGenerator.traceFromXML("../../traces/xml/G1.xml", 1));
		assertConsistentViews();
	}

	@Test
	public void testG2Run() throws Exception {
		cd.run(TraceGenerator.traceFromXML("../../traces/xml/G2.xml", 1, 16));
		assertConsistentViews();
	}

	@Test
	public void testG3Run() throws Exception {
		cd.run(TraceGenerator.traceFromXML("../../traces/xml/G3.xml", 1));
		assertConsistentViews();
	}

	@Test
	public void testSerieRun() throws Exception {
		cd.run(TraceGenerator.traceFromXML("../../traces/xml/Serie.xml", 1));
		assertConsistentViews();
	}
}
