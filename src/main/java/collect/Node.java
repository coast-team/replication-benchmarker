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


/**
 *
 * @param <T> 
 * @author Stephane Martin
 */
public interface Node<T>  extends SimpleNode<T> {
    
    /**
     * 
     * @return Node value
     */
    @Override
    public T getValue();
    
    /**
     * 
     * @return Father node.
     */
    @Override
    public Node<T> getFather();
    
    /**
     * 
     * @return Father node.
     */
    public Node<T> getRoot();
    
    /**
     * 
     * @return Path to this node.
     */
    public List<T> getPath();
    
    /**
     * 
     * @return an iterator of Children
     */
    @Override
    public Iterator<?extends Node<T>> iterator();

    /**
     * 
     * @return a copy of Children
     */
    public Collection<?extends Node<T>> getChildrenCopy();

    /**
     * 
     * @return number of children
     */
    @Override
    public int getChildrenNumber();
    
    /**
     * Check if n is directly children of this node
     * @param n
     * @return true if n is childre of this
     */
    //public boolean isChildren(Node<T> n);
    @Override
    public boolean isChildren(SimpleNode<T> n);
    
    // Node<T> clone();
    
    /*
     * delete children of node
     * @param current: node father
     * @param node to delete
     */
    public void deleteChild( Collection<? extends Node<T>> nodeToDelet);
    /**
     * 
     * @return the level of this node on tree.
     */
    public int getLevel();
}
