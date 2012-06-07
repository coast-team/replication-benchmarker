/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.set.twophases;

import crdt.set.CommutativeSetMessage.OpType;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author score
 */
public class TwoPhaseOperationTest {
    
    @Test
        public void TwoPhaseOperationTest()
    {
         TwoPhasesMessage tpOp1 = new TwoPhasesMessage(OpType.add, 'a');
         TwoPhasesMessage tpOp2 = new TwoPhasesMessage(OpType.add, "TestString");
         TwoPhasesMessage tpOp3 = new TwoPhasesMessage(OpType.del, 123);
         
         assertEquals(tpOp1.getContent(), 'a');
         assertEquals(tpOp1.getType(), OpType.add);
         assertEquals(tpOp2.getContent(), "TestString");
         assertEquals(tpOp2.getType(), OpType.add);
         
         assertEquals(tpOp3.getContent(), 123);
         assertEquals(tpOp3.getType(), OpType.del);
         
    }
    
}
