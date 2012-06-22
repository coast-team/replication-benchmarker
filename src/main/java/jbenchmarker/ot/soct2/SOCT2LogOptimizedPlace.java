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
public class SOCT2LogOptimizedPlace<Op extends Operation> extends SOCT2Log<Op> {

    
    public SOCT2LogOptimizedPlace(SOCT2TranformationInterface t) {
        super(t);
    }

    @Override
    protected Op placeOperation(OTMessage<Op> message, int separationIndex) {
        Op opt = message.getOperation(), oo = (Op) opt.clone();
        operations.add(separationIndex, message);
        for (int i = separationIndex + 1; i < this.operations.size(); ++i) {
            Op opi = (Op) operations.get(i).getOperation().clone();
            transforme.transpose(operations.get(i).getOperation(), oo);
            oo = transforme.transpose(oo, opi);
        }
        return oo;
    }

    @Override
    public SOCT2Log<Op> create() {
        return new SOCT2LogOptimizedPlace<Op>(transforme);
    }
}
