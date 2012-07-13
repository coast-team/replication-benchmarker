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
package crdt.set.observedremove;

import crdt.set.CommutativeSetMessage.OpType;
import java.util.HashSet;
import java.util.Set;
import static org.junit.Assert.assertEquals;
import org.junit.Test;


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
