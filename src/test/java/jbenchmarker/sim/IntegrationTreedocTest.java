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

import crdt.CRDT;
import crdt.simulator.CausalSimulator;
import jbenchmarker.core.MergeAlgorithm;
import jbenchmarker.core.ReplicaFactory;
import jbenchmarker.treedoc.TreedocDocument;
import jbenchmarker.factories.TreedocFactory;

/**
 * 
 * @author mzawirski
 */
public class IntegrationTreedocTest extends AbstractIntegrationTest {
	@Override
	protected ReplicaFactory createFactory() {
		return new TreedocFactory();
	}

	@Override
	protected void assertConsistentViews(CausalSimulator cd) {
		super.assertConsistentViews(cd);
		for (CRDT replica : cd.getReplicas().values())
			((TreedocDocument) ((MergeAlgorithm) replica).getDoc()).printStats();
	}
}
