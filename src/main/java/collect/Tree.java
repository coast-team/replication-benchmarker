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
package collect;

import java.util.Iterator;
import java.util.List;

/**
 *
 * @param <T> 
 * @author Stephane Martin
 */
public interface Tree<T> {
    /**
     * Adds an element in the tree 
     * @param father a node of the tree, null for root
     * @param t the element to add
     * @return the created node
     */
    public Node<T> add(Node<T> father, T t)  ;
    
    /**
     * Creates a node without father
     * @param t the element of the node
     * @return the created node
     */
    public Node<T> createOrphan(T t)  ;

    /**
     * Deletes a subtree in the tree.
     * @param node the root of the subtree.
     */
    public void remove(Node<T> node);
    
    /**
     * Moves a node t under node father.
     * @param father
     * @param node
     */
    public void move(Node<T> father, Node <T> node);
    /**
     * Creates an iterator traversing the tree in BFS order 
     * @param node a node in the tree (null for root)
     * @return the iterator
     */
    public Iterator<? extends Node<T>> getBFSIterator(Node<T> node);

    /**
     * Creates an iterator traversing the tree in DFS order 
     * @param node a node in the tree (null for root)
     * @return the iterator
     */
    public Iterator<? extends Node<T>> getDFSIterator(Node<T> node);

    /**
     * The root of the tree 
     * @return the root of the tree 
     */
    public Node<T> getRoot();
    
    /**
     * A node in the tree at a path
     * @param path the path to the node
     * @return a node at this path
     */
    public Node<T> getNode(List<T> path);
    
    /**
     * Says if the node belong to the tree.
     * @param n the node
     * @return true iff the node belong to the tree.
     */
    public boolean contains(Node<T> n);

    /**
     * Removes all nodes except root.
     */
    public void clear();
}
