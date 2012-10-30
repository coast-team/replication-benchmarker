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

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author urso
 */
public class BoundaryListStrategy extends LogootStrategy {


    private final long bound;

    public BoundaryListStrategy(long bound) {
        this.bound = bound;
    }    
        
    /**
     * Generate N identifier between P and Q;
     */
    @Override
    List<ListIdentifier> generateLineIdentifiers(LogootDocument doc, ListIdentifier P, ListIdentifier Q, int n) {
        assert P.compareTo(Q) < 0;
        
        LogootListPosition PP = (LogootListPosition) P;
        LogootListPosition QP = (LogootListPosition) Q;
        int index = 0;

        while (PP.getSafe(index) == QP.getSafe(index)) {
            ++index;
        }        
        long interval, d = QP.getSafe(index)- PP.getSafe(index) - 1;
        if (d >= n) {
            interval = Math.min(d/n, bound); 
        } else {
            while (d < n) {
                index++;
                d = (d << 8) + QP.getSafe(index) - PP.getSafe(index) + 255;
            }           
            interval = Math.min(d/n, bound);
        }
          
        ArrayList<ListIdentifier> patch = new ArrayList<ListIdentifier>();
        LogootListPosition NP = PP;
        for (int i = 0; i < n; i++) {
            NP = plus(index, NP, LogootStrategy.nextLong(interval) + 1, doc.getReplicaNumber(), doc.getClock());
            patch.add(NP);
            doc.incClock();
        }
        return patch;
    }
    
    private LogootListPosition plus(int index, LogootListPosition PP, long l, int replicaNumber, int clock) {
        LogootListPosition NP = new LogootListPosition(index + 1, replicaNumber, clock); 
        while (l > 0) {
            long val = l + PP.getSafe(index);
            NP.set(index, (byte) (val & 0xff));
            l = (val + 128) >> 8;
            --index;
        }
        while (index >= 0) {
            NP.set(index, PP.getSafe(index));
            --index;
        }
        return NP;
    }

    @Override
    public ListIdentifier begin() {
        return new LogootListPosition(Byte.MIN_VALUE);
    }

    @Override
    public ListIdentifier end() {
        return new LogootListPosition(Byte.MAX_VALUE);
    }
}
