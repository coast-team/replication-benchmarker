/**
 * Replication Benchmarker
 * https://github.com/score-team/replication-benchmarker/
 * Copyright (C) 2012 LORIA / Inria / SCORE Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package crdt.set.twophases;

import crdt.CRDTMessage;
import crdt.set.CrdtSetGeneric;
import crdt.PreconditionException;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author score
 */
public class CommutativeTwoPhaseSetTest<T> {
    
     @Test
    public void test() throws PreconditionException{
         
         CrdtSetGeneric tcs = new CrdtSetGeneric();
         tcs.testadd(new CommutativeTwoPhaseSet());
         tcs.testremove(new CommutativeTwoPhaseSet());
         tcs.testLookupAddThenRemove(new CommutativeTwoPhaseSet());
         tcs.testLookupAfterAdd(new CommutativeTwoPhaseSet());
    }
     
     @Test(expected = PreconditionException.class)
    public void testExeption() throws PreconditionException{
         
         CrdtSetGeneric tcs = new CrdtSetGeneric();
         tcs.testAddException(new CommutativeTwoPhaseSet());
         tcs.testRemoveException(new CommutativeTwoPhaseSet());
    }
     
    @Test
    public void testapply() throws PreconditionException{
        
        CommutativeTwoPhaseSet Rep1 = new CommutativeTwoPhaseSet();
        CommutativeTwoPhaseSet Rep2 = new CommutativeTwoPhaseSet();
        
        Rep2.applyRemote(Rep1.innerAdd('a'));
        Rep2.applyRemote(Rep1.innerAdd("TestString"));
        Rep2.applyRemote(Rep1.innerAdd(123));
        
        assertEquals(Rep2.getSetA().size(), 3);//{a,TestString,123}
        assertEquals(Rep2.getSetR().size(), 0);//{}
        assertEquals(Rep1.lookup(), Rep2.lookup());
                
        Rep2.applyRemote(Rep1.innerRemove('a'));
        
        assertEquals(Rep2.getSetA().size(), 2);//{a,123}
        assertEquals(Rep2.getSetR().size(), 1);//{"TestString"}
        
    }
    
    @Test
    public void testapply2() throws PreconditionException {
        //add,innerRemove/add
        CommutativeTwoPhaseSet tpcs = new CommutativeTwoPhaseSet();
        CommutativeTwoPhaseSet tpcs2 = new CommutativeTwoPhaseSet();

        tpcs2.innerAdd('a');
        tpcs.applyRemote(tpcs.innerAdd('a'));
        tpcs.innerRemove('a');

        assertFalse(tpcs.lookup().contains('a'));
    }

     @Test
    public void testapply3() throws PreconditionException{
         //delete element does not existe in lookup
        CommutativeTwoPhaseSet Rep1 = new CommutativeTwoPhaseSet();
        CommutativeTwoPhaseSet Rep2 = new CommutativeTwoPhaseSet();
          
        Rep2.applyRemote(Rep1.innerAdd('a'));
        
        CRDTMessage op1 = Rep1.innerRemove('a');
        CRDTMessage op2 = Rep2.innerRemove('a');
        Rep1.applyRemote(op2);
        Rep2.applyRemote(op1);

        assertEquals(Rep2.lookup().size(), 0);
     }
    
    //test Add after innerRemove exception
    @Test(expected = PreconditionException.class)
    public void testaddExept() throws PreconditionException{     
        CommutativeTwoPhaseSet Rep1 = new CommutativeTwoPhaseSet();
        
        Rep1.innerAdd('a');
        Rep1.innerAdd("TestString");
        Rep1.innerAdd(123);
        
        Rep1.innerRemove('a');
        Rep1.innerAdd('a');
        
        System.err.println("add element after remove, does not detected");
    }
    
}
