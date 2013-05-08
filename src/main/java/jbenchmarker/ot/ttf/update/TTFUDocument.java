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
package jbenchmarker.ot.ttf.update;

import jbenchmarker.ot.ttf.*;
import java.util.ArrayList;
import java.util.List;
import jbenchmarker.core.Document;
import jbenchmarker.core.Operation;
import jbenchmarker.core.SequenceOperation;

/**
 * This is TTF document sequence of character
 *
 * @param <T> Type of character
 * @author urso
 */
public class TTFUDocument<T> extends TTFDocument<T> {

    /**
     * Make new TTF document
     */
    public TTFUDocument() {
        this.model = new ArrayList<TTFChar<T>>();
    }

    /*
     * Apply an operation to document.
     */
    @Override
    public void apply(Operation op) {
        TTFOperation<T> oop = (TTFOperation<T>) op;
        int pos = oop.getPosition();

            if (oop.getType() == SequenceOperation.OpType.update) {
                TTFUChar c = (TTFUChar) this.model.get(pos);
                if (c.isVisible() && oop.getChar() == null) {
                    decSize();
                }
                c.set(oop.getChar(), oop.getSiteId());
            } else if (oop.getType() == SequenceOperation.OpType.insert) { 
                this.model.add(pos, new TTFUChar<T>(oop.getChar(), oop.getSiteId()));
                incSize();
            }
        }
}
