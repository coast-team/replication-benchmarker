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
package crdt.set.counter;

import crdt.set.counter.CounterMessage;
import crdt.set.SetOperation.OpType;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author score
 */
public class CounterMessageTest<T> {
    
     @Test
    public void CounterOperationTest()
    {       
         CounterMessage cOp1 = new CounterMessage('a', 1);
         CounterMessage cOp2 = new CounterMessage("TestOperation",2);
         CounterMessage cOp3 = new CounterMessage('a', 3);
         
         assertEquals(cOp1.getCounter(), 1);
         assertEquals(cOp2.getCounter(), 2);
         assertEquals(cOp3.getCounter(), 3);
         
         assertEquals(cOp1.getContent(), 'a');
         assertEquals(cOp2.getContent(), "TestOperation");
         assertEquals(cOp3.getContent(), 'a');
    }
    
}
