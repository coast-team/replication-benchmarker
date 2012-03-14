/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.set.observedremove;

import crdt.set.CrdtSetGeneric;
import crdt.PreconditionException;
import java.util.*;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author score
 */
public class ConvergentOrSetTest<T> {
    
    CrdtSetGeneric tcs = new CrdtSetGeneric();

    @Test
    public void test() throws PreconditionException{        
         tcs.runTests(new ConvergentOrSet());
    }
    
    @Test
    public void testConcurAddDel() throws PreconditionException{
         Set<T> s = tcs.testApplyConcurAddDel(new ConvergentOrSet(){{setReplicaNumber(0);}}, 
                 new ConvergentOrSet(){{setReplicaNumber(1);}});
         assertEquals(new HashSet(){{add('a');add('b');}}, s);
    }
    
    @Test
    public void testConcurAddDelInverse() throws PreconditionException{
         Set<T> s = tcs.testApplyConcurAddDel(new ConvergentOrSet(){{setReplicaNumber(0);}}, 
                 new ConvergentOrSet(){{setReplicaNumber(0);}});
         assertEquals(new HashSet(){{add('b');}}, s);
    }
    
    @Test
    public void testConcurAddThenDel() throws PreconditionException{
        Set<T> s = tcs.testApplyConcurAddThenDel(new ConvergentOrSet(){{setReplicaNumber(0);}},
                new ConvergentOrSet(){{setReplicaNumber(1);}});
        assertEquals(new HashSet(){{add('a');}}, s);
    }
    
    @Test
    public void testRemoveAfterConcuAdd() throws PreconditionException{
        Set<T> s = tcs.testApplyRemoveAfterConcuAdd(new ConvergentOrSet(), new ConvergentOrSet());
        assertEquals(new HashSet(), s);
    }
     
    @Test(expected = PreconditionException.class)
    public void testExceptionAdd() throws PreconditionException{
         
         CrdtSetGeneric tcs = new CrdtSetGeneric();
         tcs.testAddException(new ConvergentOrSet());
    }
     
    @Test(expected = PreconditionException.class)
    public void testExceptionRmv() throws PreconditionException{
         
         CrdtSetGeneric tcs = new CrdtSetGeneric();
         tcs.testRemoveException(new ConvergentOrSet());
    }
     
     @Test
    public void testMerge() throws PreconditionException{
         //test union
        ConvergentOrSet Rep1 = new ConvergentOrSet();
        ConvergentOrSet Rep2 = new ConvergentOrSet();
        Rep1.setReplicaNumber(1);
        Rep1.setReplicaNumber(2);
        
        Rep1.innerAdd('a');
        Rep1.innerRemove('a');    
        Rep2.innerAdd('a');
        
        Rep2.applyRemote(Rep1.clone()); 
        
        assertEquals(Rep2.getMapA().keySet().size(), 1);
        Set sizeTag = (Set)Rep2.getMapA().get('a');
        assertEquals(sizeTag.size(), 2);        
        assertTrue(Rep2.lookup().contains('a'));
        
        Rep1.applyRemote(Rep2.clone()); 
        assertEquals(Rep1.getMapA().keySet().size(), 1);
        Set sizeTag2 = (Set)Rep1.getMapA().get('a');
        assertEquals(sizeTag2.size(), 2);   
        
        assertEquals(Rep1.lookup().size(), 1);
        
     }  
}
