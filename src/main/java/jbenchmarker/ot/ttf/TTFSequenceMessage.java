/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker.ot.ttf;

import jbenchmarker.ot.soct2.SOCT2Message;
import jbenchmarker.core.SequenceMessage;
import jbenchmarker.core.SequenceOperation;
import jbenchmarker.core.SequenceOperation.OpType;

/**
 *
 * @author moi
 */
public class TTFSequenceMessage extends SequenceMessage {

    SOCT2Message soct2Message;

    public TTFSequenceMessage(SOCT2Message soct2Message, SequenceOperation o) {
        super(o);
        this.soct2Message = soct2Message;
    }

    public SOCT2Message getSoct2Message() {
        return soct2Message;
    }

    @Override
    public SequenceMessage copy() {
        return new TTFSequenceMessage(soct2Message.clone(), this.getOriginalOp());
    }

  
}
