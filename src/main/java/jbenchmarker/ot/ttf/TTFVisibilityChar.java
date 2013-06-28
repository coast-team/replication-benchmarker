/**
 * Replication Benchmarker
 * https://github.com/score-team/replication-benchmarker/
 * Copyright (C) 2013 LORIA / Inria / SCORE Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package jbenchmarker.ot.ttf;

import java.io.Serializable;

/**
 * This is TTF character: couple of element and visibility flag.
 * @param <T>  Character
 * @author oster
 */
public class TTFVisibilityChar<T> implements TTFChar<T>{

    private T character;
    private boolean visible;

    /**
     * Make new visible character 
     * @param c character
     */
    public TTFVisibilityChar(T c) {
        this.character = c;
        this.visible = true;
    }

    /**
     * check the visibility of character
     * @return true if visible
     */
    @Override
    public boolean isVisible() {
        return this.visible;
    }

    /**
     * hide the character
     */
    public void hide() {
        this.visible = false;
    }

    /**
     * return the current character
     * @return character
     */
    @Override
    public T getContent() {
        return this.character;
    }

    /**
     * this is string representation of element 
     * The { character } is invisible.
     * @return string
     */
    @Override
    public String toString() {
        if (this.visible) {
            return "" + this.character;
        } else {
            return "[" + this.character + "]";
        }
    }
}
