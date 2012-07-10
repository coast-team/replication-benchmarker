/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.set;

import crdt.CRDT;
import crdt.tree.orderedtree.OrderedTreeOperation;
import java.util.logging.Level;
import java.util.logging.Logger;
import jbenchmarker.core.Operation;
import jbenchmarker.core.LocalOperation;
/**
 *
 * @author score
 */
public class SetOperation<T> implements LocalOperation {

    @Override
    public LocalOperation adaptTo(CRDT replica) {
        //TODO: Correct Adaption 
        return this;
        /*if (this.type==OpType.del && !((CRDTSet)replica).contains(this.content)){
            
        }*/
    }
    
    public enum OpType {add, del}; 
    private OpType type;
    private T content;
    
    public SetOperation(OpType type, T obj) {
        this.type = type;
        this.content = obj;
    }

    public OpType getType() {
        return type;
    }
    
    public T getContent() {
        return content;
    }

    @Override
    public String toString() {
        return "SetOperation{" + "type=" + type + ", content=" + content + '}';
    }
      @Override
    public Operation clone() {
        try {
            return (Operation) super.clone();
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(OrderedTreeOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
}
