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
package crdt.tree.orderedtree;

import collect.OrderedNode;

/**
 *
 * @author Stephane Martin <stephane.martin@loria.fr>
 */
public interface PositionnedNode<T> extends OrderedNode<T> {
    
    
    
    /**
     * Gets the child of this node with this position identifier
     * @param p a poisitioned element
     * @return children Node at this position
     */
    OrderedNode<T> getChild(Positioned<T> p);
       
    /**
     * Gets the position identifier of the pth element
     * @return the position identifier
     *
     */
    Positioned<T> getPositioned(int p);
        
    /**
     * Gets a position identifier for a new element at the pth position
     * @param p natural postion
     * @param element content
     * @return a new position indentifier
     */
    PositionIdentifier getNewPosition(int p, T element);
    
    /**
     * Adds an element with this position identifier
     * @param pi position identifier
     * @param element content 
     */
    void add(PositionIdentifier pi, T element);
        
    /**
     * Removes an element with this position identifier
     * @param pi position identifier
     * @param element content 
     */
    void remove(PositionIdentifier pi, T element);
    
}
