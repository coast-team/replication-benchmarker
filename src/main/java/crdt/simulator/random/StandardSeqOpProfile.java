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
package crdt.simulator.random;

import java.util.LinkedList;
import java.util.List;
import jbenchmarker.core.SequenceOperation;

/**
 * A profile that generates operation.
 * @author urso
 */
public class StandardSeqOpProfile extends SequenceOperationProfile<Character> {
 
    private final double perIns, perBlock, sdvBlockSize;
    private final int avgBlockSize;

    static final public StandardSeqOpProfile BASIC = new StandardSeqOpProfile(0.8, 0.1, 50, 5.0);
    static final public StandardSeqOpProfile WITHOUT_BLOCK = new StandardSeqOpProfile(0.8, 0, 0, 0);
    static final public StandardSeqOpProfile ONLY_BLOCK = new StandardSeqOpProfile(0.8, 1, 50, 5.0);
    static final public StandardSeqOpProfile ALL_INS  = new StandardSeqOpProfile(1, 0.1, 50, 5.0);
    
    /**
     * Constructor of profile
     * @param perIns  percentage of ins vs del operation 
     * @param perBlock percentage of block operation (size >= 1)
     * @param avgBlockSize average size of block operation
     * @param sdvBlockSize standard deviation of block operations' size.
     */
    public StandardSeqOpProfile(double perIns, double perBlock, int avgBlockSize, double sdvBlockSize) {
        this.perIns = perIns;
        this.perBlock = perBlock;
        this.avgBlockSize = avgBlockSize;
        this.sdvBlockSize = sdvBlockSize;
    }

    @Override
    public SequenceOperation.OpType nextType() {
        return (r.nextDouble() < perIns) ? SequenceOperation.OpType.insert : SequenceOperation.OpType.delete;            
    }
    
    @Override
    public List<Character> nextContent() {
        int length = (r.nextDouble() < perBlock) ? 
                     (int) r.nextLongGaussian(avgBlockSize, sdvBlockSize) : 1;
        List<Character> s = new LinkedList<Character>();
        for (int i = 0; i < length; i++) {
            s.add((char) ('a' + r.nextInt(26)));
        }
        return s;
    }

    @Override
    public int nextOffset(int position, int l) {
        int length = (r.nextDouble() < perBlock) ? 
               (int) r.nextLongGaussian(avgBlockSize-1, sdvBlockSize) : 1;
        return Math.min(l-position, length);
    }
}
