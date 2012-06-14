/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker.ot.ottree;

import jbenchmarker.core.Operation;

/**
 *
 * @author Stephane Martin <stephane.martin@loria.fr>
 */
public class OTTreeOperation implements Operation{

    static enum OpType{Add,Del,ChLabel,ChOrder};
    
    OpType opType;
    
    
    @Override
    public Operation clone() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
