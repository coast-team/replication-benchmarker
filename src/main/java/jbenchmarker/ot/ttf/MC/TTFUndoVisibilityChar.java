/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker.ot.ttf.MC;

import jbenchmarker.ot.ttf.TTFVisibilityChar;

/**
 *
 * @author score
 */
public class TTFUndoVisibilityChar<T> extends TTFVisibilityChar<T> {

    private int visibility;

    TTFUndoVisibilityChar(T c, int visibility) {
        super(c);
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
