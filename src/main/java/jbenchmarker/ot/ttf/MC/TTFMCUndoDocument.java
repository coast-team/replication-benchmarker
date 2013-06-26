/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker.ot.ttf.MC;

import crdt.Operation;
import crdt.simulator.IncorrectTraceException;
import java.util.ArrayList;
import java.util.List;
import jbenchmarker.core.Document;
import jbenchmarker.core.SequenceOperation;

/**
 *
 * @author score
 */
public class TTFMCUndoDocument<T> extends TTFMCDocument<T>{
    
    public TTFMCUndoDocument() {
        super();
    }
        /*
     * Apply an operation to document.
     */
    @Override
    public void apply(Operation op) {
        if(op instanceof TTFUndoOperation){
            TTFUndoOperation uop = (TTFUndoOperation) op;
            undo(uop.getPosition(), uop.getVisibility());
        }
        else{
            super.apply(op);
        }
    }
    
    protected void undo(int pos, int visibility) {
        TTFUndoVisibilityChar<T> n = (TTFUndoVisibilityChar)this.model.get(pos);
        int v = n.getVisibility();
        n.changeVisibility(visibility);
        if (v <= 0 && n.getVisibility() > 0) {
            this.incSize();
        } else if (v > 0 && n.getVisibility() <= 0) {
            this.decSize();
        }
    }
    
    public Document create() {
        return new TTFMCDocument();
    }
}
