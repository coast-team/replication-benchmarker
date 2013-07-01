/**
 * Replication Benchmarker
 * https://github.com/score-team/replication-benchmarker/ Copyright (C) 2013
 * LORIA / Inria / SCORE Team
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package jbenchmarker.ot.ttf.MC;

import crdt.Operation;
import jbenchmarker.core.SequenceOperation.OpType;
import jbenchmarker.ot.ttf.TTFDocument;
import jbenchmarker.ot.ttf.TTFOperation;

    
/**
 * A TTFdocument with visibility characters. 
 * Insert operation creates a 1 visibility character and undo shift visibility.
 * @author urso
 */
public class TTFUndoDocument<T> extends TTFDocument<T>{
    
    public TTFUndoDocument() {
        super();
    }
        
    /*
     * Apply an operation to document.
     */
    @Override
    public void apply(Operation op) {
        TTFOperation oop = (TTFOperation) op;
        int pos = oop.getPosition();

        if (oop.getType() == OpType.insert) {
            this.model.add(pos, new TTFUndoVisibilityChar(oop.getContent()));
            incSize();
        } else if (oop.getType() == OpType.undo) { // undo
            TTFUndoVisibilityChar c = (TTFUndoVisibilityChar) this.model.get(pos);
            boolean wasVisible = c.isVisible();
            c.changeVisibility((Integer) oop.getContent());
            if (!wasVisible && c.isVisible()) {
                incSize();
            } else if (wasVisible && !c.isVisible()) {
                decSize();
            }
        }
    }
}
