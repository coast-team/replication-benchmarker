/**
 * Replication Benchmarker
 * https://github.com/score-team/replication-benchmarker/
 * Copyright (C) 2013 LORIA / Inria / SCORE Team
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
package jbenchmarker.woot;

import crdt.CRDTMessage;
import crdt.OperationBasedOneMessage;
import crdt.PreconditionException;
import crdt.simulator.IncorrectTraceException;
import java.util.NoSuchElementException;
import jbenchmarker.woot.original.WootOriginalDocument;
import java.util.List;
import jbenchmarker.core.SequenceMessage;
import jbenchmarker.core.SequenceOperation;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author urso
 */
public class WootMergeTest {

    // helpers
    SequenceOperation insert(int p, String s) {
        return SequenceOperation.insert(p, s);
    }
    SequenceOperation delete(int p, int o) {
         return SequenceOperation.delete( p, o);
    }
    
    /**
     * Test of generateLocal method, of class WootMerge.
     */
    @Test
    public void testGenerateLocal() throws IncorrectTraceException, PreconditionException {
        System.out.println("generateLocal");
        WootMerge instance = new WootMerge(new WootOriginalDocument(), 1);
        
        CRDTMessage r = instance.applyLocal(SequenceOperation.insert(0,"a"));
        assertEquals(1, r.size());
        assertEquals('a', ((WootOperation) ((OperationBasedOneMessage) r).getOperation()).getContent());
        assertEquals("a", instance.lookup());  
        assertEquals(1, instance.getDoc().viewLength());

        r = instance.applyLocal(SequenceOperation.insert(0,"bc"));
        assertEquals(2, r.size());
        assertEquals("bca", instance.lookup());        
        assertEquals(3, instance.getDoc().viewLength());

        r = instance.applyLocal(SequenceOperation.delete(0,1));
        assertEquals(1, r.size());
        assertEquals("ca", instance.lookup()); 
        assertEquals(2, instance.getDoc().viewLength());

        r = instance.applyLocal(SequenceOperation.insert(1,"efg"));
        assertEquals(3, r.size());
        assertEquals("cefga", instance.lookup()); 
        assertEquals(5, instance.getDoc().viewLength());

        r = instance.applyLocal(SequenceOperation.delete(1,2));
        assertEquals(2, r.size());
        assertEquals("cga", instance.lookup()); 
        assertEquals(3, instance.getDoc().viewLength());

        r = instance.applyLocal(SequenceOperation.delete(1,2));
        assertEquals(2, r.size());
        assertEquals("c", instance.lookup()); 
        assertEquals(1, instance.getDoc().viewLength());
    }
    
    /**
     * Testing out of bound insert.
     */
    @Test(expected=NoSuchElementException.class)
    public void testGenerateInsIncorrect() throws IncorrectTraceException {
        WootMerge instance = new WootMerge(new WootOriginalDocument(), 1);
        
        instance.localInsert(SequenceOperation.insert(10,"a"));
        fail("Out of bound insert not detected.");    
    }
    
    /**
     * Testing out of bound del.
     */
    @Test(expected=NoSuchElementException.class)
    public void testGenerateDelIncorrect() throws IncorrectTraceException, PreconditionException {
        WootMerge instance = new WootMerge(new WootOriginalDocument(), 1);
        
        instance.applyLocal(SequenceOperation.delete(0,1));
        fail("Out of bound delete not detected.");    
    }
    
    
    @Test
    public void accent() throws IncorrectTraceException {
        WootMerge instance = new WootMerge(new WootOriginalDocument(), 1);
        List<SequenceMessage> r = instance.localInsert(SequenceOperation.insert(0,"à"));
        assertEquals(1, r.size());
    }
   
}
