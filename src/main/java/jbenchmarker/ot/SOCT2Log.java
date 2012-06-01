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
package jbenchmarker.ot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author oster
 */
public class SOCT2Log implements Iterable<SOCT2OperationInterface>, Serializable {

    private SOCT2TranformationInterface tranforme;
    
    public SOCT2Log(SOCT2TranformationInterface t){
        this.tranforme=t;
    }
    private SOCT2Log(){
        
    }
    
    protected List<SOCT2OperationInterface> operations = new ArrayList<SOCT2OperationInterface>();

    public void add(SOCT2OperationInterface operation) {
        this.operations.add(operation);
    }

    public int getSize() {
        return this.operations.size();
    }

    public Iterator<SOCT2OperationInterface> iterator() {
        return this.operations.iterator();
    }

    public SOCT2OperationInterface merge(TTFOperation operation) {
        SOCT2OperationInterface opt = operation;
        int separationIndex = separatePrecedingAndConcurrentOperations(operation);

        for (int i = separationIndex; i < this.operations.size(); i++) {
            opt = tranforme.transpose(opt, operations.get(i));
        }

        return opt;
    }

    private int separatePrecedingAndConcurrentOperations(TTFOperation receivedOperation) {
        int separationIndex = 0;
        int logSize = operations.size();

        for (int i = 0; i < logSize; i++) {
            SOCT2OperationInterface localOperation = operations.get(i);
            int siteIdOfLocalOperation = localOperation.getSiteId();

            if (localOperation.getClock().getSafe(siteIdOfLocalOperation) < receivedOperation.getClock().getSafe(siteIdOfLocalOperation)) {
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
        SOCT2OperationInterface opj = operations.get(index);
        SOCT2OperationInterface opk = operations.get(index - 1);

        SOCT2OperationInterface opi = tranforme.transposeBackward(opj, opk);
        opk = tranforme.transpose(opk, opi);

        operations.set(index - 1, opi);
        operations.set(index, opk);
    }
}
