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
package jbenchmarker.ot.ttf.update;

import crdt.CRDTMessage;
import crdt.PreconditionException;
import jbenchmarker.core.MergeAlgorithm;
import jbenchmarker.core.ReplicaFactory;
import jbenchmarker.core.SequenceOperation;
import jbenchmarker.factories.TTFUFactories;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test of TTF with update 
 * @author urso
 */
public class TTFUpdateBasicTest {
    
    ReplicaFactory upd = new TTFUFactories.Update(), del = new TTFUFactories.DelWins();
    
    public TTFUpdateBasicTest() {
    }

    @Test
    public void testLWW() throws PreconditionException {
        MergeAlgorithm r1 = upd.create(1), r2 = upd.create(2);
        CRDTMessage ins = r1.applyLocal(SequenceOperation.insert(0, "abc"));
        assertEquals("abc", r1.lookup());
        r2.applyRemote(ins);
        assertEquals("abc", r2.lookup());
        CRDTMessage u1 = r1.applyLocal(SequenceOperation.replace(1, 1, "xy")),
                u2 = r2.applyLocal(SequenceOperation.replace(1, 2, "z"));
        assertEquals("axyc", r1.lookup());
        assertEquals("az", r2.lookup());  
        r1.applyRemote(u2);
        r2.applyRemote(u1);
        assertEquals("azy", r1.lookup());
        assertEquals("azy", r2.lookup());          
    }
    
    @Test
    public void testLWWDelete() throws PreconditionException {
        MergeAlgorithm r1 = upd.create(1), r2 = upd.create(2);
        CRDTMessage ins = r1.applyLocal(SequenceOperation.insert(0, "abc"));
        assertEquals("abc", r1.lookup());
        r2.applyRemote(ins);
        assertEquals("abc", r2.lookup());
        CRDTMessage u1 = r1.applyLocal(SequenceOperation.delete(1, 1)),
                u2 = r2.applyLocal(SequenceOperation.replace(1, 1, "z"));
        assertEquals("ac", r1.lookup());
        assertEquals("azc", r2.lookup());  
        r1.applyRemote(u2);
        r2.applyRemote(u1);
        assertEquals("azc", r1.lookup());
        assertEquals("azc", r2.lookup());          
    }
    
    @Test
    public void testDelWinsLWW() throws PreconditionException {
        MergeAlgorithm r1 = del.create(1), r2 = del.create(2);
        r1.setReplicaNumber(1);
        r2.setReplicaNumber(2);
        CRDTMessage ins = r1.applyLocal(SequenceOperation.insert(0, "abc"));
        assertEquals("abc", r1.lookup());
        r2.applyRemote(ins);
        assertEquals("abc", r2.lookup());
        CRDTMessage u1 = r1.applyLocal(SequenceOperation.replace(1, 1, "xy")),
                u2 = r2.applyLocal(SequenceOperation.replace(1, 2, "z"));
        assertEquals("axyc", r1.lookup());
        assertEquals("az", r2.lookup());  
        r1.applyRemote(u2);
        r2.applyRemote(u1);
        assertEquals("azy", r1.lookup());
        assertEquals("azy", r2.lookup());         
    }
    
    @Test
    public void testDelWins() throws PreconditionException {
        MergeAlgorithm r1 = del.create(1), r2 = del.create(2);
        r1.setReplicaNumber(1);
        r2.setReplicaNumber(2);
        CRDTMessage ins = r1.applyLocal(SequenceOperation.insert(0, "abc"));
        assertEquals("abc", r1.lookup());
        r2.applyRemote(ins);
        assertEquals("abc", r2.lookup());
        CRDTMessage u1 = r1.applyLocal(SequenceOperation.delete(1, 1)),
                u2 = r2.applyLocal(SequenceOperation.replace(1, 1, "z"));
        assertEquals("ac", r1.lookup());
        assertEquals("azc", r2.lookup());  
        r1.applyRemote(u2);
        r2.applyRemote(u1);
        assertEquals("ac", r1.lookup());
        assertEquals("ac", r2.lookup());          
    }
}