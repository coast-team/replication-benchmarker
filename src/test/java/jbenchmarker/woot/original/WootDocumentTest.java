/**
 * Replication Benchmarker
 * https://github.com/score-team/replication-benchmarker/
 * Copyright (C) 2011 INRIA / LORIA / SCORE Team
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
package jbenchmarker.woot.original;

import jbenchmarker.trace.SequenceOperation;
import jbenchmarker.woot.WootIdentifier;
import jbenchmarker.woot.WootOperation;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author urso
 */
public class WootDocumentTest {
    
    WootOriginalNode a, b, c, d, e, f, g, h, x, z;
    
    public WootDocumentTest() {
        z = new WootOriginalNode(new WootIdentifier(1,3), WootOriginalNode.CB, WootOriginalNode.CE, 'z', false);
        a = new WootOriginalNode(new WootIdentifier(1,1), WootOriginalNode.CB, WootOriginalNode.CE, 'a', true);
        b = new WootOriginalNode(new WootIdentifier(2,1), a, WootOriginalNode.CE, 'b', true);
        c = new WootOriginalNode(new WootIdentifier(2,2), b, WootOriginalNode.CE, 'c', false);
        d = new WootOriginalNode(new WootIdentifier(3,1), WootOriginalNode.CB, a, 'd', false);
        e = new WootOriginalNode(new WootIdentifier(3,2), a, b, 'e', false);
        f = new WootOriginalNode(new WootIdentifier(3,3), e, f, 'f', false);
        g = new WootOriginalNode(new WootIdentifier(3,4), a, e, 'g', true);
        h = new WootOriginalNode(new WootIdentifier(4,1), g, b, 'h', true);
        x = new WootOriginalNode(new WootIdentifier(1,2), WootOriginalNode.CB, WootOriginalNode.CE, 'x', true);
    }

    // helpers
    WootOperation ins(WootOriginalDocument r, WootOriginalNode n) {
        return r.insert(SequenceOperation.insert(0, 0, null, null), 
                n.getId(), n.getCp().getId(), n.getCn().getId(), n.getContent());
    }
    WootOperation del(WootOriginalDocument r, WootOriginalNode n) {
        return r.delete(SequenceOperation.delete(0, 0, 0, null), n.getId());
    }
    
    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
 
    /**
     * Test of view method, of class WootOriginalDocument.
     */
    @Test
    public void testView() {
        System.out.println("view");
        WootOriginalDocument instance = new WootOriginalDocument();

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
     * Test of getVisible method, of class WootOriginalDocument.
     */
    @Test
    public void testGetVisible() {
        System.out.println("getVisible");
        WootOriginalDocument instance = new WootOriginalDocument();
        
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
     * Test of getVisible method, of class WootOriginalDocument.
     */
    @Test
    public void testGetPrevious() {
        System.out.println("getPrevious");
        WootOriginalDocument instance = new WootOriginalDocument();
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
     * Test of nextVisible method, of class WootOriginalDocument.
     */
    @Test
    public void testNextVisible() {
        System.out.println("nextVisible");
        WootOriginalDocument instance = new WootOriginalDocument();

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
        WootOriginalDocument instance = new WootOriginalDocument();
        
        instance.apply(ins(instance,a));
        assertEquals("a", instance.view());
        
        instance.apply(ins(instance,b));
        assertEquals("ab", instance.view());

        instance.apply(ins(instance,d));
        assertEquals("dab", instance.view());

        instance.apply(del(instance,d));
        assertEquals("ab", instance.view());
        
        instance.apply(ins(instance,e));
        assertEquals("aeb", instance.view());

        instance.apply(del(instance,e));
        assertEquals("ab", instance.view());
    
        instance.apply(ins(instance,g));
        assertEquals("agb", instance.view());

        instance.apply(ins(instance,h));
        assertEquals("aghb", instance.view());
    }
    
    @Test
    public void wootPuzzle() {
        System.out.println("Woot Puzzle");
        WootOriginalDocument instance = new WootOriginalDocument();
        
        instance.apply(ins(instance,a));
        instance.apply(ins(instance,d));
        instance.apply(ins(instance,x));
        assertTrue(x.getId().compareTo(a.getId()) > 0);
        assertTrue(x.getId().compareTo(d.getId()) < 0);
        assertEquals("dax", instance.view());
    }
}