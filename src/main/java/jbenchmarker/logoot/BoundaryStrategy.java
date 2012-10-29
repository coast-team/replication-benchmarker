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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author urso
 */
public class BoundaryStrategy extends LogootStrategy {


    private final long bound;
    private final BigInteger boundBI;

    public BoundaryStrategy(long bound) {
        this.bound = bound;
        this.boundBI = BigInteger.valueOf(bound);
    }    
        
    /**
     * Generate N identifier between P and Q;
     */
    @Override
    ArrayList<ListIdentifier> generateLineIdentifiers(LogootDocument doc, ListIdentifier lP, ListIdentifier lQ, int n) {
        LogootIdentifier P = (LogootIdentifier) lP, Q = (LogootIdentifier) lQ;
        int index = 0, tMin = Math.min(P.length(), Q.length());
        
        while ((index < tMin && P.getComponentAt(index).equals(Q.getComponentAt(index))   
                || (P.length() <= index && Q.length() > index && Q.getDigitAt(index) == 0))) {
            index++;
        }         
        
        long interval, d = Q.getDigitAt(index) - P.getDigitAt(index) - 1;
        if (d >= n) {
            interval = Math.min(d/n, bound); 
        } else {
            BigInteger diff = d == -1 ? BigInteger.ZERO : BigInteger.valueOf(d),
                    N = BigInteger.valueOf(n);
            while (diff.compareTo(N) < 0) {
                index++;
                diff = diff.multiply(doc.getBase()).
                        add(BigInteger.valueOf(doc.getMax() - P.getDigitAt(index)).
                        add(BigInteger.valueOf(Q.getDigitAt(index))));
            }           
            interval = diff.divide(N).min(boundBI).longValue();
        }
          
        ArrayList<ListIdentifier> patch = new ArrayList<ListIdentifier>();
        List<Long> digits = P.digits(index);
        for (int i = 0; i < n; i++) {
            LogootStrategy.plus(digits, LogootStrategy.nextLong(interval) + 1, doc.getBase(), doc.getMax());
            patch.add(LogootStrategy.constructIdentifier(digits, P, Q, doc.getReplicaNumber(), doc.getClock()));
            doc.incClock();
        }
        return patch;    
    }
}
