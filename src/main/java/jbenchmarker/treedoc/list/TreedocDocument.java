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
package jbenchmarker.treedoc.list;

import java.util.Collections;

import jbenchmarker.core.Document;
import jbenchmarker.core.SequenceMessage;
import jbenchmarker.trace.TraceOperation;
import citi.treedoc.Treedoc;
import citi.treedoc.TreedocId;
import citi.treedoc.TreedocIdFactory;

/**
 * 
 * @author mzawirski
 */
public class TreedocDocument extends Treedoc<TreedocId, Character> implements
		Document {

	public TreedocDocument() {
		super(TreedocIdFactory.getFactory());
	}

	/**
	 * Deletes atom with Id id.
	 */
	protected void remove(TreedocId id) {
		int pos = Collections.binarySearch(ids, id);
		if (pos >= 0)
			remove(pos);
	}

	@Override
	public String view() {
		final StringBuilder buffer = new StringBuilder(size());
		for (final Character ch : data)
			buffer.append(ch);
		return buffer.toString();
	}

	@Override
	public void apply(SequenceMessage op) {
		final TreedocOperation treedocOp = (TreedocOperation) op;
		if (treedocOp.getOriginalOp().getType() == TraceOperation.OpType.ins) {
			insert(treedocOp.getId(), treedocOp.getContent());
		} else {
			remove(treedocOp.getId());
		}
	}
}
