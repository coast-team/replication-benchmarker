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
package jbenchmarker.woot.wooth.undo;

import jbenchmarker.core.SequenceOperation.OpType;
import jbenchmarker.woot.WootId;
import jbenchmarker.woot.WootOperation;

/**
 * An undo operation for wooth.
 * @author urso
 */
public class WootUndo<T> extends WootOperation<T> {
    private final int visibility;
    
    // TODO : remove type and content.
    public WootUndo(WootId identifier, int visibility) {
        super(OpType.undo, identifier, null);
        this.visibility = visibility;
    } 

    public int getVisibility() {
        return visibility;
    }

    @Override
    public WootUndo clone() {
        return new WootUndo(identifier.clone(), visibility);
    }
}
