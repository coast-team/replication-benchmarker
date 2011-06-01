package jbenchmarker.woot;

import java.util.NoSuchElementException;
import jbenchmarker.woot.wooto.WootOptimizedDocument;
import java.util.List;
import jbenchmarker.core.Operation;
import jbenchmarker.trace.IncorrectTrace;
import jbenchmarker.trace.TraceOperation;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author urso
 */
public class WootOMergeTest {

    // helpers
    TraceOperation insert(int p, String s) {
        return TraceOperation.insert(1, p, s, null);
    }
    TraceOperation delete(int p, int o) {
         return TraceOperation.delete(1, p, o, null);
    }
    
    /**
     * Test of generateLocal method, of class WootMerge.
     */
    @Test
    public void testGenerateLocal() throws IncorrectTrace {
        System.out.println("generateLocal");
        WootMerge instance = new WootMerge(new WootOptimizedDocument(), 1);
        
        List<Operation> r = instance.generateLocal(insert(0,"a"));
        assertEquals(1, r.size());
        assertEquals('a', ((WootOperation) r.get(0)).getContent());
        assertEquals("a", instance.getDoc().view());        

        r = instance.generateLocal(insert(0,"bc"));
        assertEquals(2, r.size());
        assertEquals("bca", instance.getDoc().view());         

        r = instance.generateLocal(delete(0,1));
        assertEquals(1, r.size());
        assertEquals("ca", instance.getDoc().view()); 

        r = instance.generateLocal(insert(1,"efg"));
        assertEquals(3, r.size());
        assertEquals("cefga", instance.getDoc().view()); 

        r = instance.generateLocal(delete(1,2));
        assertEquals(2, r.size());
        assertEquals("cga", instance.getDoc().view()); 

        r = instance.generateLocal(delete(1,2));
        assertEquals(2, r.size());
        assertEquals("c", instance.getDoc().view()); 
    }
    
    /**
     * Testing out of bound insert.
     */
    @Test(expected=NoSuchElementException.class)
    public void testGenerateInsIncorrect() throws IncorrectTrace {
        WootMerge instance = new WootMerge(new WootOptimizedDocument(), 1);
        
        instance.generateLocal(insert(10,"a"));
        fail("Out of bound insert not detected.");    
    }
    
    /**
     * Testing out of bound del.
     */
    @Test(expected=NoSuchElementException.class)
    public void testGenerateDelIncorrect() throws IncorrectTrace {
        WootMerge instance = new WootMerge(new WootOptimizedDocument(), 1);
        
        instance.generateLocal(delete(0,1));
        fail("Out of bound delete not detected.");    
    }
    
    
    @Test
    public void accent() throws IncorrectTrace {
        WootMerge instance = new WootMerge(new WootOptimizedDocument(), 1);
        List<Operation> r = instance.generateLocal(insert(0,"à"));
        assertEquals(1, r.size());
    }
   
}