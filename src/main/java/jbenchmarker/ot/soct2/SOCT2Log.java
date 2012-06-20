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
package jbenchmarker.ot.soct2;

import collect.RangeList;
import collect.VectorClock;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import jbenchmarker.core.Operation;

/**
 *  This object is a log of Soct2. It splits the history the process to transform new operation
 * @param <Op> 
 * @author oster
 */
public class SOCT2Log<Op extends Operation> implements Iterable<OTMessage<Op>>, Serializable {

    private SOCT2TranformationInterface<Op> transforme;
    
    /**
     * Construct a new log with this transformations.
     * @param t Transformation
     */
    public SOCT2Log(SOCT2TranformationInterface t){
        this.transforme=t;
    }
    
    /*
     * Construction without transformation is forbiden.
     */
    
    private SOCT2Log(){
        
    }
    
    /**
     * This is log or history of process.
     */
    protected final RangeList<OTMessage<Op>> operations = new RangeList<OTMessage<Op>>();

    /**
     * add operation to log without transformation
     * @param operation
     */
    public void add(OTMessage operation) {
        this.operations.add(operation);
    }

    /**
     * 
     * @return size of log or history
     */
    public int getSize() {
        return this.operations.size();
    }

    /**
     * 
     * @return iterator for each operation with vector clock
     */
    @Override
    public Iterator<OTMessage<Op>> iterator() {
        return this.operations.iterator();
    }

    /**
     * Integrate the new operation. 
     * The history is splited and operation is transformed
     * @param operation new operation
     * @return return the transformed operation
     */
    public Op merge(OTMessage<Op> operation) {
        Op opt = operation.getOperation();
        int separationIndex = separatePrecedingAndConcurrentOperations(operation.getClock());
        for (int i = separationIndex; i < this.operations.size(); i++) {
            opt = transforme.transpose(opt, operations.get(i).getOperation());
        }

        return opt;
    }

    int separatePrecedingAndConcurrentOperations(VectorClock clock) {
        int separationIndex = 0;
        int logSize = operations.size();

        for (int i = 0; i < logSize; i++) {
            OTMessage localOperation = operations.get(i);
            int siteIdOfLocalOperation = localOperation.getSiteId();

            //if (localOperation.getClock().getSafe(siteIdOfLocalOperation) < receivedOperation.getClock().getSafe(siteIdOfLocalOperation)) { Garbage collection
            if (localOperation.getClock().getSafe(siteIdOfLocalOperation) <= clock.getSafe(siteIdOfLocalOperation)) {    
            // opi precedes op
                for (int j = i; j > separationIndex; j--) {
                    // transpose opi backward to seq1
                    transposeBackward(j);
                }
                separationIndex++;
            }
        }

        return separationIndex;
    }

    // transpose backward the (index)th operation with the (index-1)th operation
    private void transposeBackward(int index) {
        OTMessage<Op> messj = operations.get(index);
        OTMessage<Op> messk = operations.get(index - 1);
        
        Op opj = messj.getOperation();
        Op opk = messk.getOperation();
        

        messj.setOperation( transforme.transposeBackward(opj, opk));
        messk.setOperation( transforme.transpose(opk, opj));

        operations.set(index - 1, messj);
        operations.set(index, messk);
    }

    void purge(int purgePoint) {
        operations.removeRangeOffset(0, purgePoint);
    }
}
