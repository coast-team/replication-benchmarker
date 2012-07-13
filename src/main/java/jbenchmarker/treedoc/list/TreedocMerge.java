/**
 * Replication Benchmarker
 * https://github.com/score-team/replication-benchmarker/
 * Copyright (C) 2012 LORIA / Inria / SCORE Team
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

import crdt.CRDT;
import java.util.ArrayList;

import java.util.LinkedList;
import java.util.List;

import citi.treedoc.TreedocId;

import jbenchmarker.core.MergeAlgorithm;
import jbenchmarker.core.SequenceMessage;
import crdt.simulator.IncorrectTraceException;
import jbenchmarker.core.SequenceOperation;

/**
 * 
 * @author mzawirski
 */
public class TreedocMerge<T> extends MergeAlgorithm {

	public TreedocMerge(final TreedocDocument doc, int r) {
		super(doc, r);
	}

	@Override
	protected void integrateRemote(SequenceMessage op) throws IncorrectTraceException {
		getDoc().apply(op);
	}

	@Override
	protected List<SequenceMessage> generateLocal(SequenceOperation opt)
			throws IncorrectTraceException {
		final TreedocDocument<T> treedoc = (TreedocDocument) getDoc();
		final List<T> content = opt.getContent();
		final List<SequenceMessage> ops = new LinkedList<SequenceMessage>();

		switch (opt.getType()) {
		case ins:
			final int index = restrictedIndex(opt.getPosition(), true);
			if (content.size() == 1) {
				final TreedocId id = treedoc.insert(index, content.get(0),
						getReplicaNumber());
				ops.add(new TreedocOperation(opt, id, content.get(0)));
			} else {
				final List<T> characters = new ArrayList<T>(
						content.size());
				for (int i = 0; i < content.size(); i++)
					characters.add(content.get(i));
				final List<TreedocId> ids = treedoc.insert(index,
						content.size(), characters, getReplicaNumber());
				for (int i = 0; i < characters.size(); i++)
					ops.add(new TreedocOperation(opt, ids.get(i), characters
							.get(i)));
			}
			break;
		case del:
			// TODO: implement batch delete more efficiently?
			for (int i = opt.getPosition(); i < opt.getPosition()
					+ opt.getLenghOfADel(); i++) {
				final TreedocId id = treedoc.remove(restrictedIndex(i, false));
				ops.add(new TreedocOperation(opt, id));
			}
			break;
		default:
			throw new IncorrectTraceException("Unsupported operation type");
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

    @Override
    public CRDT<String> create() {
        return new TreedocMerge(new TreedocDocument(), 0);
    }
}
