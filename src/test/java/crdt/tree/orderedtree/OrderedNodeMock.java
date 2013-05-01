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
package crdt.tree.orderedtree;

import collect.OrderedNode;
import collect.SimpleNode;
import crdt.CRDTMessage;
import crdt.OperationBasedMessagesBag;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author urso
 */
public class OrderedNodeMock<T> implements OrderedNode<T> {
    final List<OrderedNodeMock<T>> children = new ArrayList<OrderedNodeMock<T>>();
    final T value;

    public OrderedNodeMock(T value) {
        this.value = value;
    }
    
    @Override
    public int getChildrenNumber() {
        return children.size(); 
    }

    public List<OrderedNodeMock<T>> getChildrens() {
        return children;
    }

    @Override
    public OrderedNodeMock<T> getChild(int p) {
        return children.get(p);
    }

   

    @Override
    public T getValue() {
        return value;
    }

    @Override
    public List<? extends OrderedNode<T>> getElements() {
        return children;
    }

    @Override
    public OrderedNode<T> createNode(T elem) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

   /* @Override
    public boolean same(OrderedNode<T> other) {
        if (other == null) {
            return false;
        }
        if (this.value != other.getValue() && (this.value == null || !this.value.equals(other.getValue()))) {
            return false;
        }
        if (getChildrenNumber() != other.getChildrenNumber()) {
            return false;
        }
        for (int i = 0; i < getChildrenNumber(); ++i) {
            if (!getChild(i).same(other.getChild(i))) {
                return false;
            }
        }
        return true;
    }*/

    static <T> OrderedNodeMock<T> tree(T ch, Object... cn) {
        OrderedNodeMock on = new OrderedNodeMock(ch);
        for (Object c : cn) {
            if (c instanceof OrderedNode) {
                on.children.add(c);
            } else {
                on.children.add(new OrderedNodeMock(c));
            }
        }
        return on;
    }
    static CRDTMessage makeOrderedTreeByMock(OrderedNodeMock mock, CRDTOrderedTree tree)throws Exception{
        return  makeOrderedTreeByMock(mock,tree,new LinkedList<Integer>());
        
    } 
    static private CRDTMessage makeOrderedTreeByMock(OrderedNodeMock mock, CRDTOrderedTree tree,LinkedList<Integer> path)throws Exception {
        CRDTMessage mess=new OperationBasedMessagesBag();
        for(int i=0;i<mock.getChildrenNumber();i++){
            mess=mess.concat(tree.add(path, i, mock.getChild(i).getValue()));
            path.addLast(i);
            mess=mess.concat(makeOrderedTreeByMock(mock.getChild(i),tree,path));
            path.removeLast();
        }
        return mess;
    }
    @Override
    public String toString() {
        return value + "{" + getElements() + '}';
    }

    @Override
    public void setReplicaNumber(int replicaNumber) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public SimpleNode<T> getFather() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Iterator<? extends SimpleNode<T>> iterator() {
        return this.getElements().iterator();
    }

    @Override
    public boolean isChildren(SimpleNode<T> n) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
   
}
