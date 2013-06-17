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
package jbenchmarker.woot.wooth;

import jbenchmarker.woot.WootIdentifier;

/**
 * A linked woot node with visibility flag?
 * @author urso
 */
public class WootHashNode<T> extends LinkedNode<T> {

    private boolean visible;
    
    public WootHashNode(WootIdentifier id, T content, boolean visible, LinkedNode<T> next, int degree) {
        super(id, content, next, degree);
        this.visible = visible;
    }

    @Override
    public boolean isVisible() {
        return visible;
    }

    void setVisible(boolean b) {
        visible = b;
    }
}
