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

import citi.treedoc.TreedocId;
import jbenchmarker.core.SequenceMessage;
import jbenchmarker.trace.TraceOperation;

/**
 * 
 * @author mzawirski
 */
public class TreedocOperation extends SequenceMessage {
	private final TreedocId id;
	private final char content;

	public TreedocOperation(TraceOperation o, TreedocId id) {
		super(o);
		this.id = id;
		this.content = (char) 0;
	}

	public TreedocOperation(TraceOperation o, TreedocId id, char content) {
		super(o);
		this.id = id;
		this.content = content;
	}

	public TreedocId getId() {
		return id;
	}

	public char getContent() {
		return content;
	}

	@Override
	public SequenceMessage copy() {
		return new TreedocOperation(getOriginalOp(), new TreedocId(id), content);
	}
}
