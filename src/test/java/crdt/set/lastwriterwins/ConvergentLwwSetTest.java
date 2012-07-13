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
package crdt.set.lastwriterwins;

import crdt.set.CrdtSetGeneric;
import crdt.PreconditionException;
import java.util.*;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author score
 */
public class ConvergentLwwSetTest<T> {
    
    CrdtSetGeneric tcs = new CrdtSetGeneric();

    @Test
    public void test() throws PreconditionException{        
         tcs.runTests(new ConvergentLwwSet());
    }
    
    @Test
    public void testConcurAddDel() throws PreconditionException{
         Set<T> s = tcs.testApplyConcurAddDel(new ConvergentLwwSet(){{setReplicaNumber(0);}}, 
                 new ConvergentLwwSet(){{setReplicaNumber(1);}});
         assertEquals(new HashSet(){{add('b');}}, s);
    }
    
    @Test
    public void testConcurAddDelInverse() throws PreconditionException{
         Set<T> s = tcs.testApplyConcurAddDel(new ConvergentLwwSet(){{setReplicaNumber(1);}}, 
                 new ConvergentLwwSet(){{setReplicaNumber(0);}});
         assertEquals(new HashSet(){{add('b');}}, s);
    }
    
    @Test
    public void testConcurAddThenDel() throws PreconditionException{
        Set<T> s = tcs.testApplyConcurAddThenDel(new ConvergentLwwSet(), new ConvergentLwwSet());
        assertEquals(new HashSet(), s);
    }
    
    @Test
    public void testRemoveAfterConcuAdd() throws PreconditionException{
        Set<T> s = tcs.testApplyRemoveAfterConcuAdd(new ConvergentLwwSet(), new ConvergentLwwSet());
        assertEquals(new HashSet(), s);
    }
    
    @Test(expected = PreconditionException.class)
    public void testExceptionAdd() throws PreconditionException{
         
         CrdtSetGeneric tcs = new CrdtSetGeneric();
         tcs.testAddException(new ConvergentLwwSet());
    }
     
    @Test(expected = PreconditionException.class)
    public void testExceptionRmv() throws PreconditionException{
         
         CrdtSetGeneric tcs = new CrdtSetGeneric();
         tcs.testRemoveException(new ConvergentLwwSet());
    }
    
    @Test
    public void testmerge() throws PreconditionException {
          
        //Add,innerRemove/Add
        ConvergentLwwSet Rep1 = new ConvergentLwwSet();
        ConvergentLwwSet Rep2 = new ConvergentLwwSet();
        
        Rep1.innerAdd('a');//time=1
        Rep1.innerRemove('a');//time=2

        Rep2.innerAdd('a');//time = 1
        
        Rep2.applyRemote(Rep1); 
        
        assertEquals(Rep2.getMapA().keySet().size(), 1);
        assertEquals(Rep2.getMapR().keySet().size(), 1);
        assertEquals(Rep2.getMapA().get('a'), 1);
        assertEquals(Rep2.getMapR().get('a'), 1);
        assertEquals(Rep1.getMapR().get('a'), 1);//time=2(innerRemove)        
        assertEquals(Rep2.lookup().size(),0);
          
      }
      
       @Test
    public void testmerge2() throws PreconditionException {
          
        //delete element does not existe in lookup
        ConvergentLwwSet Rep1 = new ConvergentLwwSet();
        ConvergentLwwSet Rep2 = new ConvergentLwwSet();
        
        Rep1.innerAdd('a');//time=1
        Rep2.applyRemote(Rep1);
        
        Rep1.innerRemove('a');//time = 2
        Rep2.innerRemove('a');//time = 2
        
        Rep2.applyRemote(Rep1);
         
        
        assertEquals(Rep2.getMapA().keySet().size(), 1);
        assertEquals(Rep2.getMapR().keySet().size(), 1);
        assertEquals(Rep2.getMapA().get('a'), 1);
        assertEquals(Rep2.getMapR().get('a'), 1);//time=2(innerRemove)        
        assertEquals(Rep2.lookup().size(),0);
          
      }
    
}
