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

import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author urso
 */
class BoundaryStrategy implements LogootStrategy {

    private Random ran = new Random();
    private int bound;

    public BoundaryStrategy(int bound) {
        this.bound = bound;
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
        long space = 0, interval = 0;
//        BigInteger interval = BigInteger.ZERO, N = BigInteger.valueOf(n);

        while (index<tMin && P.getComponentAt(index).equals(Q.getComponentAt(index))) {
            index++;
        }
        long diff = Q.getDigitAt(index) - P.getDigitAt(index) - 1;
        if (diff < n) {
            if (diff<0) space = 0;
            else space = diff;
            index++;
            while (interval == 0) {
                if (space > 0 && replica.getNbBit() == 64) { // Enough place => boundary 
                    interval = bound;
                } else {
                    if (replica.getNbBit() == 64) {
                        space = replica.getMax() - P.getDigitAt(index);
                        if ((space / bound > n) || (Q.getDigitAt(index) / bound > n)) {
                            interval = bound;
                        } else {
                            space += Q.getDigitAt(index);
                        }
                    } else {
                        space = space * replica.getBase() + replica.getMax() - P.getDigitAt(index) + Q.getDigitAt(index);
                    }
                    if (space > n) {
                        interval = space / n;
                    } else {
                        index++;
                    }
                }
            }
        } else {
            interval = diff / n;
        }
           
        if (interval > bound) interval = bound;
        
        ArrayList<LogootIdentifier> patch = new ArrayList<LogootIdentifier>();        
        LogootIdentifier id = P;        
        for (int i = 0; i < n; i++) 
        {
            id = id.plus(index, nextLong(interval)+1, Q, replica.getMax(), replica.getReplicaNb(), replica.getClock()); 
            replica.incClock();
            patch.add(id);
        }
        return patch;
    }
}
