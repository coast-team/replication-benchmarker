/**
 * Replication Benchmarker
 * https://github.com/score-team/replication-benchmarker/
 * Copyright (C) 2011 INRIA / LORIA / SCORE Team
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
package jbenchmarker.woot;

import java.io.Serializable;

/**
 *
 * @author urso
 */
public abstract class WootNode implements Serializable{ 
    
    final private WootIdentifier id; // own identifier
    final private char content;
    private boolean visible;

    public WootNode(WootIdentifier id, char content, boolean visible) {
        this.id = id;
        this.content = content;
        this.visible = visible;
    }

    public WootIdentifier getId() {
        return id;
    }

    public char getContent() {
        return content;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
