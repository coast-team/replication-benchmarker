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
import java.util.Iterator;
import java.util.Iterator;


/**
 *
 * @param <T> 
 * @author Stephane Martin
 */
public interface SimpleNode<T> extends Serializable, Iterable  {
    
    /**
     * 
     * @return Node value
     */
    public T getValue();
    
    /**
     * 
     * @return Father node.
     */
    public SimpleNode<T> getFather();
    
    
    
    /**
     * 
     * @return an iterator of Children
     */
    @Override
    public Iterator<? extends SimpleNode <T>> iterator();

    
    /**
     * 
     * @return number of children
     */
    public int getChildrenNumber();
    
    /**
     * Check if n is directly children of this node
     * @param n
     * @return true if n is childre of this
     */
    public boolean isChildren(SimpleNode<T> n);
    
  
}
