/**
 * Replication Benchmarker
 * https://github.com/score-team/replication-benchmarker/
 * Copyright (C) 2012 LORIA / Inria / SCORE Team
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
