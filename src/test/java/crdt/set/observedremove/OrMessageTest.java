/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.set.observedremove;

import java.util.*;
import crdt.set.observedremove.Tag;
import crdt.set.observedremove.OrMessage;
import crdt.set.observedremove.OrMessage.OpType;
import org.junit.Test;
import static org.junit.Assert.*;


/**
 *
 * @author score
 */
public class OrMessageTest {
    
       @Test
    public void OrOperationTest()
    {        
        Set s1 = new HashSet<Tag>();
        Set s2 = new HashSet<Tag>();
        s1.add(new Tag(1, 1));
        s2.add(new Tag(2, 1));
        
        OrMessage OrOp1 = new OrMessage(OpType.add, 'a', s1);
        OrMessage OrOp2 = new OrMessage(OpType.add, 'b', s2);
        OrMessage OrOp3 = new OrMessage(OpType.del, 'a', s1);

        assertEquals(OrOp1.getContent(), 'a');
        assertEquals(OrOp2.getContent(), 'b');
        assertEquals(OrOp3.getContent(), 'a');
        
        assertEquals(OrOp1.getTags(), s1);
        assertEquals(OrOp2.getTags(), s2);
        assertEquals(OrOp3.getTags(), s1);


        assertEquals(OrOp1.getType(), OpType.add);
        assertEquals(OrOp2.getType(), OpType.add);
        assertEquals(OrOp3.getType(), OpType.del);

    }
    
}
