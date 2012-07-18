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
public class TreeOPTTTFTranformation implements SOCT2TranformationInterface <TreeOPTTTFNodeOperation>{
 private void tTF(TreeOPTTTFNodeOperation op1, TreeOPTTTFNodeOperation op2, int operator) {
       
    /*
         * op2 must be an insertion
         */
        if (op2.getType() != TreeOPTTTFNodeOperation.OpType.ins) {
            return;
        }
  
        if (op1.getPosition() == op2.getPosition()
                && op1.getSiteId() < op2.getSiteId()
                && op1.getType() == TreeOPTTTFNodeOperation.OpType.ins) {
            return;
        }
        op1.setPosition(op1.getPosition() + operator);
 }
 @Override
    public  TreeOPTTTFNodeOperation transpose(TreeOPTTTFNodeOperation op1, TreeOPTTTFNodeOperation op2) {

        tTF(op1,op2,1);
        return op1;

    }

    @Override
    public  TreeOPTTTFNodeOperation transposeBackward(TreeOPTTTFNodeOperation op1, TreeOPTTTFNodeOperation op2) {
        tTF(op1,op2,-1);
        return op1;
    }

  
    
}
