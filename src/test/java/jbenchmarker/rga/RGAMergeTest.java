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
package jbenchmarker.rga;

import crdt.PreconditionException;
import jbenchmarker.core.SequenceOperation;
import jbenchmarker.factories.RGAFactory;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class RGAMergeTest {

    private static final int REPLICA_ID = 7;
    private RGAMerge replica;

    @Before
    public void setUp() throws Exception {
        replica = (RGAMerge) new RGAFactory().create(REPLICA_ID);
    }
    
    @Test
    public void testEmptyTree() {
        assertEquals("", replica.lookup());
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
