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
package jbenchmarker.logoot;
import jbenchmarker.core.SequenceOperation;
import jbenchmarker.factories.LogootFactory;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
/**
 *
 * @author mehdi
 */
public class LogootDocumentTest {
    LogootDocument LD;
    
    @Before
    public void setUp() throws Exception {
        LD = LogootFactory.createDoc(1, 7, 10);
    }
    // helpers
    LogootOperation ins(LogootIdentifier n, char c) {
        return LogootOperation.insert(SequenceOperation.insert( 0, ""),
                n, c);
    }

    LogootOperation del(LogootIdentifier n) {
        return LogootOperation.Delete(SequenceOperation.delete( 0, 0), n);
    }

    @Test
    public void testapply() {
        LogootIdentifier P = new LogootIdentifier(1);
        LogootIdentifier A = new LogootIdentifier(1);
        LogootIdentifier Q = new LogootIdentifier(1);
        LogootIdentifier B = new LogootIdentifier(1);
        LogootIdentifier C = new LogootIdentifier(1);

        P.addComponent(new Component(20, 4, 50));
        Q.addComponent(new Component(21, 4, 50));
        LD.apply(ins(P, 'e'));
        LD.apply(ins(Q, 'c'));
        assertEquals("ec", LD.view());
        //-----------   
        A.addComponent(new Component(19, 2, 100));
        B.addComponent(new Component(23, 4, 50));
        LD.apply(ins(A, 'K'));
        LD.apply(ins(B, 'L'));
        assertEquals("KecL", LD.view());
        //----------
        C.addComponent(new Component(24, 4, 50));
        LD.apply(ins(C, 'd'));
        assertEquals("KecLd", LD.view());
        //-----------
        LD.apply(del(Q));
        assertEquals("KeLd", LD.view());
    }

    @Test
    public void dicto() {
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

        LD.apply(ins(P, 'e'));
        LD.apply(ins(Q, 'c'));
        LD.apply(ins(A, 'c'));
        LD.apply(ins(B, 'c'));
        LD.apply(ins(C, 'c'));

        D.addComponent(new Component(22, 2, 100));

        assertEquals(3, LD.dicho(D));
    }

}
