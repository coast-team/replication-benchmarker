/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.tree.orderedtree;

import collect.OrderedNode;
import crdt.CRDT;
import jbenchmarker.core.Operation;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import jbenchmarker.core.LocalOperation;

/**
 *
 * @author score
 */
public class OrderedTreeOperation<T> implements LocalOperation {

    @Override
    public Operation clone() {
        try {
            return (Operation) super.clone();
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(OrderedTreeOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public LocalOperation adaptTo(CRDT replica) {
        //CRDTOrderedTree tree=(CRDTOrderedTree)replica;
        OrderedNode node = (OrderedNode) replica.lookup();
        int i = 0;
        List<Integer> nPath = path;
        int nPos=this.position;
        for (Integer pNext : this.path) {
            if (node.childrenNumber() < pNext) {
                nPath = this.path.subList(0, i);
                break;
            }
            node = node.getChild(pNext);
            i++;
        }
        if (this.type == OpType.add) {
            if (nPos>node.childrenNumber()){
                nPos=node.childrenNumber();
            }
            return new OrderedTreeOperation(nPath,nPos,this.content);
            
        }else{
            return new OrderedTreeOperation(nPath);
        }
    }

    public enum OpType {

        add, del
    };
    final private OpType type;
    final private List<Integer> path;
    final private int position;
    final private T content;

    // Add operation
    public OrderedTreeOperation(List<Integer> path, int p, T elem) {
        this.type = OpType.add;
        this.path = path;
        this.position = p;
        this.content = elem;
    }

    // Del operation
    public OrderedTreeOperation(List<Integer> path) {
        this.type = OpType.del;
        this.path = path;
        this.position = 0;
        this.content = null;
    }

    public OpType getType() {
        return type;
    }

    public T getContent() {
        return content;
    }

    public int getPosition() {
        return position;
    }

    public List<Integer> getPath() {
        return path;
    }

    @Override
    public String toString() {
        return "OrderedTreeOperation{" + "type=" + type + ", path=" + path + ", position=" + position + ", content=" + content + '}';
    }
}
