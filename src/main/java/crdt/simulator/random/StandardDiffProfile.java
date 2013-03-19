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
package crdt.simulator.random;

import java.util.LinkedList;
import java.util.List;
import jbenchmarker.core.SequenceOperation;

/**
 * A profile that generates operation similar to VCS diff (block of lines).
 * @author urso
 */
public class StandardDiffProfile extends SequenceOperationProfile<String> {
 
    private final double perUp, perIns, perBlock, sdvBlockSize, sdvLineSize;
    private final int avgBlockSize, avgLinesize;

    public static final StandardDiffProfile BASIC = new StandardDiffProfile(0.7, 0.7, 0.9, 5, 10.0, 30, 10.0);
    public static final StandardDiffProfile SMALL = new StandardDiffProfile(0.7, 0.7, 0.9, 5, 1.0, 10, 3.0);
    public static final StandardDiffProfile WITHOUT_BLOCK = new StandardDiffProfile(0.7, 0.7, 0, 1, 0, 30, 10.0);
            
    /**
     * Constructor of profile
     * @param perUp  percentage of update vs other operation 
     * @param perIns  percentage of ins vs del operation 
     * @param perBlock percentage of block operation (size >= 1)
     * @param avgBlockSize average size of block operation (in number of lines)
     * @param sdvBlockSize standard deviation of block operations' size
     * @param avgLinesize average line size
     * @param sdvLineSize standard deviation of line's size
     */
    public StandardDiffProfile(double perUp, double perIns, double perBlock, int avgBlockSize, double sdvBlockSize, int avgLinesize, double sdvLineSize) {
        this.perUp = perUp;
        this.perIns = perIns;
        this.perBlock = perBlock;
        this.avgBlockSize = avgBlockSize;
        this.sdvBlockSize = sdvBlockSize;
        this.avgLinesize = avgLinesize;
        this.sdvLineSize = sdvLineSize;
    }

    @Override
    public SequenceOperation.OpType nextType() {
        return (r.nextDouble() < perUp) ? SequenceOperation.OpType.replace : 
                (r.nextDouble() < perIns) ? SequenceOperation.OpType.ins : SequenceOperation.OpType.del;            
    }
    
    @Override
    public List<String> nextContent() {
        int length = (r.nextDouble() < perBlock) ? 
                     (int) r.nextLongGaussian(avgBlockSize, sdvBlockSize) : 1;
        List<String> b = new LinkedList<String>();
        for (int i = 0; i < length; i++) {
            StringBuilder s = new StringBuilder();
            int lineSize = (int) r.nextLongGaussian(avgLinesize, sdvLineSize);            
            for (int j = 0; j < lineSize; j++) {
                s.append((char) ('a' + r.nextInt(26)));
            }
            s.append('\n');
            b.add(s.toString());
        }
        return b;
    }

    @Override
    public int nextOffset(int position, int l) {
        int length = (r.nextDouble() < perBlock) ? 
               (int) r.nextLongGaussian(avgBlockSize-1, sdvBlockSize) : 1;
        return Math.min(l-position, length);
    }
}
