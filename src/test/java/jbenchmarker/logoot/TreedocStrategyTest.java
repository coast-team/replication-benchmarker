/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker.logoot;

import java.util.List;
import jbenchmarker.factories.LogootFactory;
import org.junit.Test;
import static org.junit.Assert.*;
import static collect.Utils.*;
import jbenchmarker.logoot.LogootBinaryPosition.Component;
import jbenchmarker.logoot.LogootBinaryPosition.Direction;

/**
 *
 * @author urso
 */
public class TreedocStrategyTest {
    
    public TreedocStrategyTest() {
    }

    @Test
    public void testRoot() {
        TreedocStrategy ts = new TreedocStrategy();
        LogootDocument replica = new LogootDocument(1, ts);
        List<ListIdentifier> l = ts.generateLineIdentifiers(replica, ts.begin(), ts.end(), 1);
        
        assertEquals(1, l.size());
        ListIdentifier i = l.get(0);
        assertEquals(1, i.length());
        
        l = ts.generateLineIdentifiers(replica, i, ts.end(), 1);
        assertEquals(1, l.size());
        ListIdentifier j = l.get(0);
        assertEquals(2, j.length());
        assertTrue(i.compareTo(j) < 0);
    }
    
    @Test
    public void testTwo() {
        TreedocStrategy ts = new TreedocStrategy();
        LogootDocument replica = new LogootDocument(1, ts);
        List<ListIdentifier> l = ts.generateLineIdentifiers(replica, ts.begin(), ts.end(), 2);
        
        assertEquals(toList(new LogootBinaryPosition(Direction.left, 1, 0), new LogootBinaryPosition(Direction.right, 1, 1)), l);
    }
    
    @Test
    public void testTreeRight() {
        TreedocStrategy ts = new TreedocStrategy();
        LogootDocument replica = new LogootDocument(1, ts);
        List<ListIdentifier> l = ts.generateLineIdentifiers(replica, ts.begin(), ts.end(), 1), 
                l2 = ts.generateLineIdentifiers(replica, l.get(0), ts.end(), 14);
        assertTrue(isSorted(l2));
        assertEquals(5, maxLength(l2));
        assertGreaterThan(l2.get(0), l.get(0));
    }
    
    @Test
    public void testTreeLeft() {
        TreedocStrategy ts = new TreedocStrategy();
        LogootDocument replica = new LogootDocument(1, ts);
        List<ListIdentifier> l = ts.generateLineIdentifiers(replica, ts.begin(), ts.end(), 14);       
        int pos = 12;
        
        assertTrue(isSorted(l));
        assertEquals(3, maxLength(l));
        assertTrue(((LogootBinaryPosition) l.get(pos + 1)).isRightSonOf((LogootBinaryPosition) l.get(pos)));

        List<ListIdentifier> l2 = ts.generateLineIdentifiers(replica, l.get(pos), l.get(pos+1), 14);
        assertTrue(isSorted(l2));
        assertEquals(7, maxLength(l2));
        assertGreaterThan(l2.get(0), l.get(pos));
        assertGreaterThan(l.get(pos + 1), l2.get(l2.size() - 1));
    }
    
    public static int maxLength(List<ListIdentifier> list) {
        int m = 0;
        for (ListIdentifier id : list) {
            if (id.length() > m) {
                m = id.length();
            }
        }
        return m;
    } 
}
