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

import java.util.LinkedList;
import java.util.List;

import jbenchmarker.core.Document;
import jbenchmarker.core.MergeAlgorithm;
import jbenchmarker.core.Operation;
import jbenchmarker.trace.IncorrectTrace;
import jbenchmarker.trace.TraceOperation;

/**
 * 
 * @author mzawirski
 */
public class TreedocMerge extends MergeAlgorithm {
	private int replicaClock;

	public TreedocMerge(final Document doc, int r) {
		super(doc, r);
	}

	@Override
	protected void integrateLocal(Operation op) throws IncorrectTrace {
		getDoc().apply(op);
	}

	@Override
	protected List<Operation> generateLocal(TraceOperation opt)
			throws IncorrectTrace {
		final TreedocNode rootNode = ((TreedocDocument) getDoc()).getRootNode();
		final List<Operation> ops = new LinkedList<Operation>();
		final UniqueTag timestamp = getNextTimestamp();

		switch (opt.getType()) {
		case ins:
			final String content = opt.getContent();
			// TODO: Implement batch operations efficiently!! This is an ugly
			// impl.
			for (int i = 0; i < content.length(); i++) {
				final char ch = content.charAt(i);
				final TreedocIdentifier id = rootNode.insertAt(
						i + opt.getPosition(), ch, timestamp);
				ops.add(new TreedocOperation(opt, id, ch));
			}
			break;
		case del:
			for (int i = opt.getPosition(); i < opt.getPosition()
					+ opt.getOffset(); i++) {
				final TreedocIdentifier id = rootNode.deleteAt(i);
				ops.add(new TreedocOperation(opt, id));
			}
			break;
		default:
			throw new IncorrectTrace("Unsupported operation type");
		}
		return ops;
	}

	private UniqueTag getNextTimestamp() {
		return new UniqueTag(getReplicaNb(), replicaClock++);
	}
}
