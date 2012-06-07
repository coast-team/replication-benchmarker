/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.set.lastwriterwins;

import crdt.set.CommutativeSetMessage.OpType;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author score
 */
public class LwwMessageTest {
    
    @Test
    public void LwwOperationTest()
    {
      
         LwwMessage lwo1 = new LwwMessage(OpType.add, 'a', 1);
         LwwMessage lwo2 = new LwwMessage(OpType.add, "testOperation", 1);
         LwwMessage lwo3 = new LwwMessage(OpType.del, 123, 2);
         
         assertEquals(lwo1.getContent(), 'a');
         assertEquals(lwo2.getContent(), "testOperation");
         assertEquals(lwo3.getContent(), 123);
         
         assertEquals(lwo1.getime(), 1);
         assertEquals(lwo2.getime(),1);
         assertEquals(lwo3.getime(), 2);
         
         assertEquals(lwo1.getType(),OpType.add);
         assertEquals(lwo2.getType(),OpType.add);
         assertEquals(lwo3.getType(),OpType.del);
    }
}
