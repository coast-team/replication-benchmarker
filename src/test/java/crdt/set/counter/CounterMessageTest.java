/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.set.counter;
import crdt.set.counter.CounterMessage;
import crdt.set.SetOperation.OpType;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author score
 */
public class CounterMessageTest<T> {
    
     @Test
    public void CounterOperationTest()
    {       
         CounterMessage cOp1 = new CounterMessage('a', 1);
         CounterMessage cOp2 = new CounterMessage("TestOperation",2);
         CounterMessage cOp3 = new CounterMessage('a', 3);
         
         assertEquals(cOp1.getCounter(), 1);
         assertEquals(cOp2.getCounter(), 2);
         assertEquals(cOp3.getCounter(), 3);
         
         assertEquals(cOp1.getContent(), 'a');
         assertEquals(cOp2.getContent(), "TestOperation");
         assertEquals(cOp3.getContent(), 'a');
    }
    
}
