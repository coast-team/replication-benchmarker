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
package jbenchmarker.logoot;
import jbenchmarker.core.SequenceOperation;



import org.junit.Test;
import static org.junit.Assert.*;
/**
 *
 * @author mehdi
 */
public class LogootDocumentTest {

    // helpers
    LogootOperation ins(LogootIdentifier n, char c) {
        return LogootOperation.insert(SequenceOperation.insert(0, 0, "", null),
                n, c);
    }

    LogootOperation del(LogootIdentifier n) {
        return LogootOperation.Delete(SequenceOperation.delete(0, 0, 0, null), n);
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
