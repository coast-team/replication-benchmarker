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
public abstract class WootNode<T> implements Serializable{ 
    
    final private WootIdentifier id; // own identifier
    final private T content;
    private boolean visible;

    public WootNode(WootIdentifier id, T content, boolean visible) {
        this.id = id;
        this.content = content;
        this.visible = visible;
    }

    public WootIdentifier getId() {
        return id;
    }

    public T getContent() {
        return content;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    /**
     * Two invisible woot nodes are considered equal. 
     * @param obj
     * @return 
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final WootNode<T> other = (WootNode<T>) obj;
        if (!this.visible && !other.visible) {
            return true;
        }
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        if (this.content != other.content && (this.content == null || !this.content.equals(other.content))) {
            return false;
        }
        if (this.visible != other.visible) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 59 * hash + (this.content != null ? this.content.hashCode() : 0);
        hash = 59 * hash + (this.visible ? 1 : 0);
        return hash;
    }
}
