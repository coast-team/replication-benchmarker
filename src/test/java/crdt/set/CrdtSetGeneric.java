/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.set;

import crdt.CRDTMessage;
import crdt.Factory;
import crdt.PreconditionException;
import java.util.*;
import static org.junit.Assert.*;

/**
 *
 * @author score
 */
public class CrdtSetGeneric<T, L> {

    public void runTests(Factory<CRDTSet> sf) throws PreconditionException {
         testadd(sf.create());
         testremove(sf.create());
         testReadd(sf.create());
         testLookupAddThenRemove(sf.create());
         testLookupAfterAdd(sf.create());
         testApplyAddThenDel(sf.create(), sf.create());
         testApplyRemoveConcurrent(sf.create(), sf.create());
         testApplyRemoveConcurrent2(sf.create(), sf.create(), sf.create());
    } 
 
    public void testadd(CRDTSet set) throws PreconditionException {
        set.innerAdd('a');
        set.innerAdd(123);
        set.innerAdd("test");
        HashSet s = new HashSet(){{add('a');add('a');add(123);add("test");}};
        assertEquals(s, set.lookup());
        assertTrue(set.contains('a'));
    }
    
    public void testAddException(CRDTSet set) throws PreconditionException{
        set.add('a');
        set.add("testString");
        set.add('a');
        fail("Add element that exists in set not detected");
    }
    
     
    public void testremove(CRDTSet set) throws PreconditionException{
         
        set.add('a');
        set.add("testString");
        set.innerRemove('a');
        HashSet s = new HashSet(){{add("testString");}};
        assertEquals(s, set.lookup());
        assertFalse(set.contains('a'));
    }
    
    public void testRemoveException(CRDTSet set) throws PreconditionException{     
        set.add('a');
        set.remove(123);
        
        fail("Delete an element that does not exist in the set not detected");
    }
      
    /*
     * test lookup after innerAdd elements
     * compare lookup of CRDTset with Set of element
     */
    public void testLookupAfterAdd(CRDTSet set) throws PreconditionException{
        Set tps2 = new HashSet(); 
        tps2.add('a');
        tps2.add("TestString");
        tps2.add(123);
        
        set.innerAdd('a');
        set.innerAdd("TestString");
        set.innerAdd(123);
        assertEquals(tps2, set.lookup());
    }
    
    /*
     * test lookup after readd elements
     * compare lookup of CRDTset with Set of element
     */
    public void testReadd(CRDTSet set) throws PreconditionException{
        Set tps2 = new HashSet(); 
        tps2.add('a');
        tps2.add('b');
        
        set.innerAdd('a');
        set.innerAdd('b');
        set.innerRemove('b');
        set.innerAdd('b');
        assertEquals(tps2, set.lookup());
    }
    
    /*
     * test lookup after after innerAdd and innerRemove elements
     * compare lookup of CRDTset with Set of element
     */ 
    public void testLookupAddThenRemove(CRDTSet set) throws PreconditionException{
        Set tps2 = new HashSet(); 
        tps2.add('a');
        tps2.add('b');
        
        set.innerAdd('a');
        set.innerAdd("TestString");
        set.innerAdd(123);
        set.innerAdd('b');
        
        set.innerRemove("TestString");
        set.innerRemove(123);
        
        assertEquals(tps2, set.lookup());
    }
    
    /*
     * Test simple applyRemote of two CRDTSet
     * lookup must be equal
     * Add --> apply
     * apply <-- innerRemove
     */ 
    public void testApplyAddThenDel(CRDTSet set, CRDTSet cs) throws PreconditionException{
        CRDTMessage m1 = set.innerAdd('a');
        CRDTMessage m2 = set.innerAdd('b');
        CRDTMessage m3 = set.innerAdd('c');

        cs.applyRemote(m1);
        cs.applyRemote(m2);
        cs.applyRemote(m3);

        assertEquals(set.lookup(), cs.lookup());
        
        CRDTMessage m4 = cs.innerRemove('a');
        CRDTMessage m5 = cs.innerRemove('b');
        
        set.applyRemote(m4);
        set.applyRemote(m5);
        
        Set<T> s = new HashSet(){{add('c');}};
        assertEquals(s,set.lookup());
        assertEquals(set.lookup(), cs.lookup());
    }

     /*
     * Test Convergence applyRemote
     * (Add) concurrent with (Add-innerRemove) of the same element
     * Add -->
     *  <-- innerAdd
     *  <-- innerRemove
     * then apply
     */
    public Set<T> testApplyConcurAddDel(CRDTSet set1, CRDTSet set2) throws PreconditionException {
        CRDTMessage m1 = set1.innerAdd('a');
        CRDTMessage m2 = set1.innerAdd('b');
        m1.concat(m2);
        CRDTMessage m3 = set2.innerAdd('a');
        CRDTMessage m4 = set2.innerRemove('a');
        m3.concat(m4);
        
        set2.applyRemote(m1);
        set1.applyRemote(m3);
        

        assertEquals(set1.lookup(), set2.lookup());
        return (Set<T>) set1.lookup();
    }

    /**
     * Test Convergence applyRemote
     * (Add) concurrent with (Add-innerRemove) of the same element
     * Add -->
     *  <-- innerAdd
     *  <-- innerRemove
     * then apply
     */
    public Set<T> testApplyConcurAddThenDel(CRDTSet set1, CRDTSet set2) throws PreconditionException {
 
        CRDTMessage m1 = set1.innerAdd('a');
        CRDTMessage m2 = set2.innerAdd('a');


        set1.applyRemote(m2);
        CRDTMessage m3 = set2.innerRemove('a');
        

        set1.applyRemote(m3);
        set2.applyRemote(m1);
        
        assertEquals(set1.lookup(), set2.lookup());
        return (Set<T>) set1.lookup();
        
    }
  
    /*
     * Test Convergence applyRemote
     * (Add) concurrent with (Add-innerRemove) of the same element
     * Add -->
     *  <-- innerAdd
     * applyRemote
     * <-- innerRemove
     */
    public Set<T> testApplyRemoveAfterConcuAdd(CRDTSet set1, CRDTSet set2) throws PreconditionException {
        CRDTMessage m1 = set1.innerAdd('a');
        CRDTMessage m2 = set2.innerAdd('a');
        
        set2.applyRemote(m1);
        set1.applyRemote(m2);
        
        CRDTMessage m3 = set2.innerRemove('a');
        set1.applyRemote(m3);
        
        assertEquals(set1.lookup(), set2.lookup());
        return (Set<T>)set1.lookup();
    }
    
    
    /*
     * Test Convergence applyRemote
     * two innerRemove concurrent (delete an element removed)
     * Add --> apply
     * <-- innerRemove
     * innerRemove -->
     * innerAdd -->
     */
    public void testApplyRemoveConcurrent(CRDTSet set1, CRDTSet set2) throws PreconditionException {
        CRDTMessage m1 = set1.innerAdd('a');
        
        set2.applyRemote(m1);
        CRDTMessage m2 = set2.innerRemove('a');
        CRDTMessage m3 = set1.innerRemove('a');
        
        set2.applyRemote(m3);
        set1.applyRemote(m2);
        
        assertEquals(set1.lookup(), set2.lookup());
        assertEquals(set1.lookup(), new HashSet());

        CRDTMessage m4 = set1.innerAdd('a');
        set2.applyRemote(m4);        
        
        Set<T> s = new HashSet(){{add('a');}};
        assertEquals(set1.lookup(), set2.lookup());
        assertEquals(s, set2.lookup());
    }
    
    
    /*
     * Test Convergence applyRemote
     * three Replica and replica 3 receive two innerRemove concurrent
     *  P1 : innerAdd --> P2 and P3
     *  P1 : innerRemove--> P2 and P3
     *  P2 : innerRemove --> P1 and P3
     */
    public void testApplyRemoveConcurrent2(CRDTSet set1, CRDTSet set2, CRDTSet set3) throws PreconditionException {
        CRDTMessage m1 = set1.innerAdd('a');
        set2.applyRemote(m1);
        set3.applyRemote(m1);
        
        CRDTMessage m2 = set1.innerRemove('a');
        CRDTMessage m3 = set2.innerRemove('a');
        
        set3.applyRemote(m2);
        set3.applyRemote(m3);
        
        set1.applyRemote(m3);
        set2.applyRemote(m2);
        
        assertEquals(set1.lookup(), set2.lookup());
        assertEquals(set1.lookup(), set3.lookup());
        assertEquals(set1.lookup(), new HashSet()); 
    }
}
