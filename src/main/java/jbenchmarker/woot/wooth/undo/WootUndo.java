/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker.woot.wooth.undo;

import jbenchmarker.core.SequenceOperation.OpType;
import jbenchmarker.woot.WootId;
import jbenchmarker.woot.WootOperation;

/**
 * An undo operation for wooth.
 * @author urso
 */
public class WootUndo<T> extends WootOperation<T> {
    private final int visibility;
    
    // TODO : remove type and content.
    public WootUndo(WootId identifier, int visibility) {
        super(OpType.undo, identifier, null);
        this.visibility = visibility;
    } 

    public int getVisibility() {
        return visibility;
    }

    @Override
    public WootUndo clone() {
        return new WootUndo(identifier.clone(), visibility);
    }
}
