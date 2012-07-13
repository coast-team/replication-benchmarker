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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


/**
 *
 * @param <T> 
 * @author Stephane Martin
 */
public interface Node<T> {
    
    /**
     * 
     * @return Node value
     */
    T getValue();
    
    /**
     * 
     * @return Father node.
     */
    Node<T> getFather();
    
    /**
     * 
     * @return Father node.
     */
    Node<T> getRoot();
    
    /**
     * 
     * @return Path to this node.
     */
    List<T> getPath();
    
    /**
     * 
     * @return an iterator of Children
     */
    Iterator<?extends Node<T>> getChildrenIterator();

    /**
     * 
     * @return a copy of Children
     */
    Collection<?extends Node<T>> getChildrenCopy();

    /**
     * 
     * @return number of children
     */
    int getChildrenNumber();
    
    /**
     * Check if n is directly children of this node
     * @param n
     * @return true if n is childre of this
     */
    boolean isChildren(Node<T> n);
    
    // Node<T> clone();
    
    /*
     * delete children of node
     * @param current: node father
     * @param node to delete
     */
    void deleteChild( Collection<? extends Node<T>> nodeToDelet);
    /**
     * 
     * @return the level of this node on tree.
     */
    int getLevel();
}
