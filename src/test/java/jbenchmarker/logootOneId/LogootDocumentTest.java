/**
 * Replication Benchmarker
 * https://github.com/score-team/replication-benchmarker/
 * Copyright (C) 2013 LORIA / Inria / SCORE Team
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
package jbenchmarker.logootOneId;
import org.junit.Ignore;
import jbenchmarker.logootOneId.*;
import java.math.BigDecimal;
import jbenchmarker.core.SequenceOperation;



import org.junit.Test;
import static org.junit.Assert.*;
/**
 *
 * @author mehdi
 */
public class LogootDocumentTest {

    // helpers
    LogootOneIdOperation ins(LogootOneIdentifier n, char c) {
        return LogootOneIdOperation.insert(SequenceOperation.insert(0, ""),
                n, c);
    }

    LogootOneIdOperation del(LogootOneIdentifier n) {
        return LogootOneIdOperation.Delete(SequenceOperation.delete(0, 0), n);
    }
    @Test
    public void testapply() {
        LogootOneIdDocument LM = new LogootOneIdDocument(1, new BoundaryStrategy(100));

        LogootOneIdentifier P = new LogootOneIdentifier(new BigDecimal("0.20"));
        LogootOneIdentifier A = new LogootOneIdentifier(BigDecimal.valueOf(0.19));
        LogootOneIdentifier Q = new LogootOneIdentifier(BigDecimal.valueOf(0.21));
        LogootOneIdentifier B = new LogootOneIdentifier(BigDecimal.valueOf(0.23));
        LogootOneIdentifier C = new LogootOneIdentifier(BigDecimal.valueOf(0.24));

        LM.apply(ins(P, 'e'));
        LM.apply(ins(Q, 'c'));
        assertEquals("ec", LM.view());
        //-----------   

        LM.apply(ins(A, 'K'));
        LM.apply(ins(B, 'L'));
        assertEquals("KecL", LM.view());
//        //----------
        LM.apply(ins(C, 'd'));
        assertEquals("KecLd", LM.view());
        //-----------
        LM.apply(del(Q));
        assertEquals("KeLd", LM.view());
    }
    
    //@Ignore
    @Test
    public void dicto() {
        LogootOneIdDocument LM = new LogootOneIdDocument(1, new BoundaryStrategy(100));

        LogootOneIdentifier P = new LogootOneIdentifier(new BigDecimal("0.20"));
        LogootOneIdentifier A = new LogootOneIdentifier(BigDecimal.valueOf(0.22));
        LogootOneIdentifier Q = new LogootOneIdentifier(BigDecimal.valueOf(0.21));
        LogootOneIdentifier B = new LogootOneIdentifier(BigDecimal.valueOf(0.23));
        LogootOneIdentifier C = new LogootOneIdentifier(BigDecimal.valueOf(0.24));
        LogootOneIdentifier D = new LogootOneIdentifier(BigDecimal.valueOf(0.22));


        LM.apply(ins(P, 'e'));
        LM.apply(ins(Q, 'c'));
        LM.apply(ins(A, 'c'));
        LM.apply(ins(B, 'c'));
        LM.apply(ins(C, 'c'));


        assertEquals(3, LM.dicho(D));
    }

}
