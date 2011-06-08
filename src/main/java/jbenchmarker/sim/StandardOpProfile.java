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
package jbenchmarker.sim;

import jbenchmarker.core.VectorClock;
import jbenchmarker.trace.TraceOperation;

/**
 * A profile that generates operation.
 * @author urso
 */
public class StandardOpProfile implements OperationProfile {

    private final double perIns, perBlock, sdvBlockSize;
    private final int avgBlockSize;
    private final RandomGauss r;

    /**
     * Constructor of profile
     * @param perIns  percentage of ins vs del operation 
     * @param perBlock percentage of block operation (size >= 1)
     * @param avgBlockSize average size of block operation
     * @param sdvBlockSize standard deviation of block operations' size.
     */
    public StandardOpProfile(double perIns, double perBlock, int avgBlockSize, double sdvBlockSize) {
        this.perIns = perIns;
        this.perBlock = perBlock;
        this.avgBlockSize = avgBlockSize;
        this.sdvBlockSize = sdvBlockSize;
        this.r = new RandomGauss();
    }

    @Override
    public TraceOperation.OpType nextType() {
        return (r.nextDouble() < perIns) ? TraceOperation.OpType.ins : TraceOperation.OpType.del;            
    }
    
    
    @Override
    public int nextPosition(int length) {
       return (int) (length*r.nextDouble());
    }
    
    @Override
    public String nextContent() {
        int length = (r.nextDouble() < perBlock) ? 
               (int) r.nextGaussian(avgBlockSize, sdvBlockSize) : 1;
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < length; i++) {
            s.append((char) ('a' + r.nextInt(26)));
        }
        return s.toString();
    }


    @Override
    public int nextOffset(int position, int l) {
        int length = (r.nextDouble() < perBlock) ? 
               (int) r.nextGaussian(avgBlockSize, sdvBlockSize) : 1;
        return Math.min(l-position, length);
    }
}
