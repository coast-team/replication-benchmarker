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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jbenchmarker.woot;

import java.util.NoSuchElementException;
import jbenchmarker.woot.original.WootOriginalDocument;
import java.util.List;
import jbenchmarker.core.Operation;
import jbenchmarker.trace.IncorrectTrace;
import jbenchmarker.trace.TraceOperation;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author urso
 */
public class WootMergeTest {

    // helpers
    TraceOperation insert(int p, String s) {
        return TraceOperation.insert(1, p, s, null);
    }
    TraceOperation delete(int p, int o) {
         return TraceOperation.delete(1, p, o, null);
    }
    
    /**
     * Test of generateLocal method, of class WootMerge.
     */
    @Test
    public void testGenerateLocal() throws IncorrectTrace {
        System.out.println("generateLocal");
        WootMerge instance = new WootMerge(new WootOriginalDocument(), 1);
        
        List<Operation> r = instance.generateLocal(insert(0,"a"));
        assertEquals(1, r.size());
        assertEquals('a', ((WootOperation) r.get(0)).getContent());
        assertEquals("a", instance.getDoc().view());        

        r = instance.generateLocal(insert(0,"bc"));
        assertEquals(2, r.size());
        assertEquals("bca", instance.getDoc().view());         

        r = instance.generateLocal(delete(0,1));
        assertEquals(1, r.size());
        assertEquals("ca", instance.getDoc().view()); 

        r = instance.generateLocal(insert(1,"efg"));
        assertEquals(3, r.size());
        assertEquals("cefga", instance.getDoc().view()); 

        r = instance.generateLocal(delete(1,2));
        assertEquals(2, r.size());
        assertEquals("cga", instance.getDoc().view()); 

        r = instance.generateLocal(delete(1,2));
        assertEquals(2, r.size());
        assertEquals("c", instance.getDoc().view()); 
    }
    
    /**
     * Testing out of bound insert.
     */
    @Test(expected=NoSuchElementException.class)
    public void testGenerateInsIncorrect() throws IncorrectTrace {
        WootMerge instance = new WootMerge(new WootOriginalDocument(), 1);
        
        instance.generateLocal(insert(10,"a"));
        fail("Out of bound insert not detected.");    
    }
    
    /**
     * Testing out of bound del.
     */
    @Test(expected=NoSuchElementException.class)
    public void testGenerateDelIncorrect() throws IncorrectTrace {
        WootMerge instance = new WootMerge(new WootOriginalDocument(), 1);
        
        instance.generateLocal(delete(0,1));
        fail("Out of bound delete not detected.");    
    }
    
    
    @Test
    public void accent() throws IncorrectTrace {
        WootMerge instance = new WootMerge(new WootOriginalDocument(), 1);
        List<Operation> r = instance.generateLocal(insert(0,"à"));
        assertEquals(1, r.size());
    }
   
}