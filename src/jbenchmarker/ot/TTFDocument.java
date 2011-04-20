/**
 *   This file is part of ReplicationBenchmark.
 *
 *   ReplicationBenchmark is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   ReplicationBenchmark is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with ReplicationBenchmark.  If not, see <http://www.gnu.org/licenses/>.
 *
 **/

package jbenchmarker.ot;

import java.util.ArrayList;
import java.util.List;
import jbenchmarker.core.Document;
import jbenchmarker.core.Operation;
import jbenchmarker.trace.TraceOperation;

/**
 *
 * @author oster
 */
public class TTFDocument implements Document {

    protected List<TTFChar> model;

    public TTFDocument() {
        this.model = new ArrayList<TTFChar>();
    }

    @Override
    public String view() {
        StringBuilder sb = new StringBuilder();
        for (TTFChar c : this.model) {
            if (c.isVisible()) {
                sb.append(c.getChar());
            }
        }
        return sb.toString();
    }

    @Override
    public void apply(Operation op) {
        TTFOperation oop = (TTFOperation) op;
        int pos = oop.getPosition();

        if (oop.getType() == TraceOperation.OpType.del) {
            TTFChar c = this.model.get(pos);
            c.hide();
        } else {
            this.model.add(pos, new TTFChar(oop.getChar()));
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
}
