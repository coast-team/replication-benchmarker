/*
 *  Replication Benchmarker
 *  https://github.com/score-team/replication-benchmarker/
 *  Copyright (C) 2013 LORIA / Inria / SCORE Team
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package jbenchmarker.logootsplitO;

import crdt.CRDTMessage;
import crdt.PreconditionException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import jbenchmarker.core.SequenceOperation;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author Stephane Martin <stephane@stephanemartin.fr>
 */
public class LogootSAlgoTest {

    static List<Character> getListFromString(String str) {
        List<Character> l = new LinkedList();
        for (int i = 0; i < str.length(); i++) {
            l.add(str.charAt(i));
        }
        return l;
    }
    LogootSAlgo alg1;
    LogootSAlgo alg2;
 LogootSAlgo alg3;
    @Before
    public void setup() {
        alg1 = new LogootSAlgo(new LogootSDocumentD(), 1);
        alg2 = new LogootSAlgo(new LogootSDocumentD(), 2);
        alg3 = new LogootSAlgo(new LogootSDocumentD(), 3);
    }

    @Test
    public void testSomeMethod() {
    }

    static LogootSBlock[] getFromMap(HashMap<List<Integer>, LogootSBlock> map) {

        return map.values().toArray(new LogootSBlock[map.size()]);

    }
    @Test
    public void adddel() throws PreconditionException{
                CRDTMessage p=alg3.insert(0, "abcd");
        alg2.applyRemote(p);
        alg1.applyRemote(p);
        assertEquals("abcd", alg2.lookup());
        CRDTMessage p2=alg2.remove(0, 2);
        CRDTMessage p3=alg2.remove(0, 2);
        
        alg1.applyRemote(p2);
        assertEquals("cd", alg1.lookup());
        alg1.applyRemote(p3);
        assertEquals("", alg1.lookup());
        
        
        alg3.applyRemote(p3);
        assertEquals("ab", alg3.lookup());
        alg3.applyRemote(p2);
        assertEquals("", alg3.lookup());

    }
    
    @Test
    public void SimpleAddDelTest() throws PreconditionException {

        
        
        
        CRDTMessage op1 = alg1.insert(0, "Test1234");
        assertEquals("Test1234", alg1.lookup());




        CRDTMessage op2 = alg1.insert(5, "haha");
        assertEquals("Test1haha234", alg1.lookup());


        alg2.applyRemote(op2);
        assertEquals("haha", alg2.lookup());

        alg2.applyRemote(op1);
        assertEquals("Test1haha234", alg2.lookup());

        alg3.applyRemote(op1);
        alg3.applyRemote(op2);
        assertEquals("Test1haha234", alg3.lookup());


        /**
         * Del
         */
        CRDTMessage op3 = alg3.remove(4, 6);

        assertEquals("Test34", alg3.lookup());
        alg2.applyRemote(op3);
        assertEquals("Test34", alg2.lookup());

        /**
         * Make another del
         */
        assertEquals("Test1haha234", alg1.lookup());
        CRDTMessage op4 = alg1.remove(3, 4);
        assertEquals("Tesha234", alg1.lookup());

        //assertEquals(2,op4.lid.size());


        /**
         * integration of del
         */
        alg1.applyRemote(op3);

        assertEquals("Tes34", alg1.lookup());

        alg2.applyRemote(op4);

        assertEquals("Tes34", alg2.lookup());


        alg3.applyRemote(op4);

        assertEquals("Tes34", alg3.lookup());

    }
    

    

    @Test
    public void testEmpty() {
        assertEquals("", alg1.lookup());
    }

    @Test
    public void testInsert() throws PreconditionException {
        String content = "abcdejk", c2 = "fghi";
        int pos = 3;      
        alg1.applyLocal(SequenceOperation.insert(0, content));
        assertEquals(content, alg1.lookup());
        alg1.applyLocal(SequenceOperation.insert(pos, c2));
        assertEquals(content.substring(0, pos) + c2 + content.substring(pos), alg1.lookup());        
    }
        
    @Test
    public void testDelete() throws PreconditionException {
        String content = "abcdefghijk";
        int pos = 3, off = 4;       
        alg1.applyLocal(SequenceOperation.insert(0, content));
        assertEquals(content, alg1.lookup());
        alg1.applyLocal(SequenceOperation.delete(pos, off));
        assertEquals(content.substring(0, pos) + content.substring(pos+off), alg1.lookup());        
    }
    
    @Test
    public void testConcurrentDelete() throws PreconditionException {
        String content = "abcdefghij";
        CRDTMessage m1 = alg1.applyLocal(SequenceOperation.insert(0, content));
        assertEquals(content, alg1.lookup());
        alg1.applyLocal(SequenceOperation.insert(2, "2"));
        assertEquals("ab2cdefghij", alg1.lookup());
        alg1.applyLocal(SequenceOperation.insert(7, "7"));
        assertEquals("ab2cdef7ghij", alg1.lookup());
        
    
        alg2.applyRemote(m1);
        assertEquals(content, alg2.lookup());
        CRDTMessage m2 = alg2.applyLocal(SequenceOperation.delete(1, 8));
        assertEquals("aj", alg2.lookup());
        alg1.applyRemote(m2);
        assertEquals("a27j", alg1.lookup());
    }
    
    @Test
    public void testMultipleDeletions() throws PreconditionException {
         
        String content = "abcdefghij";
        CRDTMessage m1 = alg1.applyLocal(SequenceOperation.insert(0, content));
        alg1.applyLocal(SequenceOperation.insert(2, "28"));
        assertEquals("ab28cdefghij", alg1.lookup());
        alg1.applyLocal(SequenceOperation.insert(10, "73"));
        assertEquals("ab28cdefgh73ij", alg1.lookup());
        CRDTMessage m2 = alg1.applyLocal(SequenceOperation.delete(3, 8));
        assertEquals("ab23ij", alg1.lookup());
        
        
        alg2.applyRemote(m1);
        
        alg2.applyLocal(SequenceOperation.insert(4, "01"));
        assertEquals("abcd01efghij", alg2.lookup());
        alg2.applyRemote(m2);
        
        assertEquals("ab01ij", alg2.lookup());
        
    }
    
  
    
  
    
    @Test
    public void testUpdate() throws PreconditionException {
        String content = "abcdefghijk", upd = "xy";
        int pos = 3, off = 5;       
        alg1.applyLocal(SequenceOperation.insert(0, content));
        assertEquals(content, alg1.lookup());
        alg1.applyLocal(SequenceOperation.replace(pos, off, upd));
        assertEquals(content.substring(0, pos) + upd + content.substring(pos+off), alg1.lookup());        
    }
}