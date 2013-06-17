/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker.woot.wooth.undo;

import jbenchmarker.woot.WootIdentifier;
import jbenchmarker.woot.wooth.LinkedNode;

/**
 * A wooth node with a visibility counter.
 * @author urso
 */
public class WootUndoNode<T> extends  LinkedNode<T> {
    private int visibility;
    
    // TODO : refactor with adequate interface
    public WootUndoNode(WootIdentifier id, T content, LinkedNode<T> next, int degree, int visibility) {
        super(id, content, next, degree);
        this.visibility = visibility;
    }

    @Override
    public boolean isVisible() {
        return visibility > 0;
    }

    int getVisibility() {
        return visibility;
    }

    void changeVisibility(int visibility) {
        this.visibility += visibility;
    }  
}
