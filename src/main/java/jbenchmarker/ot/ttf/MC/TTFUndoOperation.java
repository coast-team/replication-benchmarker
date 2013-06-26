/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker.ot.ttf.MC;

import jbenchmarker.core.SequenceOperation.OpType;
import jbenchmarker.ot.ttf.TTFOperation;

/**
 *
 * @author score
 */
public class TTFUndoOperation<T> extends TTFOperation<T> {
    private final int visibility;
    
     public TTFUndoOperation(int pos, int siteId, int visibility) {
        super(OpType.undo, pos, siteId);
        this.visibility = visibility;
    }
    
    public int getVisibility() {
        return visibility;
    }
    
    @Override
    public TTFUndoOperation<T> clone() {
        return new TTFUndoOperation(this.getPosition(), this.getSiteId(), visibility);
    }
}
