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
package jbenchmarker.logoot;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author urso
 */
class BoundaryStrategy extends LogootStrategy {

    private Random ran = new Random();
    private final long bound;
    private final BigInteger boundBI;

    public BoundaryStrategy(int bound) {
        this.bound = bound;
        this.boundBI = BigInteger.valueOf(bound);
    }    
        
    public long nextLong(long l) {
        long x = ran.nextLong() % l;
        if (x<0) x += l;
        return x;
    }
    
    /**
     * Generate N identifier between P and Q;
     */
    public ArrayList<LogootIdentifier> generateLineIdentifiers(LogootMerge replica, LogootIdentifier P, LogootIdentifier Q, int n) {
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
                diff = diff.multiply(replica.getBase()).
                        add(BigInteger.valueOf(replica.getMax() - P.getDigitAt(index)).
                        add(BigInteger.valueOf(Q.getDigitAt(index))));
            }           
            interval = diff.divide(N).min(boundBI).longValue();
        }
        
        ArrayList<LogootIdentifier> patch = new ArrayList<LogootIdentifier>();        
        List<Long> digits = P.digits(index);
        for (int i = 0; i < n; i++) {
            plus(digits, nextLong(interval) + 1, replica.getBase(), replica.getMax());
            P = constructIdentifier(digits, P, Q, replica.getReplicaNumber(), replica.getClock());
            replica.incClock();
            patch.add(P);
        }  
        return patch;
    }
}
