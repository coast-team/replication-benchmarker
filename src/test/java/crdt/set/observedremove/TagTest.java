/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.set.observedremove;

import crdt.set.observedremove.Tag;
import org.junit.Test;
import static org.junit.Assert.*;


/**
 *
 * @author score
 */
public class TagTest {

        
    @Test
    public void testTag() {
        Tag t = new Tag();        
        
        assertEquals(t.getNumOp(), 0);
        assertEquals(t.getNumReplica(), 0);
        
        Tag t2 = new Tag(1, 2);
        assertEquals(t2.getNumOp(), 2);
        assertEquals(t2.getNumReplica(), 1);        

    }
    
}
