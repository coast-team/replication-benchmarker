/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.set.twophases;

import crdt.set.CrdtSetGeneric;
import crdt.PreconditionException;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author score
 */
public class ConvergentTwoPhaseSetTest {
    
    @Test
    public void test() throws PreconditionException{
         
         CrdtSetGeneric tcs = new CrdtSetGeneric();
         tcs.testadd(new ConvergentTwoPhaseSet());
         tcs.testremove(new ConvergentTwoPhaseSet());
         tcs.testLookupAddThenRemove(new ConvergentTwoPhaseSet());
         tcs.testLookupAfterAdd(new ConvergentTwoPhaseSet());
    }
     
     @Test(expected = PreconditionException.class)
    public void testExeption() throws PreconditionException{
         
         CrdtSetGeneric tcs = new CrdtSetGeneric();
         tcs.testAddException(new ConvergentTwoPhaseSet());
         tcs.testRemoveException(new ConvergentTwoPhaseSet());
    }
    
    @Test(expected = PreconditionException.class)
    public void addAfterRemove() throws PreconditionException{
        //verify tombstome
        ConvergentTwoPhaseSet tpcs = new ConvergentTwoPhaseSet();
        tpcs.innerAdd('a');
        tpcs.innerRemove('a');
        tpcs.innerAdd('a');
        fail("exception add after delete does not detected");
    }
    
    @Test
    public void merge() throws PreconditionException{
        //add,innerRemove/add
        ConvergentTwoPhaseSet tpcs = new ConvergentTwoPhaseSet();
        ConvergentTwoPhaseSet tpcs2 = new ConvergentTwoPhaseSet();
        
        tpcs.innerAdd('a');
        tpcs.innerRemove('a');
        
        tpcs2.innerAdd('a');
        
        tpcs.applyRemote(tpcs2);
        assertFalse(tpcs.lookup().contains('a'));
    }
    
     @Test
    public void testMerge2() throws PreconditionException{
         //delete element does not existe in lookup
        ConvergentTwoPhaseSet Rep1 = new ConvergentTwoPhaseSet();
        ConvergentTwoPhaseSet Rep2 = new ConvergentTwoPhaseSet();
        
        Rep1.innerAdd('a');
        Rep2.applyRemote(Rep1);
                
        Rep1.innerRemove('a');
        Rep2.innerRemove('a');
        Rep2.applyRemote(Rep1);

        assertEquals(Rep2.lookup().size(), 0);
     }
     
    

}
