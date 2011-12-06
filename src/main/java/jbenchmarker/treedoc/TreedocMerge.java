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

import jbenchmarker.core.MergeAlgorithm;
import jbenchmarker.core.Operation;
import jbenchmarker.trace.IncorrectTrace;
import jbenchmarker.trace.TraceOperation;

/**
 * 
 * @author mzawirski
 */
public class TreedocMerge extends MergeAlgorithm {
	public TreedocMerge(int r) {
		super(new TreedocDocument(UniqueTag.createGenerator(r)), r);
	}

	@Override
	protected void integrateLocal(Operation op) throws IncorrectTrace {
		getDoc().apply(op);
	}

	@Override
	protected List<Operation> generateLocal(TraceOperation opt)
			throws IncorrectTrace {
		final TreedocRoot rootNode = ((TreedocDocument) getDoc()).getRoot();
		final List<Operation> ops = new LinkedList<Operation>();

		switch (opt.getType()) {
		case ins:
			final TreedocIdentifier id = rootNode.insertAt(opt.getPosition(),
					opt.getContent());
			ops.add(new TreedocOperation(opt, id, opt.getContent()));
			break;
		case del:
			// TODO: implement batch delete more efficiently?
			for (int i = opt.getPosition(); i < opt.getPosition()
					+ opt.getOffset(); i++) {
				final TreedocIdentifier deletedId = rootNode.deleteAt(i);
				ops.add(new TreedocOperation(opt, deletedId));
			}
			break;
		default:
			throw new IncorrectTrace("Unsupported operation type");
		}
		return ops;
	}
}
