/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.set.observedremove;

import crdt.CRDTMessage;
import crdt.set.CrdtSetGeneric;
import crdt.PreconditionException;
import crdt.set.observedremove.OrMessage.OpType;
import java.util.*;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author score
 */
public class CommutativeOrSetTest<T> {

    CrdtSetGeneric tcs = new CrdtSetGeneric();

    @Test
    public void test() throws PreconditionException{        
         tcs.runTests(new CommutativeOrSet());
    }
    
    @Test
    public void testConcurAddDel() throws PreconditionException{
         Set<T> s = tcs.testApplyConcurAddDel(new CommutativeOrSet(){{setReplicaNumber(0);}}, 
                 new CommutativeOrSet(){{setReplicaNumber(1);}});
         assertEquals(new HashSet(){{add('a');add('b');}}, s);
    }
    
    /*
     * It is normal that we will be divergence in this case
     * first replica add a and b . his stat = {a,b}
     * seconde replica add a and delete a.his stat is empty {}
     * after applyOneRemote : replica1 contains only b, unlike replica2 add a and b {a,b}
     * 
     */
//    @Test
//    public void testConcurAddDelInverse() throws PreconditionException{
//         Set<T> s = tcs.testApplyConcurAddDel(new CommutativeOrSet(){{setReplicaNumber(0);}}, 
//                 new CommutativeOrSet(){{setReplicaNumber(0);}});
//         //assertEquals(new HashSet(){{add('a');add('b');}}, s);
//         //assertEquals(new HashSet(){{add('b');}}, s);
//    }
    
        @Test
    public void testConcurAddThenDel() throws PreconditionException{
        Set<T> s = tcs.testApplyConcurAddThenDel(new CommutativeOrSet(){{setReplicaNumber(0);}}, 
                new CommutativeOrSet(){{setReplicaNumber(1);}});
        assertEquals(new HashSet(){{add('a');}}, s);
    }
    
    @Test
    public void testRemoveAfterConcuAdd() throws PreconditionException{
        Set<T> s = tcs.testApplyRemoveAfterConcuAdd(new CommutativeOrSet(), new CommutativeOrSet());
        assertEquals(new HashSet(), s);
    }
     
    @Test(expected = PreconditionException.class)
    public void testExceptionAdd() throws PreconditionException{
         
         CrdtSetGeneric tcs = new CrdtSetGeneric();
         tcs.testAddException(new CommutativeOrSet());
    }
     
    @Test(expected = PreconditionException.class)
    public void testExceptionRmv() throws PreconditionException{
         
         CrdtSetGeneric tcs = new CrdtSetGeneric();
         tcs.testRemoveException(new CommutativeOrSet());
    }
    
    @Test
    public void testremove2() throws PreconditionException {
        //delete an element with several tags
        CommutativeOrSet orSet = new CommutativeOrSet();

        HashMap<Integer, Set<Tag>> hm = new HashMap<Integer, Set<Tag>>();

        Set t1 = new HashSet<Tag>();
        t1.add(new Tag(1, 1));
        Set t2 = new HashSet<Tag>();
        t2.add(new Tag(1, 2));
        Set t3 = new HashSet<Tag>();
        t3.add(new Tag(2, 1));

        hm.put(123, t1);
        hm.get(123).add(new Tag(1, 2));
        hm.put(156, t3);
        //{[123, (1,1)(1,2)],[156, (2,1)]}

        orSet.setMapA(hm);
        orSet.innerRemove(123);

        assertEquals(orSet.getMapA().size(), 1);
        assertTrue(orSet.contains(156));
        assertFalse(orSet.contains(123));

    }

    @Test
    public void testapply() throws PreconditionException {

        //simple apply
        CommutativeOrSet result = new CommutativeOrSet();

        CRDTMessage m1 = result.innerAdd('a');
        CRDTMessage m2 = result.innerAdd("testApply");
        CRDTMessage m3 = result.innerAdd(123);

        result.applyRemote(m1);
        result.applyRemote(m2);
        result.applyRemote(m3);

        assertEquals(result.getMapA().size(), 3);

    }

    @Test
    public void testapply2() throws PreconditionException {
        //Test concurrent innerAdd operations
        CommutativeOrSet result = new CommutativeOrSet();

        HashMap<Integer, Set<Tag>> hm = new HashMap<Integer, Set<Tag>>();

        CRDTMessage m1 = result.innerAdd('a');
        CRDTMessage m2 = result.innerAdd("testApply");
        CRDTMessage m3 = result.innerAdd(123);

        Set t1 = new HashSet<Tag>();
        t1.add(new Tag(1, 1));
        hm.put(123, t1);
        hm.get(123).add(new Tag(1, 2));
        result.setMapA(hm);//setA = {[123,(1, 1)(1, 2)]}

        result.applyRemote(m1);
        result.applyRemote(m2);
        result.applyRemote(m3);

        //setA = {[123,(1, 1)(1, 2)(Tag)][testApply, Tag][a, Tag]}
        assertEquals(result.getMapA().size(), 3);

    }

    @Test
    public void testapply3() throws PreconditionException {
        //Test concurrent innerAdd and innerRemove operations
        CommutativeOrSet result = new CommutativeOrSet();

        HashMap<Integer, Set<Tag>> hm = new HashMap<Integer, Set<Tag>>();

        CRDTMessage tpOp1 = result.innerAdd('a');
        CRDTMessage tpOp2 = result.innerAdd("testApply");
        CRDTMessage tpOp3 = result.innerAdd(123);

        Set t1 = new HashSet<Tag>();
        t1.add(new Tag(1, 1));
        hm.put(123, t1);
        result.setMapA(hm);//setA = {[123,(1, 1)]}

        result.applyRemote(tpOp1);
        result.applyRemote(tpOp2);
        result.applyRemote(tpOp3);
        //setA = {[123,(1, 1)(Tag)][testApply, Tag][a, Tag]}
        assertEquals(result.getMapA().size(), 3);


        Set stag = new HashSet<Tag>();
        Tag tag = new Tag(1, 1);
        stag.add(tag);
        CRDTMessage m4 = new OrMessage(OpType.del, 123, stag);
        result.applyRemote(m4);

        //setA = {[123,(Tag)][testApply, Tag][a, Tag]}
        assertEquals(result.getMapA().size(), 3);
    }
    
    @Test
    public void testapply4() throws PreconditionException{
         //delete element does not existe in lookup
        CommutativeOrSet Rep1 = new CommutativeOrSet();
        CommutativeOrSet Rep2 = new CommutativeOrSet();
        
        Rep2.applyRemote(Rep1.innerAdd('a'));
 
        Rep2.innerRemove('a');
        Rep2.applyRemote(Rep1.innerRemove('a'));
        
        assertEquals(Rep2.getMapA().keySet().size(), 0);
        assertEquals(Rep2.lookup().size(), 0);
     }
}
