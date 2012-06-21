/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker.ot.soct2;

import jbenchmarker.core.Operation;


/**
 *
 * @author urso
 */
public class SOCT2LogOptimizedPlaceAndLast<Op extends Operation> extends SOCT2LogOptimizedLast<Op> {

    
    public SOCT2LogOptimizedPlaceAndLast(SOCT2TranformationInterface t) {
        super(t);
    }

    @Override
    protected void placeOperation(OTMessage<Op> message, int separationIndex) {
        Op opt = message.getOperation();
        operations.add(separationIndex, message);
        for (int i = separationIndex + 1; i < this.operations.size(); ++i) {
            transforme.transpose(operations.get(i).getOperation(), opt);
        }
    }

    @Override
    public SOCT2Log<Op> create() {
        return new SOCT2LogOptimizedPlaceAndLast<Op>(transforme);
    }
}
