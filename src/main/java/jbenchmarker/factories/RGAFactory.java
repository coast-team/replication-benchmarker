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
package jbenchmarker.factories;

import jbenchmarker.core.MergeAlgorithm;
import jbenchmarker.core.ReplicaFactory;
import jbenchmarker.rga.RGADocument;
import jbenchmarker.rga.RGAMerge;

public class RGAFactory extends ReplicaFactory {

    @Override
	public MergeAlgorithm create(int r) {
		return new RGAMerge(new RGADocument(), r);
	}
    

	static RGADocument createDoc(int r, int base) {
		return new RGADocument();
	}

	public static class ShortList<T> extends ReplicaFactory {
		@Override
		public RGAMerge create(int r) {         
			return new RGAMerge(createDoc(r, 16), r);
		}
	}
}
