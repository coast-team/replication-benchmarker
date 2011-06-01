package jbenchmarker.logoot;
import jbenchmarker.trace.TraceOperation;



import org.junit.Test;
import static org.junit.Assert.*;
/**
 *
 * @author mehdi
 */
public class LogootDocumentTest {

    // helpers
    LogootOperation ins(LogootIdentifier n, char c) {
        return LogootOperation.insert(TraceOperation.insert(0, 0, null, null),
                n, c);
    }

    LogootOperation del(LogootIdentifier n) {
        return LogootOperation.Delete(TraceOperation.delete(0, 0, 0, null), n);
    }

    @Test
    public void testview() {
        System.out.println("Test LogootDocument ...");
        LogootDocument LM = new LogootDocument(100);
        assertEquals("", LM.view());

        LM.getDocument().add(1, 'a');
        assertEquals("a", LM.view());

        LM.getDocument().add(2, 'b');
        assertEquals("ab", LM.view());

    }

    @Test
    public void testapply() {
        LogootDocument LM = new LogootDocument(100);

        LogootIdentifier P = new LogootIdentifier(1);
        LogootIdentifier A = new LogootIdentifier(1);
        LogootIdentifier Q = new LogootIdentifier(1);
        LogootIdentifier B = new LogootIdentifier(1);
        LogootIdentifier C = new LogootIdentifier(1);

        P.addComponent(new Component(20, 4, 50));
        Q.addComponent(new Component(21, 4, 50));
        LM.apply(ins(P, 'e'));
        LM.apply(ins(Q, 'c'));
        assertEquals("ec", LM.view());
        //-----------   
        A.addComponent(new Component(19, 2, 100));
        B.addComponent(new Component(23, 4, 50));
        LM.apply(ins(A, 'K'));
        LM.apply(ins(B, 'L'));
        assertEquals("KecL", LM.view());
        //----------
        C.addComponent(new Component(24, 4, 50));
        LM.apply(ins(C, 'd'));
        assertEquals("KecLd", LM.view());
        //-----------
        LM.apply(del(Q));
        assertEquals("KeLd", LM.view());
    }

    @Test
    public void dicto() {
        LogootDocument LM = new LogootDocument(100);

        LogootIdentifier P = new LogootIdentifier(1);
        LogootIdentifier A = new LogootIdentifier(1);
        LogootIdentifier Q = new LogootIdentifier(1);
        LogootIdentifier B = new LogootIdentifier(1);
        LogootIdentifier C = new LogootIdentifier(1);
        LogootIdentifier D = new LogootIdentifier(1);

        P.addComponent(new Component(20, 4, 50));
        Q.addComponent(new Component(21, 4, 50));
        A.addComponent(new Component(22, 2, 100));
        B.addComponent(new Component(23, 4, 50));
        C.addComponent(new Component(24, 4, 50));

        LM.apply(ins(P, 'e'));
        LM.apply(ins(Q, 'c'));
        LM.apply(ins(A, 'c'));
        LM.apply(ins(B, 'c'));
        LM.apply(ins(C, 'c'));

        D.addComponent(new Component(22, 2, 100));

        assertEquals(3, LM.dicho(D));
    }

}
