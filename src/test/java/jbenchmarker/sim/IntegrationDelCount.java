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
package jbenchmarker.sim;

import jbenchmarker.logoot.LogootCounter;
import org.junit.Ignore;
import jbenchmarker.core.MergeAlgorithm;
import jbenchmarker.logoot.LogootFactory;
import java.util.Iterator;
import jbenchmarker.logoot.LogootCounter;
import jbenchmarker.trace.TraceGenerator;
import jbenchmarker.trace.SequenceOperation;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author urso
 */
public class IntegrationDelCount {
    static int totalcount = 0;
    
//    @Ignore
    @Test
    public void testLogootG1Run() throws Exception {
        Iterator<SequenceOperation> trace;
        OldCausalDispatcher cd;
        
//        trace = TraceGenerator.traceFromXML("../../traces/xml/G1.xml", 1);
//        cd = new OldCausalDispatcher(new LogootFactory());
//
//        cd.run(trace);
//        System.out.println(LogootCounter.count / cd.getReplicas().keySet().size());
//        LogootCounter.count = 0;
//        
//        trace = TraceGenerator.traceFromXML("../../traces/xml/G2.xml", 1);
//        cd = new OldCausalDispatcher(new LogootFactory());
//
//        cd.run(trace);
//        System.out.println(LogootCounter.count / cd.getReplicas().keySet().size());
//        LogootCounter.count = 0;
//        
//        trace = TraceGenerator.traceFromXML("../../traces/xml/G3.xml", 1);
//        cd = new OldCausalDispatcher(new LogootFactory());
//        LogootCounter.count = 0;
//
//        cd.run(trace);
//        System.out.println(LogootCounter.count / cd.getReplicas().keySet().size());
//        LogootCounter.count = 0;
//        
//        trace = TraceGenerator.traceFromXML("../../traces/xml/Serie.xml", 1);
//        cd = new OldCausalDispatcher(new LogootFactory());
//        LogootCounter.count = 0;
//
//        cd.run(trace);
//        System.out.println(LogootCounter.count / cd.getReplicas().keySet().size());
//        LogootCounter.count = 0;
        
        int nb = 10;
        cd = new OldCausalDispatcher(new LogootCounter.Factory());

        for (int i = 0; i < nb; i++) {
            cd.reset();
            trace = new RandomTrace(1000, RandomTrace.FLAT, new StandardOpProfile(0.8, 0.1, 40, 5.0), 0.2, 10, 3.0, 13);
            cd.run(trace);
        }
        System.out.println((LogootCounter.count / nb) / cd.getReplicas().keySet().size());
        LogootCounter.count = 0;
        
        for (int i = 0; i < nb; i++) {
            cd.reset();
            trace = new RandomTrace(1000, RandomTrace.FLAT, new StandardOpProfile(0.8, 0.1, 40, 5.0), 0.05, 40, 3.0, 13);
            cd.run(trace);
        }
        System.out.println((LogootCounter.count / nb) / cd.getReplicas().keySet().size());
        LogootCounter.count = 0;        
        
        for (int i = 0; i < nb; i++) {
            cd.reset();
            trace = new RandomTrace(4000, RandomTrace.FLAT, new StandardOpProfile(0.8, 0.1, 40, 5.0), 0.05, 10, 3.0, 13);
            cd.run(trace);
        }
        System.out.println((LogootCounter.count / nb) / cd.getReplicas().keySet().size());
        LogootCounter.count = 0;    
        
        for (int i = 0; i < nb; i++) {
            cd.reset();
            trace = new RandomTrace(1000, RandomTrace.FLAT, new StandardOpProfile(0.8, 0.1, 40, 5.0), 0.05, 10, 3.0, 13);
            cd.run(trace);
        }
        System.out.println((LogootCounter.count / nb) / cd.getReplicas().keySet().size());
        LogootCounter.count = 0;   
        
    }
}
