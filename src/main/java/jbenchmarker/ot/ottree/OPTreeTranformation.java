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
public class OPTreeTranformation implements SOCT2TranformationInterface <OPTTreeNodeOperation>{

 @Override
    public  OPTTreeNodeOperation transpose(OPTTreeNodeOperation op1, OPTTreeNodeOperation op2) {

        if (op1.getType() == OPTTreeNodeOperation.OpType.ins && op2.getType() == OPTTreeNodeOperation.OpType.ins) {
            if (op1.getPosition() < op2.getPosition()) {
                return op1;
            } else if (op1.getPosition() == op2.getPosition()
                    && op1.getSiteId() < op2.getSiteId()) {
                return op1;
            } else {
                op1.setPosition(op1.getPosition() + 1);
                return op1;
            }
        } else if (op1.getType() == OPTTreeNodeOperation.OpType.del && op2.getType() == OPTTreeNodeOperation.OpType.ins) {
            if (op1.getPosition() < op2.getPosition()) {
                return op1;
            } else {
                op1.setPosition(op1.getPosition() + 1);
                return op1;
            }
        } else if (op1.getType() == OPTTreeNodeOperation.OpType.transpose && op2.getType() == OPTTreeNodeOperation.OpType.ins) {
            if (op1.getPosition() < op2.getPosition()) {
                return op1;
            } else {
                op1.setPosition(op1.getPosition() + 1);
                return op1;
            }
        }else if (op1.getType() == OPTTreeNodeOperation.OpType.chT && op2.getType() == OPTTreeNodeOperation.OpType.ins) {
            if (op1.getPosition() < op2.getPosition()) {
                return op1;
            } else {
                op1.setPosition(op1.getPosition() + 1);
                return op1;
            }
        }
        return op1;

    }

    @Override
    public  OPTTreeNodeOperation transposeBackward(OPTTreeNodeOperation op1, OPTTreeNodeOperation op2) {
        if (op1.getType() == OPTTreeNodeOperation.OpType.ins && op2.getType() == OPTTreeNodeOperation.OpType.ins) {
            if (op1.getPosition() < op2.getPosition()) {
                return op1;
            } else if (op1.getPosition() == op2.getPosition()
                    && op1.getSiteId() < op2.getSiteId()) {
                return op1;
            } else {
                op1.setPosition(op1.getPosition() - 1);
                return op1;
            }
        } else if (op1.getType() == OPTTreeNodeOperation.OpType.del && op2.getType() == OPTTreeNodeOperation.OpType.ins) {
            if (op1.getPosition() < op2.getPosition()) {
                return op1;
            } else {
                op1.setPosition(op1.getPosition() - 1);
                return op1;
            }
        } else if (op1.getType() == OPTTreeNodeOperation.OpType.transpose && op2.getType() == OPTTreeNodeOperation.OpType.ins) {
            if (op1.getPosition() < op2.getPosition()) {
                return op1;
            } else {
                op1.setPosition(op1.getPosition() - 1);
                return op1;
            }
        } else if (op1.getType() == OPTTreeNodeOperation.OpType.chT && op2.getType() == OPTTreeNodeOperation.OpType.ins) {
            if (op1.getPosition() < op2.getPosition()) {
                return op1;
            } else {
                op1.setPosition(op1.getPosition() - 1);
                return op1;
            }
        }
        
        return op1;
    }

  
    
}
