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

import crdt.Factory;
import jbenchmarker.core.SequenceOperation;
import jbenchmarker.core.SequenceOperation.OpType;
import jbenchmarker.ot.soct2.OTAlgorithm;
import jbenchmarker.ot.ttf.TTFDocument;
import jbenchmarker.ot.ttf.TTFMergeAlgorithm;
import jbenchmarker.ot.ttf.TTFOperation;

/**
 * A TTF merge algorithm that consider only visibility of element. 
 * Deletes and reappear are undo operation.
 * @author urso
 */
public class TTFUndoMergeAlgorithm extends TTFMergeAlgorithm<TTFOperation> {
    
    public TTFUndoMergeAlgorithm(TTFDocument doc, int siteId, Factory<OTAlgorithm<TTFOperation>> otAlgo){
        super(doc, siteId, otAlgo);
    }

    protected int getVisibility(int pos) {
        return ((TTFUndoVisibilityChar) getDoc().getChar(pos)).getVisibility();
    }
    
    @Override
    protected TTFOperation deleteOperation(int pos) {
        return new TTFOperation(OpType.undo, pos, -getVisibility(pos));
    }

    @Override
    protected TTFOperation insertOperation(int pos, Object content) {
        if (!getDoc().getChar(pos).isVisible() && getDoc().getChar(pos).getContent().equals(content)) {
            return new TTFOperation(OpType.undo, pos, 1 - getVisibility(pos));
        } else {
            return new TTFOperation(OpType.insert, pos, content);
        }
    }
}
