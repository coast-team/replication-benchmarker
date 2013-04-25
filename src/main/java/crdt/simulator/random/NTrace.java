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


package crdt.simulator.random;

import crdt.simulator.Trace;
import crdt.simulator.TraceOperation;
import java.util.Enumeration;

/**
 *  Special Trace with is concatenation with many traces given in constructor.
 * 
 * @author Stephane Martin <stephane@stephanemartin.fr>
 */
public class NTrace implements Trace {

    Trace[] listRandomTrace;
    
    /**
     * @param traces many traces
     */
    public NTrace(Trace... traces) {
        this.listRandomTrace = traces;
    }

    @Override
    public Enumeration<TraceOperation> enumeration() {

        return new Enumeration<TraceOperation>() {
            Enumeration<TraceOperation> currentTrace=listRandomTrace.length>0?listRandomTrace[0].enumeration():null;
            int currentPlace = 0;
            
            /**
             * Place check if no more operation existing
             * If it is existing it set currentTrace to non empty trace.
             */
            private void setCurrent() {
                while (currentTrace!=null && !currentTrace.hasMoreElements() && currentPlace < listRandomTrace.length-1) {
                    currentTrace = listRandomTrace[++currentPlace].enumeration();
                }
            }

            @Override
            public boolean hasMoreElements() {
                setCurrent();
                return currentTrace!=null?currentTrace.hasMoreElements():false;
            }

            @Override
            public TraceOperation nextElement() {
                setCurrent();
                return currentTrace.nextElement();
            }
        };
    }
}
