/**
 *   This file is part of ReplicationBenchmark.
 *
 *   ReplicationBenchmark is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   ReplicationBenchmark is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with ReplicationBenchmark.  If not, see <http://www.gnu.org/licenses/>.
 *
 **/


package jbenchmarker.logoot;
import java.util.ArrayList;
import jbenchmarker.core.Operation;
import jbenchmarker.trace.TraceOperation;
import java.util.List;



import org.junit.Test;
import static org.junit.Assert.*;
import jbenchmarker.trace.IncorrectTrace;

/**
 *
 * @author mehdi
 */
public class LogootMergeTest 
{
      
         // helpers
    TraceOperation insert(int p, String s) {
        return TraceOperation.insert(1, p, s, null); //Replica , position , content , VH
    }
    TraceOperation delete(int p, int o) {
         return TraceOperation.delete(1, p, o, null);//Replica , position , offset , VH
    }
    
        @Test
    public void testgenerateLocal() {
        LogootMerge LM = new LogootMerge(new LogootDocument(Long.MAX_VALUE), 1, 64, new BoundaryStrategy(1000000000));

        List<Operation> a = LM.generateLocal(insert(0, "a"));  //a
        assertEquals(1, a.size());
        assertEquals("a", LM.getDoc().view());


        a = LM.generateLocal(insert(0, "gf")); //gfa
        assertEquals(2, a.size());
        assertEquals("gfa", LM.getDoc().view());

        a = LM.generateLocal(delete(0, 1));//gf
        assertEquals(1, a.size());
        assertEquals("fa", LM.getDoc().view());

        a = LM.generateLocal(insert(1, "EKL"));//fEKLa
        assertEquals(3, a.size());
        assertEquals("fEKLa", LM.getDoc().view());


        a = LM.generateLocal(delete(1, 3));
        assertEquals("fa", LM.getDoc().view());
    }
        
    @Test
    public void testDeleteBloc() {
        
        LogootMerge LM = new LogootMerge(new LogootDocument(Long.MAX_VALUE), 1, 64, new BoundaryStrategy(1000000000));
        LogootDocument lg = (LogootDocument) (LM.getDoc());
        
        
        List<Operation> a = LM.generateLocal(insert(0, "aiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiib"));
        assertEquals(80, a.size());
        assertEquals("aiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiib", LM.getDoc().view());


        for(int i =1;i<lg.getIdTable().size()-1;i++)
        {
             assertTrue(lg.getIdTable().get(i).compareTo(lg.getIdTable().get(i-1)) > 0);
             assertFalse(lg.getIdTable().get(i).compareTo(lg.getIdTable().get(i+1)) > 0);
        }
        
        a = LM.generateLocal(delete(1, 78));
        assertEquals("ab", LM.getDoc().view());
        
        
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testIns() throws IncorrectTrace {
        LogootMerge LM = new LogootMerge(new LogootDocument(Long.MAX_VALUE), 1, 2, new BoundaryStrategy(1000000000));
        LM.generateLocal(insert(10, "a"));
        fail("Out of bound insert not detected.");
    }

    @Test(expected = java.lang.AssertionError.class) //contrairement à IndexOutOfBoundsException
    public void testDel() throws IncorrectTrace {
        LogootMerge LM = new LogootMerge(new LogootDocument(Long.MAX_VALUE), 1, 2, new BoundaryStrategy(1000000000));
        LM.generateLocal(delete(0, 1));
        fail("Out of bound delete not detected.");
    }
}
