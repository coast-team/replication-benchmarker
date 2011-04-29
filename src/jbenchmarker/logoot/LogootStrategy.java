/**
 *   This file is part of ReplicationBenchmark.
 *
 *   ReplicationBenchmark is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   ReplicationBenchmark is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with ReplicationBenchmark.  If not, see <http://www.gnu.org/licenses/>.
 *
 **/


package jbenchmarker.logoot;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author urso
 */
public abstract class LogootStrategy {




    /**
     * Generate N identifier between P and Q;
     */
    abstract ArrayList<LogootIdentifier> generateLineIdentifiers(LogootMerge replica, LogootIdentifier P, LogootIdentifier Q, int N);



    static LogootIdentifier plus(int index, BigInteger bigId, BigInteger base, LogootIdentifier P, LogootIdentifier Q, int peer, int clock) {
        LogootIdentifier R = new LogootIdentifier(index + 1);
        List<Long> digits = digits(bigId, index, base);
        int i = 0;

        while (i < index && i < P.length() && digits.get(index - i) == P.getDigitAt(i)) {
            R.addComponent(P.getComponentAt(i).clone());
            i++;
        }
        while (i < index && i < Q.length() && digits.get(index - i) >= Q.getDigitAt(i)) {
            R.addComponent(Q.getComponentAt(i).clone());
            i++;
        }
        while (i <= index) {
            R.addComponent(new Component(digits.get(index - i), peer, clock));
            i++;
        }
        return R;
    }

    /**
     * An identifier as a biginteger.
     */
    static BigInteger big(LogootIdentifier id, int index, BigInteger base) {
        BigInteger bi = BigInteger.valueOf(id.getDigitAt(0));
        for (int i = 1; i <= index; i++) {
            bi = bi.multiply(base).add(BigInteger.valueOf(id.getDigitAt(i)));
        }
        return bi;
    }

    /**
     * Digits of BigInteger in reverse order
     */
    static List<Long> digits(BigInteger id, int index, BigInteger base) {
        List<Long> l = new ArrayList<Long>(index+1);
        while(index>=0) {
            BigInteger[] dr = id.divideAndRemainder(base);
            l.add(dr[1].longValue());
            id = dr[0];
            --index;
        }
        return l;
    }
}
