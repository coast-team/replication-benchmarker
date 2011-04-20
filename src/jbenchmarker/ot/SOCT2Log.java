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

package jbenchmarker.ot;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author oster
 */
public class SOCT2Log implements Iterable<TTFOperation> {

    protected List<TTFOperation> operations = new ArrayList<TTFOperation>();

    public void add(TTFOperation operation) {
        this.operations.add(operation);
    }

    public int getSize() {
        return this.operations.size();
    }

    @Override
    public Iterator<TTFOperation> iterator() {
        return this.operations.iterator();
    }

    public TTFOperation merge(TTFOperation operation) {
        TTFOperation opt = operation;
        int separationIndex = separatePrecedingAndConcurrentOperations(operation);

        for (int i = separationIndex; i < this.operations.size(); i++) {
            opt = TTFTransformations.transpose(opt, operations.get(i));
        }

        return opt;
    }

    private int separatePrecedingAndConcurrentOperations(TTFOperation receivedOperation) {
        int separationIndex = 0;
        int logSize = operations.size();

        for (int i = 0; i < logSize; i++) {
            TTFOperation localOperation = operations.get(i);
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
        TTFOperation opj = operations.get(index);
        TTFOperation opk = operations.get(index - 1);

        TTFOperation opi = TTFTransformations.transposeBackward(opj, opk);
        opk = TTFTransformations.transpose(opk, opi);

        operations.set(index - 1, opi);
        operations.set(index, opk);
    }
}
