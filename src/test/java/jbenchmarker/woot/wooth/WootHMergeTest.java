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
package jbenchmarker.woot.wooth;

import java.util.NoSuchElementException;
import java.util.List;
import jbenchmarker.core.SequenceMessage;
import jbenchmarker.trace.IncorrectTrace;
import jbenchmarker.trace.TraceOperation;
import jbenchmarker.woot.WootOperation;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author urso
 */
public class WootHMergeTest {

    // helpers
    TraceOperation insert(int p, String s) {
        return TraceOperation.insert(1, p, s, null);
    }
    TraceOperation delete(int p, int o) {
         return TraceOperation.delete(1, p, o, null);
    }
     
    /**
     * Test of generateLocal method, of class WootHashMerge.
     */
    @Test
    public void testGenerateLocal() throws IncorrectTrace {
        System.out.println("generateLocal");
        WootHashMerge instance = new WootHashMerge(new WootHashDocument(), 1);
        
        List<SequenceMessage> r = instance.generateLocal(insert(0,"a"));
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
    @Test(expected=NullPointerException.class)
    public void testGenerateInsIncorrect() throws IncorrectTrace {
        WootHashMerge instance = new WootHashMerge(new WootHashDocument(), 1);
        
        instance.generateLocal(insert(10,"a"));
        fail("Out of bound insert not detected.");    
    }
    
    /**
     * Testing out of bound del.
     */
    @Test(expected=NullPointerException.class)
    public void testGenerateDelIncorrect() throws IncorrectTrace {
        WootHashMerge instance = new WootHashMerge(new WootHashDocument(), 1);
        
        instance.generateLocal(delete(0,1));
        fail("Out of bound delete not detected.");    
    }
    
    
    @Test
    public void accent() throws IncorrectTrace {
        WootHashMerge instance = new WootHashMerge(new WootHashDocument(), 1);
        List<SequenceMessage> r = instance.generateLocal(insert(0,"à"));
        assertEquals(1, r.size());
    }
   
}