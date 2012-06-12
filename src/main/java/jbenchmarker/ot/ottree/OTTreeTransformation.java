/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker.ot.ottree;

import jbenchmarker.ot.soct2.SOCT2TranformationInterface;

/**
 *
 * @author Stephane Martin <stephane.martin@loria.fr>
 */
public class OTTreeTransformation implements SOCT2TranformationInterface<OTTreeOperation> {

    @Override
    public OTTreeOperation transpose(OTTreeOperation op1, OTTreeOperation op2) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public OTTreeOperation transposeBackward(OTTreeOperation op1, OTTreeOperation op2) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
