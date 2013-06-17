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

import jbenchmarker.woot.WootIdentifier;
import jbenchmarker.woot.wooth.LinkedNode;

/**
 * A wooth node with a visibility counter.
 * @author urso
 */
public class WootUndoNode<T> extends  LinkedNode<T> {
    private int visibility;
    
    // TODO : refactor with adequate interface
    public WootUndoNode(WootIdentifier id, T content, LinkedNode<T> next, int degree, int visibility) {
        super(id, content, next, degree);
        this.visibility = visibility;
    }

    @Override
    public boolean isVisible() {
        return visibility > 0;
    }

    int getVisibility() {
        return visibility;
    }

    void changeVisibility(int visibility) {
        this.visibility += visibility;
    }  
}
