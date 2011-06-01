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

    public String view() {
        StringBuilder sb = new StringBuilder();
        for (TTFChar c : this.model) {
            if (c.isVisible()) {
                sb.append(c.getChar());
            }
        }
        return sb.toString();
    }

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
