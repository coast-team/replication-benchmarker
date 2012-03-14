/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
