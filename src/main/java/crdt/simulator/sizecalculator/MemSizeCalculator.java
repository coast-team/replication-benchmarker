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
package crdt.simulator.sizecalculator;

import com.carrotsearch.sizeof.RamUsageEstimator;
import crdt.CRDT;
import java.io.IOException;

/**
 *
 * @author Stephane Martin <stephane@stephanemartin.fr>
 */
public class MemSizeCalculator implements SizeCalculator {

    private boolean overHead = true;

    public MemSizeCalculator(boolean overHead) {
        this.overHead=overHead;
    }

    public MemSizeCalculator() {
    }

    
    public static long sizeOf(Object o){ //For test
        return RamUsageEstimator.sizeOf(o);
    }

    /**
     *
     * @param m the value of m
     * @return the long
     * @throws IOException
     */
    @Override
    public long serializ(CRDT m) throws IOException {
        if (overHead) {
            return sizeOf(m);
        } else {
            return sizeOf(m.lookup());
        }
        //SizeOf.deepSizeOf(m);
        // throw new UnsupportedOperationException("Not supported yet.");
    }
}
