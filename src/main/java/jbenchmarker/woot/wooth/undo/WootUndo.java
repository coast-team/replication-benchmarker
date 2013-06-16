/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker.woot.wooth.undo;

import jbenchmarker.core.SequenceOperation;
import jbenchmarker.core.SequenceOperation.OpType;
import jbenchmarker.woot.WootOperation;

/**
 * An undo operation for wooth.
 * @author urso
 */
public class WootUndo<T> extends WootOperation<T> {
    private final int visibility;
    
    // TODO : remove type and content.
    public WootUndo(SequenceOperation o, Cloneable identifier, int visibility) {
        super(null, identifier, null);
        this.visibility = visibility;
    } 

    public int getVisibility() {
        return visibility;
    }
}
