/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker.ot;

/**
 *
 * @author stephane martin
 */
public interface SOCT2TranformationInterface {
    
    public  SOCT2OperationInterface transpose(SOCT2OperationInterface op1, SOCT2OperationInterface op2);
    public  SOCT2OperationInterface transposeBackward(SOCT2OperationInterface op1, SOCT2OperationInterface op2);
}
