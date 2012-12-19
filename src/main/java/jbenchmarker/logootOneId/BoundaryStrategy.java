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
package jbenchmarker.logootOneId;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author urso
 */
public class BoundaryStrategy extends LogootOneIdStrategy {

    private final BigInteger boundBI;
    Random r = new Random();

    public BoundaryStrategy(int bound) {
        this.boundBI = BigInteger.valueOf(bound);
    }

    /**
     * Generate N identifier between P and Q;
     */
    @Override
    ArrayList<LogootOneIdentifier> generateLineIdentifiers(LogootOneIdDocument replica, LogootOneIdentifier P, LogootOneIdentifier Q, int n) {
        int scale=1;
        //concat with replica
        
        BigDecimal degitP = P.getDigit().setScale(scale, RoundingMode.DOWN);
        BigDecimal degitQ = Q.getDigit().setScale(scale, RoundingMode.DOWN);
        
        BigDecimal sub = degitQ.subtract(degitP);
                
        BigInteger d = sub.unscaledValue().subtract(BigInteger.valueOf(1));
        BigInteger interval;
        BigInteger N = BigInteger.valueOf(n);
        if (d.compareTo(N) >= 0) {
            interval = d.divide(N).min(boundBI);
        } else {
            BigInteger diff = d.equals(BigInteger.valueOf(-1)) ? BigInteger.ZERO : d;
            while (diff.compareTo(N) < 0) {
                scale++;
                degitP = P.getDigit().setScale(scale, RoundingMode.DOWN);
                degitQ = Q.getDigit().setScale(scale, RoundingMode.DOWN);
                sub = degitQ.subtract(degitP);
                diff = sub.unscaledValue().subtract(BigInteger.valueOf(1));
            }
            
            interval = diff.divide(N).min(boundBI);
        }
        
        ArrayList<LogootOneIdentifier> patch = new ArrayList<LogootOneIdentifier>();
        BigDecimal forRandom = BigDecimal.valueOf(0);
        BigDecimal intervalDec = new BigDecimal(interval).scaleByPowerOfTen(-scale);
        degitP = P.getDigit().setScale(scale, RoundingMode.DOWN);
        for (int i = 0; i < n; i++) {
            forRandom = nextBigDec(degitP, intervalDec, scale);
            P = constructIdentifier(forRandom, replica.getReplicaNumber(), replica.getClock());
            replica.incClock();
            patch.add(P);
            degitP = forRandom;
        }
        return patch;
    }

    public BigDecimal nextBigDec(BigDecimal lastDegit, BigDecimal interval, int index) {
        BigDecimal prefix = lastDegit.setScale(index, RoundingMode.DOWN);
        
        BigInteger random = new BigInteger(8, r);
        BigDecimal ranDec = new BigDecimal(random).scaleByPowerOfTen(-index);
        BigDecimal ran = ranDec.remainder(interval);
        if (ran.compareTo(BigDecimal.valueOf(0)) <= 0) {
            ran = ran.add(interval);
        }
        return prefix.add(ran);
    }
}