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
package collect;

import collect.ArraySkipList;
import java.util.NoSuchElementException;
import java.util.Iterator;
import java.util.List;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author urso
 */
public class ArraySkipListTest {
    
    public ArraySkipListTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Test
    public void testGetOutEmpty() {
        List<String> list = new ArraySkipList();
        assertNull(list.get(0));
    }
    
    @Test
    public void testGetOut() {
        List<String> list = new ArraySkipList();
        list.set(6, "toto");
        assertNull(list.get(10));
    }

    @Test
    public void testSizeEmpty() {
        List<String> list = new ArraySkipList();
        assertSame(0,list.size());
    } 
    
    @Test
    public void testGetNull() {
        List<String> list = new ArraySkipList();
        list.set(9, "toto");
        assertNull(list.get(6));
    }    
    
    @Test
    public void testSet() {
        List<String> list = new ArraySkipList();
        list.set(6, "toto");
        assertEquals("toto", list.get(6));
        assertSame(7,list.size());
    }
    
    @Test
    public void testSetTwo() {
        List<String> list = new ArraySkipList();
        list.set(6, "toto");
        list.set(10, "titi");
        assertNull(list.get(5));
        assertEquals("toto", list.get(6));
        assertNull(list.get(7));
        assertEquals("titi", list.get(10));
    }    
    
    @Test
    public void testIteratorEmpty() {
        List<String> list = new ArraySkipList();
        Iterator it = list.iterator();
        assertFalse(it.hasNext());
    }
        
    @Test(expected=NoSuchElementException.class)
    public void testIteratorNextEmpty() {
        List<String> list = new ArraySkipList();
        Iterator it = list.iterator();
        it.next();
    }
        
    @Test
    public void testIterator() {
        List<String> list = new ArraySkipList();
        list.set(6, "toto");
        list.set(10, "titi");
        Iterator it = list.iterator();
        assertTrue(it.hasNext());
        assertEquals("toto", it.next());
        assertTrue(it.hasNext());
        assertEquals("titi", it.next());
        assertFalse(it.hasNext());
    }     
}
