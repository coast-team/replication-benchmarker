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
package jbenchmarker.ot.ttf.MC;

import jbenchmarker.ot.ttf.TTFChar;

/**
 * A Char with a visibility degree.
 * @author urso
 */
public class TTFUndoVisibilityChar<T> implements TTFChar<T> {
    private final T character;
    private int visibility;

    public TTFUndoVisibilityChar(T character) {
        this.character = character;
        this.visibility = 1;
    }

    @Override
    public boolean isVisible() {
        return visibility > 0;
    }

    int getVisibility() {
        return visibility;
    }

    @Override
    public T getContent() {
        return character;
    }
    
    void changeVisibility(int visibility) {
        this.visibility += visibility;
    }
}
