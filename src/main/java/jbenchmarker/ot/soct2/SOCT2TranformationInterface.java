/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker.ot.soct2;



/**
 *
 * @param <O> 
 * @author stephane martin
 */
public interface SOCT2TranformationInterface<O> {
    
    /**
     * transopose op1 with op2 is previous occurs
     * @param op1
     * @param op2
     * @return transposed operation
     */
    public  O transpose(O op1, O op2);
    /**
     * restaure op1 modified by op2
     * @param op1 
     * @param op2
     * @return original operation
     */
    public  O transposeBackward(O op1, O op2);
}
