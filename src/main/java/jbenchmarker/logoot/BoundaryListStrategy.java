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
package jbenchmarker.logoot;

import java.util.ArrayList;
import java.util.List;

/**
 * Boundary random logoot strategy that uses list identifiers. 
 * @author urso
 */
public class BoundaryListStrategy extends RandomLogootStrategy {


    private final long bound;
    private final int base;
    private final long plus;
    private final long mask;

    /**
     * Boundary strategy for logoot list. base can be 8, 16, 32. Boundary = 2^base/2;
     */
    public BoundaryListStrategy(int base) {
        if (base != 8 && base != 16 && base != 32) {
            throw new IllegalArgumentException("Illegal base.");
        }
        this.base = base;
        this.mask = (long) Math.pow(2, base) - 1;
        this.plus = (long) Math.pow(2, base - 1);
        this.bound = (long) Math.pow(2, base / 2);
    }    
        
    /**
     * Generate N identifier between P and Q;
     */
    @Override
    public List<ListIdentifier> generateLineIdentifiers(LogootDocument doc, ListIdentifier P, ListIdentifier Q, int n) {
//        assert P.compareTo(Q) < 0;
        
        LogootListPosition PP = (LogootListPosition) P;
        LogootListPosition QP = (LogootListPosition) Q;
        int index = 0;

        while (PP.getSafe(index) == QP.getSafe(index)) {
            ++index;
        }        
        long interval, d = (long) QP.getSafe(index) - (long) PP.getSafe(index) - 1;
        if (d >= n) {
            interval = Math.min(d/n, bound); 
        } else {
            while (d < n) {
                ++index;
                d = (d << base) + (long) QP.getSafe(index) - (long) PP.getSafe(index) + mask;
            }           
            interval = Math.min(d/n, bound);
        }
          
        ArrayList<ListIdentifier> patch = new ArrayList<ListIdentifier>();
        LogootListPosition NP = PP;
        for (int i = 0; i < n; i++) {
            NP = plus(index, NP, RandomLogootStrategy.nextLong(interval) + 1, doc.getReplicaNumber(), doc.nextClock());
            patch.add(NP);
        }
        return patch;
    }
    
    private LogootListPosition plus(int index, LogootListPosition PP, long l, int replicaNumber, int clock) {
        LogootListPosition NP = new LogootListPosition(base, index + 1, replicaNumber, clock); 
        while (l > 0) {
            long val = l + PP.getSafe(index);
            NP.set(index, (int) (val & mask));
            l = (val + plus) >> base;
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
        return new LogootListPosition(base, (int) (-plus));
    }

    @Override
    public ListIdentifier end() {
        return new LogootListPosition(base, (int) (plus - 1));
    }
}
