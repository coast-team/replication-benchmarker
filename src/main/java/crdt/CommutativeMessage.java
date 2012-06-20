/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt;

import jbenchmarker.core.Operation;

/**
 *
 * @author urso
 */
public abstract class CommutativeMessage<T> implements CRDTMessage, Cloneable,Operation {
    //private LinkedList<CommutativeMessage<T>> msgs = new LinkedList<CommutativeMessage<T>>();

    /*public LinkedList<CommutativeMessage<T>> getMsgs() {
        return msgs;
    }*/
    
    @Override
    public CRDTMessage concat(CRDTMessage msg){
       return new OperationBasedMessages(this,(CommutativeMessage)msg);
   }
    
   /* @Override
    public CommutativeMessage concat(CRDTMessage msg) {
        CommutativeMessage cmsg = (CommutativeMessage) msg;
        msgs.add(cmsg);
        msgs.addAll(cmsg.msgs);
        return this;
    } */
    
    @Override
    abstract public CommutativeMessage clone();

    /*@Override
    public String toString() {
        StringBuilder s = new StringBuilder(toString());
        for (CommutativeMessage m : msgs) {
            s.append(" + ").append(m.toString());
        }
        return s.toString();
    }*/

    //abstract protected String toString();
    
    //abstract protected CommutativeMessage clone();

    @Override
    public int size() {
        return 1;
    }
    @Override
    public void execute(CRDT cmrdt){
        cmrdt.applyOneRemote(this);   
    }
}
