/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker.ot.soct2;

import java.util.List;
import jbenchmarker.core.Operation;


/**
 *
 * @author urso
 */
public class SOCT2LogOptimizedLast<Op extends Operation> extends SOCT2Log<Op> {

    int lastSeparationIndex = 0;
    
    public SOCT2LogOptimizedLast(SOCT2TranformationInterface t) {
        super(t);
    }

    @Override
    public void merge(OTMessage<Op> operation) {
        int startSeparation = 0;
        if (operations.size() > 0) {
            OTMessage<Op> last = operations.get(operations.size()-1);
            if (last.getClock().get(last.getSiteId()) <= operation.getClock().getSafe(last.getSiteId())) {
                startSeparation = lastSeparationIndex;
            }
        }
        lastSeparationIndex = separatePrecedingAndConcurrentOperations(operation.getClock(), startSeparation);
        placeOperation(operation, lastSeparationIndex);            
    }

    @Override
    void purge(int purgePoint) {
        super.purge(purgePoint);
        lastSeparationIndex = Math.max(0, lastSeparationIndex - purgePoint);
    }

    @Override
    void insertAll(List<OTMessage<Op>> purged) {
        super.insertAll(purged);
        lastSeparationIndex += purged.size();
    }

    @Override
    public SOCT2Log<Op> create() {
        return new SOCT2LogOptimizedLast<Op>(transforme);
    }
}
