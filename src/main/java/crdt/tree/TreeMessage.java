/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.tree;

import crdt.CRDT;
import crdt.CRDTMessage;
import crdt.set.SetOperation;
import java.util.HashSet;
import java.util.Set;


/**
 *
 * @author score
 */
@Deprecated
public class TreeMessage implements CRDTMessage {
    Set<CRDTMessage> operations;

    @Override
    public CRDTMessage concat(CRDTMessage msg) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public CRDTMessage clone() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void execute(CRDT crdt) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public enum TreeOperationType {add, del}; 
    private TreeOperationType type;

    public TreeMessage(TreeOperationType type, CRDTMessage operation) {
        this.operations = new HashSet<CRDTMessage>(1);       
        this.operations.add(operation);
        this.type = type;
    }
    
    public TreeMessage(TreeOperationType type, Set<CRDTMessage> operations) {
        this.operations = operations;
        this.type = type;
    }

    public TreeMessage(TreeOperationType type) {
        this.operations = new HashSet<CRDTMessage>();
        this.type = type;
    }
    
    public TreeOperationType getType() {
        return type;
    }

    @Override
    public int size() {
        return -1;
    }
}
