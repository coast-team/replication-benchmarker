/*
 *  Replication Benchmarker
 *  https://github.com/score-team/replication-benchmarker/
 *  Copyright (C) 2013 LORIA / Inria / SCORE Team
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

import Tools.ProgressBar;
import crdt.simulator.Trace;
import crdt.simulator.TraceOperation;
import java.util.Enumeration;

/**
 *
 * @author Stephane Martin <stephane@stephanemartin.fr>
 */
public class ProgressTrace implements Trace  {
    Trace trace;
    long lenght;

    public ProgressTrace(Trace trace, long lenght) {
        this.trace = trace;
        this.lenght = lenght;
    }
    
    
    @Override
    public Enumeration<TraceOperation> enumeration() {
        return new Enumeration<TraceOperation>(){
            ProgressBar progress=new ProgressBar(lenght);
             Enumeration<TraceOperation> enu=trace.enumeration();
            @Override
            public boolean hasMoreElements() {
                return enu.hasMoreElements();
            }

            @Override
            public TraceOperation nextElement() {
                TraceOperation ret=enu.nextElement();
                progress.progress(1);
                return ret;
            }  
        };
    }
}
