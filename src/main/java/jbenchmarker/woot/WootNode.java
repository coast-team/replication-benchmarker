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
package jbenchmarker.woot;

import java.io.Serializable;

/**
 * A Woot node. Contains a content and an identifier.
 * @author urso
 */
public abstract class WootNode<T> implements Serializable {

    protected final T content;
    protected final WootIdentifier id; // own identifier

    public WootNode(WootIdentifier id, T content) {
        this.content = content;
        this.id = id;
    }

    /**
     * Two invisible woot nodes are considered equal.
     *
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
        WootNode other = (WootNode) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        if (this.content != other.content && (this.content == null || !this.content.equals(other.content))) {
            return false;
        }
        return true;
    }

    public T getContent() {
        return content;
    }

    public WootIdentifier getId() {
        return id;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 59 * hash + (this.content != null ? this.content.hashCode() : 0);
        return hash;
    }
    
    public abstract boolean isVisible();
}
