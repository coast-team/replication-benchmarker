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

import jbenchmarker.core.Document;
import jbenchmarker.core.Operation;

/**
 * An instance of Treedoc document, implemented as a binary-like tree.
 * 
 * @author mzawirski
 */
public class TreedocDocument implements Document {
	final TreedocRoot rootNode;

	public TreedocDocument(final UniqueTagGenerator tagGenerator) {
		rootNode = new TreedocRoot(tagGenerator);
	}

	@Override
	public String view() {
		return rootNode.getContent();
	}

	@Override
	public void apply(Operation op) {
		final TreedocOperation treedocOp = (TreedocOperation) op;
		switch (treedocOp.getType()) {
		case ins:
			rootNode.insertAt(treedocOp.getId(), treedocOp.getContent());
			break;
		case del:
			rootNode.deleteAt(treedocOp.getId());
			break;
		default:
			throw new IllegalArgumentException("Unsupported operation type");
		}
	}

	TreedocRoot getRoot() {
		return rootNode;
	}
}