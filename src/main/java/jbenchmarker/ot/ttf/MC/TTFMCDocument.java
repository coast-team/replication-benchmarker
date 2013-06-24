/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker.ot.ttf.MC;

import crdt.Operation;
import java.util.ArrayList;
import jbenchmarker.core.SequenceOperation;
import jbenchmarker.ot.ttf.TTFChar;
import jbenchmarker.ot.ttf.TTFDocument;
import jbenchmarker.ot.ttf.TTFOperation;
import jbenchmarker.ot.ttf.TTFVisibilityChar;

/**
 *
 * @author mehdi
 */
public class TTFMCDocument<T> extends TTFDocument<T> {
    
        /**
     * Make new TTF document
     */
    public TTFMCDocument() {
        this.model = new ArrayList<TTFChar<T>>();
    }
    
    /*
     * Apply an operation to document.
     */
    @Override
    public void apply(Operation op) {
        TTFOperation oop = (TTFOperation) op;
        int pos = oop.getPosition();
        
        if (oop.getType() == SequenceOperation.OpType.noop) {
            //this.model.add(pos, new TTFVisibilityChar(oop.getChar()));
            TTFVisibilityChar c = (TTFVisibilityChar) this.model.get(pos);
            c.hide();
        }
        if (oop.getType() == SequenceOperation.OpType.delete) {
            TTFVisibilityChar c = (TTFVisibilityChar) this.model.get(pos);
            if (c.isVisible()) {
                decSize();
            }
            c.hide();
        } else {
            this.model.add(pos, new TTFVisibilityChar(oop.getChar()));
            incSize();
        }
    }
    
}
