/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker.jupiter;

import java.util.LinkedList;
import java.util.List;
import jbenchmarker.core.LocalOperation;
import jbenchmarker.core.Operation;
import jbenchmarker.core.SequenceOperation;
import jbenchmarker.core.SequenceOperation.OpType;

/**
 * Basic OT Sequence model. Only insert and delete operation. One operation per character.  
 * @author urso
 */
public class SequenceModel implements OTModel<String> {
    private final StringBuilder content = new StringBuilder();
    
    @Override
    public String lookup() {
        return content.toString();
    }

    @Override
    public List<OTOperation> generate(LocalOperation local) {
        SequenceOperation op = (SequenceOperation) local;
        List<OTOperation> ops = new LinkedList<OTOperation>();
        if (op.getType() == OpType.del || op.getType() == OpType.update) {
            for (int i = 0; i < op.getLenghOfADel(); ++i) {
                ops.add(new SequenceOTOperation(op.getPosition()));
            }
            content.delete(op.getPosition(), op.getPosition() + op.getLenghOfADel());
        }
        if (op.getType() == OpType.ins || op.getType() == OpType.update) {
            for (int i = 0; i < op.getContent().size(); ++i) {
                ops.add(new SequenceOTOperation(op.getPosition() + i, (Character) op.getContent().get(i)));
            }
            content.insert(op.getPosition(), op.getContentAsString()); 
        }
        return ops;
    }

    @Override
    public OTOperation transform(OTOperation msg, OTOperation op) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void apply(OTOperation msg) {
        SequenceOTOperation op = (SequenceOTOperation) msg;
        if (op.type == OpType.ins) {
            content.insert(op.position, op.content);
        } else {
            content.delete(op.position, op.position + 1);
        }
    }

    
    static class SequenceOTOperation implements OTOperation {
        int replicaNumber;
        int position;
        char content;
        OpType type;

        // A DEL
        SequenceOTOperation(int position) {
            this.position = position;
            this.type = OpType.del;
        }
        
        // AN INS 
        SequenceOTOperation(int position, char content) {
            this.position = position;
            this.content = content;
            this.type = OpType.ins;
        }

        private SequenceOTOperation(int replicaNumber, int position, char content, OpType type) {
            this.replicaNumber = replicaNumber;
            this.position = position;
            this.content = content;
            this.type = type;
        }

        @Override
        public Operation clone() {
            return new SequenceOTOperation(replicaNumber, position, content, type);
        }

        @Override
        public void setReplicaNumber(int replicaNumber) {
            this.replicaNumber = replicaNumber;
        }        
    }
}
