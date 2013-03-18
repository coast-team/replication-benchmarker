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


/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker.logootsplit;

import crdt.CRDTMessage;
import crdt.PreconditionException;
import jbenchmarker.core.MergeAlgorithm;
import jbenchmarker.core.SequenceOperation;
import jbenchmarker.factories.LogootSFactory;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author urso
 */
public class LogootSAlgorithmTest {
    
    public LogootSAlgorithmTest() {
    }

    private MergeAlgorithm replica;

    @Before
    public void setUp() throws Exception {
        replica = (MergeAlgorithm) new LogootSFactory().create();
    }

    @Test
    public void testEmpty() {
        assertEquals("", replica.lookup());
    }

    @Test
    public void testInsert() throws PreconditionException {
        String content = "abcdejk", c2 = "fghi";
        int pos = 3;      
        replica.applyLocal(SequenceOperation.insert(0, content));
        assertEquals(content, replica.lookup());
        replica.applyLocal(SequenceOperation.insert(pos, c2));
        assertEquals(content.substring(0, pos) + c2 + content.substring(pos), replica.lookup());        
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
    public void testConcurrentDelete() throws PreconditionException {
        String content = "abcdefghij";
        CRDTMessage m1 = replica.applyLocal(SequenceOperation.insert(0, content));
        assertEquals(content, replica.lookup());
        replica.applyLocal(SequenceOperation.insert(2, "2"));
        assertEquals("ab2cdefghij", replica.lookup());
        replica.applyLocal(SequenceOperation.insert(7, "7"));
        assertEquals("ab2cdef7ghij", replica.lookup());
        
        MergeAlgorithm replica2 = (MergeAlgorithm) new LogootSFactory().create();
        replica2.setReplicaNumber(2);
        m1.execute(replica2);
        assertEquals(content, replica2.lookup());
        CRDTMessage m2 = replica2.applyLocal(SequenceOperation.delete(1, 8));
        assertEquals("aj", replica2.lookup());
        m2.execute(replica);
        assertEquals("a27j", replica.lookup());
    }
    
    @Test
    public void testMultipleDeletions() throws PreconditionException {
         
        String content = "abcdefghij";
        CRDTMessage m1 = replica.applyLocal(SequenceOperation.insert(0, content));
        replica.applyLocal(SequenceOperation.insert(2, "28"));
        assertEquals("ab28cdefghij", replica.lookup());
        replica.applyLocal(SequenceOperation.insert(10, "73"));
        assertEquals("ab28cdefgh73ij", replica.lookup());
        CRDTMessage m2 = replica.applyLocal(SequenceOperation.delete(3, 8));
        assertEquals("ab23ij", replica.lookup());
        
        MergeAlgorithm replica2 = (MergeAlgorithm) new LogootSFactory().create();
        replica2.setReplicaNumber(2);
        m1.execute(replica2);
        replica2.applyLocal(SequenceOperation.insert(4, "01"));
        assertEquals("abcd01efghij", replica2.lookup());
        m2.execute(replica2);
        assertEquals("ab01ij", replica2.lookup());
        
    }
    
    @Test
    public void testMultipleUpdates() throws PreconditionException {
         
        String content = "abcdefghij";
        CRDTMessage m1 = replica.applyLocal(SequenceOperation.insert(0, content));
        replica.applyLocal(SequenceOperation.insert(2, "2"));
        replica.applyLocal(SequenceOperation.insert(7, "7"));
        assertEquals("ab2cdef7ghij", replica.lookup());
        CRDTMessage m2 = replica.applyLocal(SequenceOperation.update(1, 10,"test"));
        assertEquals("atestj", replica.lookup());
        
        MergeAlgorithm replica2 = (MergeAlgorithm) new LogootSFactory().create();
        replica2.setReplicaNumber(2);
        m1.execute(replica2);
        replica2.applyLocal(SequenceOperation.insert(4, "01"));
        m2.execute(replica2);
        assertEquals("atest01j", replica2.lookup()); 
    } 

    
    @Test
    public void testConcurrentUpdate() throws PreconditionException{
        String content = "abcdefghij";
        CRDTMessage m1 = replica.applyLocal(SequenceOperation.insert(0, content));
        replica.applyLocal(SequenceOperation.update(2, 4, "27"));
        assertEquals("ab27ghij", replica.lookup());
        
        MergeAlgorithm replica2 = (MergeAlgorithm) new LogootSFactory().create();
        replica2.setReplicaNumber(2);
        m1.execute(replica2);
        CRDTMessage m2 = replica2.applyLocal(SequenceOperation.update(1, 8, "test"));
        m2.execute(replica);
        assertEquals("atest27j", replica.lookup());
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
