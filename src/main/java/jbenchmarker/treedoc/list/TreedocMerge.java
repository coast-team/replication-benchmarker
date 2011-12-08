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

import java.util.ArrayList;

import java.util.LinkedList;
import java.util.List;

import citi.treedoc.TreedocId;

import jbenchmarker.core.MergeAlgorithm;
import jbenchmarker.core.Operation;
import jbenchmarker.trace.IncorrectTrace;
import jbenchmarker.trace.TraceOperation;

/**
 * 
 * @author mzawirski
 */
public class TreedocMerge extends MergeAlgorithm {

	public TreedocMerge(final TreedocDocument doc, int r) {
		super(doc, r);
	}

	@Override
	protected void integrateLocal(Operation op) throws IncorrectTrace {
		getDoc().apply(op);
	}

	@Override
	protected List<Operation> generateLocal(TraceOperation opt)
			throws IncorrectTrace {
		final TreedocDocument treedoc = (TreedocDocument) getDoc();
		final String content = opt.getContent();
		final List<Operation> ops = new LinkedList<Operation>();

		switch (opt.getType()) {
		case ins:
			final int index = restrictedIndex(opt.getPosition(), true);
			if (content.length() == 1) {
				final TreedocId id = treedoc.insert(index, content.charAt(0),
						getReplicaNb());
				ops.add(new TreedocOperation(opt, id, content.charAt(0)));
			} else {
				final List<Character> characters = new ArrayList<Character>(
						content.length());
				for (int i = 0; i < content.length(); i++)
					characters.add(content.charAt(i));
				final List<TreedocId> ids = treedoc.insert(index,
						content.length(), characters, getReplicaNb());
				for (int i = 0; i < characters.size(); i++)
					ops.add(new TreedocOperation(opt, ids.get(i), characters
							.get(i)));
			}
			break;
		case del:
			// TODO: implement batch delete more efficiently?
			for (int i = opt.getPosition(); i < opt.getPosition()
					+ opt.getOffset(); i++) {
				final TreedocId id = treedoc.remove(restrictedIndex(i, false));
				ops.add(new TreedocOperation(opt, id));
			}
			break;
		default:
			throw new IncorrectTrace("Unsupported operation type");
		}
		return ops;
	}

	protected int restrictedIndex(final int index, final boolean insert) {
		// FIXME: Hack with restricting index within the range!
		// It seems to be caused by Simulator replaying delete blindly without
		// verifying replica document size first. Not 100% sure though.
		return Math.min(index, ((TreedocDocument) getDoc()).size()
				- (insert ? 0 : 1));
	}
}
