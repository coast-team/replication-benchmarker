/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.tree.orderedtree;

import collect.OrderedNode;
import crdt.CRDTMessage;
import crdt.OperationBasedMessagesBag;
import java.util.ArrayList;
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
    public int childrenNumber() {
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
        if (childrenNumber() != other.childrenNumber()) {
            return false;
        }
        for (int i = 0; i < childrenNumber(); ++i) {
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
        for(int i=0;i<mock.childrenNumber();i++){
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
    
   
}
