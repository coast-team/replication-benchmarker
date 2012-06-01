/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker.ot.otset;

import jbenchmarker.ot.SOCT2OperationInterface;
import jbenchmarker.ot.SOCT2TranformationInterface;

/**
 *
 * @author stephane martin
 */
public class DelWinTransformation implements SOCT2TranformationInterface {

    @Override
    public SOCT2OperationInterface transpose(SOCT2OperationInterface op1, SOCT2OperationInterface op2) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public SOCT2OperationInterface transposeBackward(SOCT2OperationInterface op1, SOCT2OperationInterface op2) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
