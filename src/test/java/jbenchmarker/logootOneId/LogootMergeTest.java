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


/**
 * Replication Benchmarker
 * https://github.com/score-team/replication-benchmarker/
 * Copyright (C) 2011 INRIA / LORIA / SCORE Team
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
package jbenchmarker.logootOneId;
import crdt.PreconditionException;
import org.junit.Ignore;
import jbenchmarker.factories.LogootOneIdFactory;
import crdt.simulator.IncorrectTraceException;
import jbenchmarker.core.SequenceMessage;
import jbenchmarker.core.SequenceOperation;
import java.util.List;
import jbenchmarker.core.MergeAlgorithm;
import jbenchmarker.factories.LogootFactory;
import jbenchmarker.factories.LogootSFactory;


import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author mehdi
 */
public class LogootMergeTest 
{
          private MergeAlgorithm replica;

    @Before
    public void setUp() throws Exception {
        replica = (MergeAlgorithm) new LogootFactory().create();
    }

    @Test
    public void testEmpty() {
        assertEquals("", replica.lookup());
    }

    @Test
    public void testInsert() throws PreconditionException {
        String content = "abcdejk", c2 = "fghi";
        int pos = 3;      
        replica.applyLocal(SequenceOperation.insert(0, content));
        assertEquals(content, replica.lookup());
        replica.applyLocal(SequenceOperation.insert(pos, c2));
        assertEquals(content.substring(0, pos) + c2 + content.substring(pos), replica.lookup());        
    }
        
    @Test
    public void testDelete() throws PreconditionException {
        String content = "abcdefghijk";
        int pos = 3, off = 4;       
        replica.applyLocal(SequenceOperation.insert(0, content));
        assertEquals(content, replica.lookup());
        replica.applyLocal(SequenceOperation.delete(pos, off));
        assertEquals(content.substring(0, pos) + content.substring(pos+off), replica.lookup());        
    }
    
    @Test
    public void testUpdate() throws PreconditionException {
        String content = "abcdefghijk", upd = "xy";
        int pos = 3, off = 5;       
        replica.applyLocal(SequenceOperation.insert(0, content));
        assertEquals(content, replica.lookup());
        replica.applyLocal(SequenceOperation.replace(pos, off, upd));
        assertEquals(content.substring(0, pos) + upd + content.substring(pos+off), replica.lookup());        
    }

         // helpers
    SequenceOperation insert(int p, String s) {
        return  SequenceOperation.insert( p, s); //Replica , position , content , VH
    }
    SequenceOperation delete(int p, int o) {
         return SequenceOperation.delete( p, o);//Replica , position , offset , VH
    }
    SequenceOperation update(int p, int o, String s) {
        return SequenceOperation.replace(p, o, s); //Replica , position , content , VH
    }

        @Test
    public void testgenerateLocal() throws IncorrectTraceException {
         LogootOneIdMerge LM = (LogootOneIdMerge) new LogootOneIdFactory().create();

        List<SequenceMessage> a = LM.localInsert(insert(0, "a"));  //a
        assertEquals(1, a.size());
        assertEquals("a", LM.lookup());

        a = LM.localInsert(insert(0, "gf")); //gfa
        assertEquals(2, a.size());
        assertEquals("gfa", LM.lookup());

        a = LM.localDelete(delete(0, 1));//gf
        assertEquals(1, a.size());
        assertEquals("fa", LM.lookup());

        a = LM.localInsert(insert(1, "EKL"));//fEKLa
        assertEquals(3, a.size());
        assertEquals("fEKLa", LM.lookup());

        a = LM.localDelete(delete(1, 3));
        assertEquals(3, a.size());
        assertEquals("fa", LM.lookup());
    }
        
    //@Ignore
    @Test
    public void testUpdateLocal() throws IncorrectTraceException {
        LogootOneIdMerge LM = (LogootOneIdMerge) new LogootOneIdFactory().create();

        List<SequenceMessage> a = LM.localInsert(insert(0, "abcdef"));  //a

        a = LM.localUpdate(update(0, 2, "X")); 
        assertEquals(3, a.size());
        assertEquals("Xcdef", LM.lookup());

        a = LM.localUpdate(update(2, 2, "YZ")); 
        assertEquals(4, a.size());
        assertEquals("XcYZf", LM.lookup());
        
        a = LM.localUpdate(update(4, 1, "UVW")); 
        assertEquals(4, a.size());
        assertEquals("XcYZUVW", LM.lookup());
    }
        
    //@Ignore    
    @Test
    public void testDeleteBloc() throws IncorrectTraceException {
        
        LogootOneIdMerge LM = (LogootOneIdMerge) new LogootOneIdFactory().create();
        LogootOneIdDocument<Character> lg = (LogootOneIdDocument) (LM.getDoc());
        
        
        List<SequenceMessage> a = LM.localInsert(insert(0, "aiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiib"));
        assertEquals(80, a.size());
        assertEquals("aiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiib", LM.lookup());


        for(int i =1;i<=lg.viewLength();i++)
        {
             assertTrue(lg.getId(i).compareTo(lg.getId(i-1)) > 0);
             assertTrue(lg.getId(i+1).compareTo(lg.getId(i)) > 0);
        }
        
        a = LM.localDelete(delete(1, 78));
        assertEquals("ab", LM.lookup());
         
    }
    

    @Test(expected = IndexOutOfBoundsException.class)
    public void testIns() throws IncorrectTraceException {
        LogootOneIdMerge LM = (LogootOneIdMerge) new LogootOneIdFactory().create();
        LM.localInsert(insert(10, "a"));
        fail("Out of bound insert not detected.");
    }

    @Test(expected = java.lang.AssertionError.class) //contrairement à IndexOutOfBoundsException
    public void testDel() throws IncorrectTraceException {
        LogootOneIdMerge LM = (LogootOneIdMerge) new LogootOneIdFactory().create();
        LM.localDelete(delete(0, 1));
        fail("Out of bound delete not detected.");
    }
}
