/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.set.lastwriterwins;
import crdt.OperationBasedOneMessage;
import crdt.set.CommutativeSetMessage;

/**
 *
 * @author score
 */
public class LwwMessage<T> extends TypedMessage<T> {
    
    private int now;
    
    public LwwMessage(OpType type, T t, int now) {
        super(type, t);
        this.now = now;
    }
        
    public int getime() {
        return this.now;
    }
    
    public void settime(int t) {
        this.now = t;
    }

    @Override
    public String toString() {
        return "LM{" + "now=" + now + ", type=" + type + ", content=" + content + '}';
    }

    @Override
    public LwwMessage<T> clone() {
        return new LwwMessage(type, content, now);
    }
}
