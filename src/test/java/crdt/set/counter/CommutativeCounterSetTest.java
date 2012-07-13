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
package crdt.set.counter;

import crdt.CRDTMessage;
import crdt.OperationBasedOneMessage;
import java.util.*;
import crdt.set.CrdtSetGeneric;
import crdt.PreconditionException;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author score
 */
public class CommutativeCounterSetTest<T> {

    CrdtSetGeneric tcs = new CrdtSetGeneric();

    @Test
    public void test() throws PreconditionException {
        tcs.runTests(new CommutativeCounterSet());
    }

    @Test
    public void testConcurAddDel() throws PreconditionException {
        Set<T> s = tcs.testApplyConcurAddDel(new CommutativeCounterSet(), new CommutativeCounterSet());
        assertEquals(new HashSet() {

            {
                add('a');
                add('b');
            }
        }, s);
    }

    @Test
    public void testConcurAddThenDel() throws PreconditionException {
        Set<T> s = tcs.testApplyConcurAddThenDel(new CommutativeCounterSet(), new CommutativeCounterSet());
        assertEquals(new HashSet() {

            {
                add('a');
            }
        }, s);
    }

    @Test
    public void testRemoveAfterConcuAdd() throws PreconditionException {
        Set<T> s = tcs.testApplyRemoveAfterConcuAdd(new CommutativeCounterSet(), new CommutativeCounterSet());
        assertEquals(new HashSet(), s);
    }

    @Test(expected = PreconditionException.class)
    public void testExceptionAdd() throws PreconditionException {

        CrdtSetGeneric tcs = new CrdtSetGeneric();
        tcs.testAddException(new CommutativeCounterSet());
    }

    @Test(expected = PreconditionException.class)
    public void testExceptionRmv() throws PreconditionException {

        CrdtSetGeneric tcs = new CrdtSetGeneric();
        tcs.testRemoveException(new CommutativeCounterSet());
    }

    @Test
    public void testapply() throws PreconditionException {
        //verify counter
        CommutativeCounterSet Rep1 = new CommutativeCounterSet();
        CommutativeCounterSet Rep2 = new CommutativeCounterSet();
        CommutativeCounterSet Rep3 = new CommutativeCounterSet();
        CommutativeCounterSet result2 = new CommutativeCounterSet();//remote

        CRDTMessage Op1 = Rep1.innerAdd('a');
        CRDTMessage Op2 = Rep2.innerAdd('a');
        CRDTMessage Op3 = Rep3.innerAdd('a');

        result2.applyRemote(Op1);
        result2.applyRemote(Op2);
        result2.applyRemote(Op3);

        assertEquals(result2.getMap().size(), 1);
        assertEquals(result2.getMap().get('a'), 3);
    }

    @Test
    public void testapply2() throws PreconditionException {
        CommutativeCounterSet Rep1 = new CommutativeCounterSet();
        CommutativeCounterSet Rep2 = new CommutativeCounterSet();

        CRDTMessage Op1 =  Rep1.innerAdd('a');
        CRDTMessage Op2 = Rep1.innerRemove('a');
        CRDTMessage Op3 =  Rep2.innerAdd('a');

        Rep1.applyRemote(Op3);
        Rep2.applyRemote(Op1);
        Rep2.applyRemote(Op2);

        assertEquals(Rep1.getMap().size(), 1);
        assertEquals(Rep2.getMap().size(), 1);

        assertEquals(getCounterMessage(Op1).getCounter(), 1);
        assertEquals(getCounterMessage(Op2).getCounter(), -1);
        assertEquals(getCounterMessage(Op3).getCounter(), 1);
        assertEquals(Rep1.lookup(), Rep2.lookup());
    }
    CounterMessage<T> getCounterMessage(CRDTMessage mess){
        return(CounterMessage)((OperationBasedOneMessage) mess).getOperation();
    }
}
