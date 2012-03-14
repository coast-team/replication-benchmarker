/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt;

import crdt.CRDTMessage;
import java.util.LinkedList;

/**
 *
 * @author urso
 */
public abstract class CommutativeMessage<T> implements CRDTMessage, Cloneable {
    private LinkedList<CommutativeMessage<T>> msgs = new LinkedList<CommutativeMessage<T>>();

    public LinkedList<CommutativeMessage<T>> getMsgs() {
        return msgs;
    }
    
    @Override
    public CommutativeMessage concat(CRDTMessage msg) {
        CommutativeMessage cmsg = (CommutativeMessage) msg;
        msgs.add(cmsg);
        msgs.addAll(cmsg.msgs);
        return this;
    } 
    
    @Override
    public CommutativeMessage clone() {
        CommutativeMessage clone = copy();
        clone.msgs = (LinkedList<CommutativeMessage>) msgs.clone();
        return clone;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(visu());
        for (CommutativeMessage m : msgs) {
            s.append(" + ").append(m.visu());
        }
        return s.toString();
    }

    abstract protected String visu();
    
    abstract protected CommutativeMessage copy();
}
