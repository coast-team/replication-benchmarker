/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.set.lastwriterwins;
import crdt.CommutativeMessage;
import crdt.set.CommutativeSetMessage;

/**
 *
 * @author score
 */
public class LwwMessage<T> extends CommutativeSetMessage<T> {
    
    private int now;
   
    public enum OpType {add, del}; 
    private OpType type;
   
    public OpType getType() {
        return type;
    }
    
    public LwwMessage(OpType type, T t, int now) {
        this.type=type;
        this.now = now;
        this.content = t;
    }
        
    public int getime() {
        return this.now;
    }
    
    public void settime(int t) {
        this.now = t;
    }

    @Override
    public String visu() {
        return "LM{" + "now=" + now + ", type=" + type + ", content=" + content + '}';
    }

    @Override
    protected CommutativeMessage copy() {
        return new LwwMessage(type, content, now);
    }
}