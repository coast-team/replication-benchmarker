/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.set.counter;
import crdt.CommutativeMessage;
import crdt.set.CommutativeSetMessage;

/**
 *
 * @author score
 */
public class CounterMessage<T> extends CommutativeSetMessage<T> {

    private int counter;

    public CounterMessage(T t, int c) {
        this.content = t;
        this.counter = c;
    }
    
    public int getCounter() {
        return counter;
    }

    @Override
    public String visu() {
        return "CM{" + "elem=" + content + ", cont=" + counter + '}';
    }

    @Override
    protected CommutativeMessage copy() {
        return new CounterMessage(this.content, this.counter);
    }
    
}
