/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.set.counter;
import crdt.OperationBasedOneMessage;
import crdt.set.CommutativeSetMessage;

/**
 *
 * @author score
 */
public class CounterMessage<T> extends CommutativeSetMessage<T> {

    private int counter;

    public CounterMessage(T t, int c) {
        super(t);
        this.counter = c;
    }
    
    public int getCounter() {
        return counter;
    }

    @Override
    public String toString() {
        return "CM{" + "elem=" + content + ", cont=" + counter + '}';
    }

    @Override
    public CounterMessage clone() {
        return new CounterMessage(this.content, this.counter);
    }

    @Override
    public OpType getType() {
        return (counter > 0) ? OpType.add : OpType.del; 
    }
}
