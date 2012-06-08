/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker.ot.soct2;



/**
 *
 * @author stephane martin
 */
public interface SOCT2TranformationInterface<O> {
    
    public  O transpose(O op1, O op2);
    public  O transposeBackward(O op1, O op2);
}
