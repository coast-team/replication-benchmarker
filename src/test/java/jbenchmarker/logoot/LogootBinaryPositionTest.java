/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker.logoot;

import jbenchmarker.logoot.LogootBinaryPosition.Component;
import jbenchmarker.logoot.LogootBinaryPosition.Direction;
import org.junit.Test;
import static org.junit.Assert.*;
import static collect.Utils.*;
import java.util.List;

/**
 *
 * @author urso
 */
public class LogootBinaryPositionTest {
    
    public LogootBinaryPositionTest() {
    }

    @Test
    public void testOrder() {
        LogootBinaryPosition a = new LogootBinaryPosition(toList(new Component(Direction.left, 23, 42))),
                b = new LogootBinaryPosition(toList(new Component(Direction.left, 23, 42), new Component(Direction.right, 12, 56))),
                c = new LogootBinaryPosition(toList(new Component(Direction.right, 23, 42))),
                d = new LogootBinaryPosition(toList(new Component(Direction.left, 25, 42))),
                e = new LogootBinaryPosition(toList(new Component(Direction.left, 23, 42), new Component(Direction.left, 12, 56))),
                f = new LogootBinaryPosition(toList(new Component(Direction.right, 23, 42), new Component(Direction.left, 12, 56))); 
        List<LogootBinaryPosition> order = toList(e, a, b, d, f, c);
           
        for (int i = 0; i < order.size(); ++i) {
            for (int j = 0; j < order.size(); ++j) {
               assertEquals("compare " + order.get(i) + "<" +  order.get(j), sigint(i-j), sigint(order.get(i).compareTo(order.get(j))));    
            }
        }
    }
}
