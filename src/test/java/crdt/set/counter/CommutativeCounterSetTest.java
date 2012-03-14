/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.set.counter;

import java.util.*;
import crdt.set.CrdtSetGeneric;
import crdt.PreconditionException;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author score
 */
public class CommutativeCounterSetTest<T> {
    CrdtSetGeneric tcs = new CrdtSetGeneric();
         
    @Test
    public void test() throws PreconditionException{        
         tcs.runTests(new CommutativeCounterSet());
    }
    
    @Test
    public void testConcurAddDel() throws PreconditionException{
         Set<T> s = tcs.testApplyConcurAddDel(new CommutativeCounterSet(), new CommutativeCounterSet());
         assertEquals(new HashSet(){{add('a');add('b');}}, s);
    }
    
    @Test
    public void testConcurAddThenDel() throws PreconditionException{
        Set<T> s = tcs.testApplyConcurAddThenDel(new CommutativeCounterSet(), new CommutativeCounterSet());
        assertEquals(new HashSet(){{add('a');}}, s);
    }
    
    @Test
    public void testRemoveAfterConcuAdd() throws PreconditionException{
        Set<T> s = tcs.testApplyRemoveAfterConcuAdd(new CommutativeCounterSet(), new CommutativeCounterSet());
        assertEquals(new HashSet(), s);
    }
     
    @Test(expected = PreconditionException.class)
    public void testExceptionAdd() throws PreconditionException{
         
         CrdtSetGeneric tcs = new CrdtSetGeneric();
         tcs.testAddException(new CommutativeCounterSet());
    }
     
    @Test(expected = PreconditionException.class)
    public void testExceptionRmv() throws PreconditionException{
         
         CrdtSetGeneric tcs = new CrdtSetGeneric();
         tcs.testRemoveException(new CommutativeCounterSet());
    }
    
    @Test
    public void testapply() throws PreconditionException {
        //verify counter
        CommutativeCounterSet Rep1 = new CommutativeCounterSet();
        CommutativeCounterSet Rep2 = new CommutativeCounterSet();
        CommutativeCounterSet Rep3 = new CommutativeCounterSet();
        CommutativeCounterSet result2 = new CommutativeCounterSet();//remote

        CounterMessage Op1 = (CounterMessage) Rep1.innerAdd('a');
        CounterMessage Op2 = (CounterMessage) Rep2.innerAdd('a');
        CounterMessage Op3 = (CounterMessage) Rep3.innerAdd('a');

        result2.applyRemote(Op1);
        result2.applyRemote(Op2);
        result2.applyRemote(Op3);

        assertEquals(result2.getMap().size(), 1);
        assertEquals(result2.getMap().get('a'), 3);
    }

    @Test
    public void testapply2() throws PreconditionException {
        CommutativeCounterSet Rep1 = new CommutativeCounterSet();
        CommutativeCounterSet Rep2 = new CommutativeCounterSet();

        CounterMessage Op1 = (CounterMessage) Rep1.innerAdd('a');
        CounterMessage Op2 = (CounterMessage) Rep1.innerRemove('a');
        CounterMessage Op3 = (CounterMessage) Rep2.innerAdd('a');

        Rep1.applyRemote(Op3);
        Rep2.applyRemote(Op1);
        Rep2.applyRemote(Op2);

        assertEquals(Rep1.getMap().size(), 1);
        assertEquals(Rep2.getMap().size(), 1);

        assertEquals(Op1.getCounter(), 1);
        assertEquals(Op2.getCounter(), -1);
        assertEquals(Op3.getCounter(), 1);
        assertEquals(Rep1.lookup(), Rep2.lookup());
    }
}
