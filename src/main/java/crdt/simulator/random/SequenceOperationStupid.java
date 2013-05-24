/*
 *  Replication Benchmarker
 *  https://github.com/score-team/replication-benchmarker/
 *  Copyright (C) 2012 LORIA / Inria / SCORE Team
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.

 */
package crdt.simulator.random;

import crdt.CRDT;
import java.util.List;
import jbenchmarker.core.Document;
import jbenchmarker.core.LocalOperation;
import jbenchmarker.core.MergeAlgorithm;
import jbenchmarker.core.SequenceOperation;

/**
 *
 * @author Stephane Martin <stephane@stephanemartin.fr>
 */
public class SequenceOperationStupid extends StandardSeqOpProfile {

    public SequenceOperationStupid(double perIns, double perBlock, int avgBlockSize, double sdvBlockSize) {
        super(perIns, perBlock, avgBlockSize, sdvBlockSize);
    }

    
   @Override
    public LocalOperation nextOperation(CRDT crdt) {
        Document replica = ((MergeAlgorithm) crdt).getDoc();

        int l = replica.viewLength();
        SequenceOperation.OpType type = (l == 0) ? SequenceOperation.OpType.insert : nextType();
        int position = type== SequenceOperation.OpType.insert? replica.viewLength(): nextPosition(l);
        int offset = (type == SequenceOperation.OpType.insert) ? 0 : nextOffset(position, l);
        List content = (type == SequenceOperation.OpType.delete) ? null : nextContent(); 
        
        return new SequenceOperation(type,  position, offset, content);
    }
    
}
