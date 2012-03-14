/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.set.lastwriterwins;

import crdt.CRDTMessage;
import crdt.set.CrdtSetGeneric;
import crdt.PreconditionException;
import java.util.*;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author score
 */
public class CommutativeLwwSetTest<T> {
    
    CrdtSetGeneric tcs = new CrdtSetGeneric();

    @Test
    public void test() throws PreconditionException{        
         tcs.runTests(new CommutativeLwwSet());
    }
    
    @Test
    public void testConcurAddDel() throws PreconditionException{
         Set<T> s = tcs.testApplyConcurAddDel(new CommutativeLwwSet(){{setReplicaNumber(0);}}, 
                 new CommutativeLwwSet(){{setReplicaNumber(1);}});
         assertEquals(new HashSet(){{add('b');}}, s);
    }
    
    @Test
    public void testConcurAddDelInverse() throws PreconditionException{
         Set<T> s = tcs.testApplyConcurAddDel(new CommutativeLwwSet(){{setReplicaNumber(1);}}, 
                 new CommutativeLwwSet(){{setReplicaNumber(0);}});
         assertEquals(new HashSet(){{add('b');}}, s);
    }
    
    @Test
    public void testConcurAddThenDel() throws PreconditionException{
        Set<T> s = tcs.testApplyConcurAddThenDel(new CommutativeLwwSet(), new CommutativeLwwSet());
        assertEquals(new HashSet(), s);
    }
    
    @Test
    public void testRemoveAfterConcuAdd() throws PreconditionException{
        Set<T> s = tcs.testApplyRemoveAfterConcuAdd(new CommutativeLwwSet(), new CommutativeLwwSet());
        assertEquals(new HashSet(), s);
    }
    
    @Test(expected = PreconditionException.class)
    public void testExceptionAdd() throws PreconditionException{
         
         CrdtSetGeneric tcs = new CrdtSetGeneric();
         tcs.testAddException(new CommutativeLwwSet());
    }
     
    @Test(expected = PreconditionException.class)
    public void testExceptionRmv() throws PreconditionException{
         
         CrdtSetGeneric tcs = new CrdtSetGeneric();
         tcs.testRemoveException(new CommutativeLwwSet());
    }

    @Test
    public void testapply() throws PreconditionException {

        CommutativeLwwSet Rep1 = new CommutativeLwwSet();
        CommutativeLwwSet Rep2 = new CommutativeLwwSet();
        CommutativeLwwSet Rep3 = new CommutativeLwwSet();
        CommutativeLwwSet lw = new CommutativeLwwSet();

        //three concurents insertions
        CRDTMessage m1 = Rep1.innerAdd('a');
        CRDTMessage m2 = Rep2.innerAdd('a');
        CRDTMessage m3 = Rep3.innerAdd('a');

        lw.applyRemote(m1);
        lw.applyRemote(m2);
        lw.applyRemote(m3);

        assertEquals(lw.getmapA().size(), 1);
        assertEquals(lw.getmapR().size(), 0);
        assertEquals(lw.getmapA().get('a'), 1);

    }

    @Test
    public void testapply2() throws PreconditionException {

        CommutativeLwwSet Rep1 = new CommutativeLwwSet();

        Rep1.innerAdd('a');
        Rep1.innerAdd('b');

        CommutativeLwwSet Rep2 = new CommutativeLwwSet();
        Rep2.setmapA(Rep1.getmapA());

        assertEquals(Rep2.getmapA().size(), 2);
        assertEquals(Rep2.getmapR().size(), 0);
        assertEquals(Rep2.getmapA().get('a'), 1);
        assertEquals(Rep2.getmapA().get('b'), 1);

        CRDTMessage m3 = Rep1.innerRemove('a');

        Rep2.applyRemote(m3);

        assertEquals(Rep2.getmapA().size(), 1);
        assertEquals(Rep2.getmapR().size(), 1);
        assertEquals(Rep2.getmapR().get('a'), 2);
        assertEquals(Rep2.getmapA().get('b'), 1);
    }

    @Test
    public void testapply3() throws PreconditionException {

        //(innerAdd,rmv)/(innerAdd)
        CommutativeLwwSet Rep1 = new CommutativeLwwSet();

        CRDTMessage m1 = Rep1.innerAdd('a');
        CRDTMessage m2 = Rep1.innerRemove('a');

        CommutativeLwwSet Rep2 = new CommutativeLwwSet();
        CRDTMessage m3 = Rep2.innerAdd('a');


        Rep2.applyRemote(m1);
        Rep2.applyRemote(m2);
        Rep1.applyRemote(m3);

        assertEquals(Rep1.getmapA().size(), 0);
        assertEquals(Rep2.getmapA().size(), 0);

        assertEquals(Rep1.getmapR().size(), 1);
        assertEquals(Rep2.getmapR().size(), 1);

        assertEquals(Rep1.getmapR().get('a'), 2);
        assertEquals(Rep2.getmapR().get('a'), 2);
    }
    
    @Test
    public void testapply4() throws PreconditionException {

        //remove element dors not existe
        CommutativeLwwSet Rep1 = new CommutativeLwwSet();
        CommutativeLwwSet Rep2 = new CommutativeLwwSet();
        CRDTMessage m1 = Rep1.innerAdd('a');
        
        Rep2.applyRemote(m1);
        CRDTMessage m2 = Rep1.innerRemove('a');
        CRDTMessage m3 = Rep2.innerRemove('a');
        
        Rep2.applyRemote(m2);

        assertEquals(Rep1.getmapA().size(), 0);
        assertEquals(Rep2.getmapA().size(), 0);

        assertEquals(Rep1.getmapR().size(), 1);
        assertEquals(Rep2.getmapR().size(), 1);

        assertEquals(Rep1.getmapR().get('a'), 2);
        assertEquals(Rep2.getmapR().get('a'), 2);
    }
}