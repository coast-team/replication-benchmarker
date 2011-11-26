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

import jbenchmarker.core.Operation;
import jbenchmarker.trace.TraceOperation;
import jbenchmarker.trace.TraceOperation.OpType;

/**
 * 
 * @author mzawirski
 */
public class TreedocOperation extends Operation {
	private final TreedocIdentifier id;
	private final char content;

	/**
	 * Creates Treedoc operation with dummy content.
	 * 
	 * @param o
	 *            original trace operation.
	 * @param id
	 *            treedoc position identifier.
	 */
	public TreedocOperation(final TraceOperation o, final TreedocIdentifier id) {
		super(o);
		this.id = id;
		this.content = (char) 0; // Dummy.
	}

	/**
	 * Creates Treedoc operation with provided content.
	 * 
	 * @param o
	 *            original trace operation.
	 * @param id
	 *            treedoc position identifier.
	 * @param content
	 *            content expressed as a single character.
	 */
	public TreedocOperation(final TraceOperation o, final TreedocIdentifier id,
			final char content) {
		super(o);
		this.id = id;
		this.content = content;
	}

	public OpType getType() {
		return getOriginalOp().getType();
	}

	public TreedocIdentifier getId() {
		return id;
	}

	public char getContent() {
		return content;
	}

	@Override
	public TreedocOperation clone() {
		return new TreedocOperation(getOriginalOp(), id.clone(), content);
	}
}
