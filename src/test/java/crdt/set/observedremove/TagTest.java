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
