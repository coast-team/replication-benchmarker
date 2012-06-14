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
public class SOCT2Log<Op extends Operation> implements Iterable<SOCT2Message<Op>>, Serializable {

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
    protected List<SOCT2Message<Op>> operations = new ArrayList<SOCT2Message<Op>>();

    /**
     * add operation to log without transformation
     * @param operation
     */
    public void add(SOCT2Message operation) {
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
    public Iterator<SOCT2Message<Op>> iterator() {
        return this.operations.iterator();
    }

    /**
     * Integrate the new operation. 
     * The history is splited and operation is transformed
     * @param operation new operation
     * @return return the transformed operation
     */
    public Op merge(SOCT2Message<Op> operation) {
        Op opt = operation.getOperation();
        int separationIndex = separatePrecedingAndConcurrentOperations(operation);
        for (int i = separationIndex; i < this.operations.size(); i++) {
            opt = transforme.transpose(opt, operations.get(i).getOperation());
        }

        return opt;
    }

    private int separatePrecedingAndConcurrentOperations(SOCT2Message receivedOperation) {
        int separationIndex = 0;
        int logSize = operations.size();

        for (int i = 0; i < logSize; i++) {
            SOCT2Message localOperation = operations.get(i);
            int siteIdOfLocalOperation = localOperation.getSiteId();

            //if (localOperation.getClock().getSafe(siteIdOfLocalOperation) < receivedOperation.getClock().getSafe(siteIdOfLocalOperation)) { Garbage collection
            if (localOperation.getClock().getSafe(siteIdOfLocalOperation) <= receivedOperation.getClock().getSafe(siteIdOfLocalOperation)) {    
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
        SOCT2Message<Op> messj = operations.get(index);
        SOCT2Message<Op> messk = operations.get(index - 1);
        
        Op opj = messj.getOperation();
        Op opk = messk.getOperation();
        

        messj.setOperation( transforme.transposeBackward(opj, opk));
        messk.setOperation( transforme.transpose(opk, opj));

        operations.set(index - 1, messj);
        operations.set(index, messk);
    }

 
}
