/*
 *  Replication Benchmarker
 *  https://github.com/score-team/replication-benchmarker/
 *  Copyright (C) 2012 LORIA / Inria / SCORE Team
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.

 */
package crdt.simulator;

import crdt.CRDT;
import crdt.PreconditionException;
import crdt.simulator.random.RandomTrace;
import crdt.simulator.sizecalculator.StringSizeCalculator;
import java.io.IOException;
import java.util.Map.Entry;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Stephane Martin <stephane@stephanemartin.fr>
 */
public class MemTimeTest {

    @Test
    public void memTest() throws IncorrectTraceException, PreconditionException, IOException {

        CRDTMock.setTime(1,1);
        CausalSimulator cd = new CausalSimulator(new CRDTMock(), true, 1, new StringSizeCalculator());
        CRDTMock.resetCounter();
        cd.setPassiveReplica(0);
        cd.run(new RandomTrace(20, RandomTrace.FLAT, new CRDTMock.OperationMock(), 0.4, 3, 2, 1));
        assertEquals(cd.getMemUsed().toString(),"[1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20]");
        
        
    }
    @Test 
    public void timeTest()throws Exception{
        CRDTMock.setTime(20,30);
        CausalSimulator cd = new CausalSimulator(new CRDTMock(), true, 10, new StringSizeCalculator());
        CRDTMock.resetCounter();
        cd.setPassiveReplica(0);
        cd.run(new RandomTrace(20, RandomTrace.FLAT, new CRDTMock.OperationMock(), 0.4, 3, 2, 5));
       
        for(long t:cd.getGenerationTimes()){
            assertEquals(2, t/10000000);
        }
        for(double t:cd.getAvgPerRemoteMessage()){
            assertEquals(3.0, t/10000000,0.1);
        }
       
    }
            
}
