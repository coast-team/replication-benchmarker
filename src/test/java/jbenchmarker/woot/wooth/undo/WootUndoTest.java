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
package jbenchmarker.woot.wooth.undo;

import jbenchmarker.woot.wooth.*;
import crdt.CRDTMessage;
import crdt.OperationBasedOneMessage;
import crdt.PreconditionException;
import crdt.simulator.IncorrectTraceException;
import jbenchmarker.core.MergeAlgorithm;
import jbenchmarker.core.SequenceOperation;
import jbenchmarker.factories.WootFactories;
import jbenchmarker.woot.WootOperation;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author urso
 */
public class WootUndoTest {

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
    

    private MergeAlgorithm replica;

    @Before
    public void setUp() throws Exception {
        replica = (MergeAlgorithm) new WootFactories.WootUFactory().create();
        replica.setReplicaNumber(7);
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
}
