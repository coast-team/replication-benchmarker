/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker.woot.wooto;

import jbenchmarker.logootsplit.*;
import crdt.PreconditionException;
import jbenchmarker.core.MergeAlgorithm;
import jbenchmarker.core.SequenceOperation;
import jbenchmarker.factories.LogootSFactory;
import jbenchmarker.factories.WootFactories;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author urso
 */
public class WootOTest {
    
    public WootOTest() {
    }
    private MergeAlgorithm replica;

    @Before
    public void setUp() throws Exception {
        replica = (MergeAlgorithm) new WootFactories.WootOFactory().create();
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
        replica.applyLocal(SequenceOperation.update(pos, off, upd));
        assertEquals(content.substring(0, pos) + upd + content.substring(pos+off), replica.lookup());        
    }
}
