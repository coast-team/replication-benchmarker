/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker.woot.wooth.undo;

import jbenchmarker.woot.WootIdentifier;
import jbenchmarker.woot.WootOperation;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author urso
 */
public class WootHashReuseTest {

    WootHashReuse<Character> doc;
    final WootIdentifier a = new WootIdentifier(1, 0), b = new WootIdentifier(2, 3);
    
    @Before
    public void initialize() {
        doc = WootHashReuse.newDocument();
    }

    public WootHashReuseTest() {
    }

    @Test
    public void testInsert() {
        assertEquals(0, doc.viewLength());
        doc.add(a, 'a', WootIdentifier.IB, WootIdentifier.IE);
        assertEquals(1, doc.viewLength());
        assertEquals("a", doc.view());
        doc.add(b, 'b', a, WootIdentifier.IE);
        assertEquals(2, doc.viewLength());
        assertEquals("ab", doc.view());
    }

    @Test
    public void testDelete() {
        testInsert();
        WootOperation op = doc.delete(null, b);
        doc.apply(op);
        assertEquals(1, doc.viewLength());
        assertEquals("a", doc.view());   
        op = doc.delete(null, a);
        doc.apply(op);
        assertEquals(0, doc.viewLength());
        assertEquals("", doc.view()); 
    }
    
    @Test
    public void testReappear() {
        testDelete();
        WootOperation op = doc.insert(null, WootIdentifier.IB, WootIdentifier.IE, 'b');
        doc.apply(op);
        assertEquals(1, doc.viewLength());
        assertEquals("b", doc.view());
        assertEquals(op.getId(), b);
    }
}
