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
