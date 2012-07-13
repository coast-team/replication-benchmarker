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
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author urso
 */
public abstract class AbstractNode<T> implements Node<T> {
    final protected T value;
    protected AbstractNode<T> father;

    public AbstractNode(T value, AbstractNode<T> father) {
        this.value = value;
        this.father = father;
    }
    
    abstract protected Collection<? extends AbstractNode<T>> getChildren();
    
    
    @Override
    public Collection<? extends Node<T>> getChildrenCopy() {
        return new LinkedList(getChildren());
    }
     
    @Override
    public int getChildrenNumber() {
        return getChildren().size();
    }
    
    @Override
    public boolean isChildren(Node<T> n) {
        return getChildren().contains((NodeImpl)n);
        /* TODO : Possible qu'un this.children.containsKey(n.getValue()) 
         * serait plus rapide*/
    }
    
    @Override
    public Iterator<? extends AbstractNode<T>> getChildrenIterator() {
        // TODO : make unmutable iterator
        return getChildren().iterator();
    }

    @Override
    public Node<T> getFather() {
        return father;
    }

    public List<T> getPath() {
        LinkedList<T> p = new LinkedList();
        AbstractNode<T> n = this;
        while (n != null) {
            if (n.value != null) {
                p.addFirst(n.value);
            }
            n = n.father;
        }
        return p;
    }

    @Override
    public Node<T> getRoot() {
        AbstractNode<T> n = this;
        while (n.father != null) {
            n = n.father;
        }
        return n;
    }

    @Override
    public T getValue() {
        return value;
    }    
}
