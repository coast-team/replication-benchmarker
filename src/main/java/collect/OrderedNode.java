/**
 * Replication Benchmarker
 * https://github.com/score-team/replication-benchmarker/
 * Copyright (C) 2012 LORIA / Inria / SCORE Team
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
package collect;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author urso
 */
public interface OrderedNode<T> extends Serializable, SimpleNode<T> {
 /**
     * Gets the ith child of this node
     *
     * @param p
     * @return children Node at ith visible pos.
     */
    public OrderedNode<T> getChild(int p);

    /**
     * Gets all children in order
     *
     * @return the children
     */
    public List<? extends OrderedNode<T>> getElements();

    /**
     * Creates a new ordered node
     *
     * @param elem the content
     * @return the children
     */
    public OrderedNode<T> createNode(T elem);

    /**
     * Sets the replica number
     *
     * @param replicaNumber number
     */
    public void setReplicaNumber(int replicaNumber);
}
