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
package jbenchmarker.ot.ttf;

import java.util.ArrayList;
import java.util.List;
import jbenchmarker.core.Document;
import crdt.Operation;
import jbenchmarker.core.SequenceOperation;

/**
 * This is TTF document sequence of character
 *
 * @param <T> Type of character
 * @author oster urso
 */
public class TTFDocument<T> implements Document {

    /**
     * This is list of characters
     */
    protected List<TTFChar<T>> model;
    private int size = 0;

    /**
     * Make new TTF document
     */
    public TTFDocument() {
        this.model = new ArrayList<TTFChar<T>>();
    }
    
    public List<TTFChar<T>> getModel() {
        return model;
    }

    /**
     * Return document without invisible character
     *
     * @return return edited document
     */
    @Override
    public String view() {
        StringBuilder sb = new StringBuilder();
        for (TTFChar c : this.model) {
            if (c.isVisible()) {
                sb.append(c.getContent());
            }
        }
        return sb.toString();
    }

    /**
     * return representation with invisible character (for debug)
     *
     * @return representation string
     */
    public String extendedView() {
        StringBuilder sb = new StringBuilder();
        for (TTFChar c : this.model) {
            sb.append(c);
        }
        return sb.toString();
    }

    protected void incSize() {
        ++size;
    }

    protected void decSize() {
        --size;
    }
    
    /*
     * Apply an operation to document.
     */
    @Override
    public void apply(Operation op) {
        TTFOperation oop = (TTFOperation) op;
        int pos = oop.getPosition();

        if (oop.getType() == SequenceOperation.OpType.delete) {
            TTFVisibilityChar c = (TTFVisibilityChar) this.model.get(pos);
            if (c.isVisible()) {
                decSize();
            }
            c.hide();
        } else {
            this.model.add(pos, new TTFVisibilityChar(oop.getContent()));
            incSize();
        }
    }

    /**
     * get character from absolute position. invisible character count.
     *
     * @param pos position
     * @return character
     */
    public TTFChar getChar(int pos) {
        return this.model.get(pos);
    }

    /**
     * Transform an position in text without invisible character to position
     * with invisible character
     *
     * @param positionInView
     * @return position in model
     */
    public int viewToModel(int positionInView) {
        int positionInModel = 0;
        int visibleCharacterCount = 0;

        while (positionInModel < this.model.size() && visibleCharacterCount < positionInView) {
            if (this.model.get(positionInModel).isVisible()) {
                visibleCharacterCount++;
            }
            positionInModel++;
        }
        return positionInModel;
    }

    /**
     *
     * @return sise of document without invisible character
     */
    @Override
    public int viewLength() {
        return size;
    }
}
