/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker.logoot;

import collect.HashVectorWithHoles;
import collect.VectorWithHoles;
import jbenchmarker.core.Operation;
import jbenchmarker.core.SequenceMessage;

/**
 * Logoot that support operation delivered in non-causal order.
 * @author urso
 */
public class NonCausalLogoot<T> extends LogootDocument<T> {

    VectorWithHoles seen;
    
    public NonCausalLogoot(int r, LogootStrategy strategy) {
        super(r, strategy);
        seen = new HashVectorWithHoles();
    }

    @Override
    public void apply(Operation op) {
        LogootOperation lg = (LogootOperation) op;
        ListIdentifier id = lg.getIdentifiant();
        int r = id.replica(), h = id.clock();
        if (lg.getType() == SequenceMessage.MessageType.del || !seen.contains(r, h)) {
            super.apply(op);
        }
        seen.add(r, h);
    }
    
    
}
