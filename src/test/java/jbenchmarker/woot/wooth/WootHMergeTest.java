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

import crdt.CRDTMessage;
import crdt.OperationBasedOneMessage;
import crdt.PreconditionException;
import crdt.simulator.IncorrectTraceException;
import jbenchmarker.core.SequenceOperation;
import jbenchmarker.woot.WootOperation;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author urso
 */
public class WootHMergeTest {

    // helpers
    SequenceOperation insert(int p, String s) {
        return SequenceOperation.insert( p, s);
    }
    SequenceOperation delete(int p, int o) {
         return SequenceOperation.delete( p, o);
    }
     
    /**
     * Test of applyLocal method, of class WootHashMerge.
     */
    @Test
    public void testapplyLocal() throws IncorrectTraceException, PreconditionException {
        System.out.println("applyLocal");
        WootHashMerge instance = new WootHashMerge(new WootHashDocument(), 1);
        OperationBasedOneMessage r1 = (OperationBasedOneMessage)instance.applyLocal(insert(0,"a"));
        assertEquals(1, r1.size());
        assertEquals('a', ((WootOperation) r1.getOperation()).getContent());
        assertEquals("a", instance.lookup());        

        CRDTMessage r;
        r =instance.applyLocal(insert(0,"bc"));
        assertEquals(2, r.size());
        assertEquals("bca", instance.lookup());         

        r = instance.applyLocal(delete(0,1));
        assertEquals(1, r.size());
        assertEquals("ca", instance.lookup()); 

        r = instance.applyLocal(insert(1,"efg"));
        assertEquals(3, r.size());
        assertEquals("cefga", instance.lookup()); 

        r = instance.applyLocal(delete(1,2));
        assertEquals(2, r.size());
        assertEquals("cga", instance.lookup()); 

        r = instance.applyLocal(delete(1,2));
        assertEquals(2, r.size());
        assertEquals("c", instance.lookup()); 
    }
    
    /**
     * Testing out of bound insert.
     */
    @Test(expected=NullPointerException.class)
    public void testGenerateInsIncorrect() throws IncorrectTraceException, PreconditionException {
        WootHashMerge instance = new WootHashMerge(new WootHashDocument(), 1);
        
        instance.applyLocal(insert(10,"a"));
        fail("Out of bound insert not detected.");    
    }
    
    /**
     * Testing out of bound del.
     */
    @Test(expected=NullPointerException.class)
    public void testGenerateDelIncorrect() throws IncorrectTraceException, PreconditionException {
        WootHashMerge instance = new WootHashMerge(new WootHashDocument(), 1);
        
        instance.applyLocal(delete(0,1));
        fail("Out of bound delete not detected.");    
    }
    
    
    @Test
    public void accent() throws IncorrectTraceException, PreconditionException {
        WootHashMerge instance = new WootHashMerge(new WootHashDocument(), 1);
        CRDTMessage r = instance.applyLocal(insert(0,"à"));
        assertEquals(1, r.size());
    }
   
}
