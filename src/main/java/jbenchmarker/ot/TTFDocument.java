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
package jbenchmarker.ot;

import java.util.ArrayList;
import java.util.List;
import jbenchmarker.core.Document;
import jbenchmarker.core.SequenceMessage;
import jbenchmarker.core.SequenceOperation;

/**
 *
 * @author oster
 */
public class TTFDocument<T> implements Document {

    protected List<TTFChar<T>> model;
    private int size = 0;

    public TTFDocument() {
        this.model = new ArrayList<TTFChar<T>>();
    }

    public String view() {
        StringBuilder sb = new StringBuilder();
        for (TTFChar c : this.model) {
            if (c.isVisible()) {
                sb.append(c.getChar());
            }
        }
        return sb.toString();
    }
    
    public String extendedView() {
        StringBuilder sb = new StringBuilder();
        for (TTFChar c : this.model) {
            if (c.isVisible()) {
                sb.append(c.getChar());
            } else {
                sb.append("[").append(c.getChar()).append("]");
            }
        }
        return sb.toString();
    }

    @Override
    public void apply(SequenceMessage op) {
        TTFOperation oop = (TTFOperation) op;
        int pos = oop.getPosition();

        if (oop.getType() == SequenceOperation.OpType.del) {
            TTFChar c = this.model.get(pos);
            if (c.isVisible()) { --size; }
            c.hide();
        } else {      
            this.model.add(pos, new TTFChar(oop.getChar()));
            ++size;
        }
    }

    public TTFChar getChar(int pos) {
        return this.model.get(pos);
    }

    public int viewToModel(int positionInView) {
        int positionInModel = 0;
        int visibleCharacterCount = 0;

        while (positionInModel < this.model.size() && (visibleCharacterCount < positionInView || (!this.model.get(positionInModel).isVisible()))) {
            if (this.model.get(positionInModel).isVisible()) {
                visibleCharacterCount++;
            }
            positionInModel++;
        }

        /*
        while (positionInModel < this.model.size() && (visibleCharacterCount < positionInView)) {
            if (this.model.get(positionInModel).isVisible()) {
                visibleCharacterCount++;
            }
            positionInModel++;
        }
        while (positionInModel < this.model.size() && (!this.model.get(positionInModel).isVisible())) {
            positionInModel++;
        }
         */

        return positionInModel;
    }

//    @Override
    public int viewLength() {
        return size;
    }
}
