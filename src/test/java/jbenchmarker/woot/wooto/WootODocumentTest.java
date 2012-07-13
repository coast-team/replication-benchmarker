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
package jbenchmarker.woot.wooto;

import jbenchmarker.woot.WootDocument;
import jbenchmarker.core.SequenceOperation;
import jbenchmarker.woot.WootOperation;
import jbenchmarker.woot.WootIdentifier;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author urso
 */
public class WootODocumentTest {

   
    WootOptimizedNode a, b, c, d, e, f, g, h, x, z;
    
    public WootODocumentTest() {
        z = new WootOptimizedNode(new WootIdentifier(1,3), 1, 'z', false);
        a = new WootOptimizedNode(new WootIdentifier(1,1), 1, 'a', true);
        b = new WootOptimizedNode(new WootIdentifier(2,1), 2, 'b', true);
        c = new WootOptimizedNode(new WootIdentifier(2,2), 3, 'c', false);
        d = new WootOptimizedNode(new WootIdentifier(3,1), 2, 'd', false);
        e = new WootOptimizedNode(new WootIdentifier(3,2), 3, 'e', false);
        f = new WootOptimizedNode(new WootIdentifier(3,3), 4, 'f', false);
        g = new WootOptimizedNode(new WootIdentifier(3,4), 3, 'g', true);
        h = new WootOptimizedNode(new WootIdentifier(4,1), 4, 'h', true);
        x = new WootOptimizedNode(new WootIdentifier(1,2), 1, 'x', true);
    }

    // helpers
    WootOperation ins(WootDocument r, WootOptimizedNode n, WootIdentifier cp, WootIdentifier cn) {
        return r.insert(SequenceOperation.insert( 0, ""), 
                n.getId(), cp, cn, n.getContent());
    }
    WootOperation del(WootDocument r, WootOptimizedNode n) {
        return r.delete(SequenceOperation.delete( 0, 0), n.getId());
    }
    
    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
 
    /**
     * Test of view method, of class WootOptimizedDocument.
     */
    @Test
    public void testView() {
        System.out.println("view");
        WootOptimizedDocument instance = new WootOptimizedDocument();

        assertEquals("", instance.view());
        
        instance.getElements().add(1,a);
        assertEquals("a", instance.view());
    
        instance.getElements().add(2,b);
        assertEquals("ab", instance.view());        

        instance.getElements().add(3,c);
        assertEquals("ab", instance.view());      

        instance.getElements().add(0,d);
        assertEquals("ab", instance.view());      
    }


    /**
     * Test of getVisible method, of class WootOptimizedDocument.
     */
    @Test
    public void testGetVisible() {
        System.out.println("getVisible");
        WootOptimizedDocument instance = new WootOptimizedDocument();
        
        instance.getElements().add(1,z);
        instance.getElements().add(1,a);
        assertEquals(1, instance.getVisible(0));  // cause a < z
        
        instance.getElements().add(1,d);
        assertEquals(2, instance.getVisible(0));  

        instance.getElements().add(3,b);
        assertEquals(2, instance.getVisible(0));
        assertEquals(3, instance.getVisible(1));
        
        instance.getElements().add(3,e);
        assertEquals(2, instance.getVisible(0));
        assertEquals(4, instance.getVisible(1));
    }

    /**
     * Test of getVisible method, of class WootOptimizedDocument.
     */
    @Test
    public void testGetPrevious() {
        System.out.println("getPrevious");
        WootOptimizedDocument instance = new WootOptimizedDocument();
        assertEquals(0, instance.getPrevious(0));
        
        instance.getElements().add(1,z);
        assertEquals(0, instance.getPrevious(0));

        instance.getElements().add(1,a);
        assertEquals(0, instance.getPrevious(0));
        assertEquals(1, instance.getPrevious(1)); 

        instance.getElements().add(1,d);
        assertEquals(0, instance.getPrevious(0));
        assertEquals(2, instance.getPrevious(1)); 

        instance.getElements().add(3,b);
        assertEquals(2, instance.getPrevious(1)); 
        assertEquals(3, instance.getPrevious(2));                 
        
        instance.getElements().add(3,e);
        assertEquals(2, instance.getPrevious(1)); 
        assertEquals(4, instance.getPrevious(2));                
    }

    /**
     * Test of nextVisible method, of class WootOptimizedDocument.
     */
    @Test
    public void testNextVisible() {
        System.out.println("nextVisible");
        WootOptimizedDocument instance = new WootOptimizedDocument();

        instance.getElements().add(1,z);
        instance.getElements().add(1,a);
        assertEquals(1, instance.nextVisible(0));  // cause a < z
        
        instance.getElements().add(1,d);
        assertEquals(2, instance.nextVisible(0));  
        assertEquals(2, instance.nextVisible(1));  // cause a < z

        instance.getElements().add(3,b);
        assertEquals(2, instance.nextVisible(0));
        assertEquals(2, instance.nextVisible(1));
        assertEquals(3, instance.nextVisible(2));
        
        instance.getElements().add(3,e);
        assertEquals(2, instance.nextVisible(0));
        assertEquals(2, instance.nextVisible(1));
        assertEquals(4, instance.nextVisible(2));
        assertEquals(4, instance.nextVisible(3));
    }
    
    @Test
    public void testApply() {
        System.out.println("apply");
        WootOptimizedDocument instance = new WootOptimizedDocument();
        
        instance.apply(ins(instance,a, WootIdentifier.IB, WootIdentifier.IE));
        assertEquals("a", instance.view());
        
        instance.apply(ins(instance,b, a.getId(), WootIdentifier.IE));
        assertEquals("ab", instance.view());

        instance.apply(ins(instance,d, WootIdentifier.IB, a.getId()));
        assertEquals("dab", instance.view());

        instance.apply(del(instance,d));
        assertEquals("ab", instance.view());
        
        instance.apply(ins(instance,e, a.getId(), b.getId()));
        assertEquals("aeb", instance.view());

        instance.apply(del(instance,e));
        assertEquals("ab", instance.view());
    
        instance.apply(ins(instance,g, a.getId(), b.getId()));
        assertEquals("agb", instance.view());

        instance.apply(ins(instance,h, g.getId(), b.getId()));
        assertEquals("aghb", instance.view());
    }
    
    @Test
    public void wootPuzzle() {
        System.out.println("Woot Puzzle");
        WootOptimizedDocument instance = new WootOptimizedDocument();
        
        instance.apply(ins(instance,a, WootIdentifier.IB, WootIdentifier.IE));
        instance.apply(ins(instance,d, WootIdentifier.IB, a.getId()));
        instance.apply(ins(instance,x, WootIdentifier.IB, WootIdentifier.IE));
        assertTrue(x.getId().compareTo(a.getId()) > 0);
        assertTrue(x.getId().compareTo(d.getId()) < 0);
        assertEquals("dax", instance.view());
    }
}
