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
public class ConvergentCounterSetTest<T> {
    
    CrdtSetGeneric tcs = new CrdtSetGeneric();

    @Test
    public void test() throws PreconditionException{        
         tcs.runTests(new ConvergentCounterSet());
    }
    
    @Test
    public void testConcurAddDel() throws PreconditionException{
         Set<T> s = tcs.testApplyConcurAddDel(new ConvergentCounterSet(), new ConvergentCounterSet());
         assertEquals(new HashSet(){{add('b');}}, s);
    }
    
    @Test
    public void testConcurAddThenDel() throws PreconditionException{
        Set<T> s = tcs.testApplyConcurAddThenDel(new ConvergentCounterSet(), new ConvergentCounterSet());
        assertEquals(new HashSet(), s);
    }
    
    @Test
    public void testRemoveAfterConcuAdd() throws PreconditionException{
        Set<T> s = tcs.testApplyRemoveAfterConcuAdd(new ConvergentCounterSet(), new ConvergentCounterSet());
        assertEquals(new HashSet(), s);
    }
    
    @Test(expected = PreconditionException.class)
    public void testExceptionAdd() throws PreconditionException{
         
         CrdtSetGeneric tcs = new CrdtSetGeneric();
         tcs.testAddException(new ConvergentCounterSet());
    }
     
    @Test(expected = PreconditionException.class)
    public void testExceptionRmv() throws PreconditionException{
         
         CrdtSetGeneric tcs = new CrdtSetGeneric();
         tcs.testRemoveException(new ConvergentCounterSet());
    }     

    @Test
    public void testMerge() throws PreconditionException {
        //Verify union
        ConvergentCounterSet cs1 = new ConvergentCounterSet();
        ConvergentCounterSet cs2 = new ConvergentCounterSet();

        cs1.innerAdd('a');
        cs1.innerRemove('a');

        cs2.innerAdd('a'); 

        cs2.applyRemote(cs1);//merge
        assertEquals(cs2.mapA.get('a'), 1);
        assertEquals(cs2.mapR.get('a'), 1);

        assertEquals(cs2.lookup().size(), 0);

        cs1.applyRemote(cs2);//merge

        assertEquals(cs1.lookup(), cs2.lookup());
        assertEquals(cs1.lookup().size(), 0);
        assertFalse(cs1.lookup().contains('a'));

    }
     @Test
    public void testMerge2() throws PreconditionException {
         //delete element does not existe in lookup
        ConvergentCounterSet Rep1 = new ConvergentCounterSet();
        ConvergentCounterSet Rep2 = new ConvergentCounterSet();
        
        Rep1.innerAdd('a');
        Rep2.applyRemote(Rep1);
                
        Rep1.innerRemove('a');
        Rep2.innerRemove('a');
        Rep2.applyRemote(Rep1);
        
        assertEquals(Rep2.mapA.keySet().size(), 1);
        assertEquals(Rep2.mapA.get('a'), Rep2.mapR.get('a'));
        assertEquals(Rep2.lookup().size(), 0);
         
     }
}
