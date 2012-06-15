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
package jbenchmarker.treedoc;

/**
 * Unique tag of an operation, implemented as timestamp pair: replica identifier
 * and counter.
 * 
 * @author mzawirski
 */
public class UniqueTag implements Comparable<UniqueTag> {
	public final static UniqueTag MIN = new UniqueTag(Integer.MIN_VALUE,
			Integer.MIN_VALUE);
	public final static UniqueTag MAX = new UniqueTag(Integer.MAX_VALUE,
			Integer.MAX_VALUE);

	private final int replicaId;
	private final int counter;

	public static UniqueTagGenerator createGenerator() {
		return new UniqueTagGenerator() {
			int currentStamp;

			@Override
			public UniqueTag nextTag(final int replicaId) {
				return new UniqueTag(replicaId, currentStamp++);
			}
		};
	}

	public UniqueTag(final int replicaId, final int counter) {
		this.replicaId = replicaId;
		this.counter = counter;
	}

	public int getReplicaId() {
		return replicaId;
	}

	public int getCounter() {
		return counter;
	}

	@Override
	public int compareTo(UniqueTag o) {
		if (replicaId != o.replicaId) {
			return replicaId - o.replicaId;
		}
		return counter - o.counter;
	}

	public UniqueTag clone() {
		return new UniqueTag(replicaId, counter);
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof UniqueTag))
			return false;
		final UniqueTag other = (UniqueTag) obj;
		return counter == other.counter && replicaId == other.replicaId;
	}

	@Override
	public String toString() {
		return "(" + replicaId + "," + counter + ")";
	}
}
