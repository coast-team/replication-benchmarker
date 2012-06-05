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
        OTSetOperations op1p=(OTSetOperations)op1;
        OTSetOperations op2p=(OTSetOperations)op2;
        OTSetOperations ret=op1p;
        switch( op1p.getType()){
            case Add:
                switch( op2p.getType()){
                    case Add:
                        if (op1p.getElement().equals(op2p.getElement())){
                            ret=op1p;
                            ret.convToNop();
                        }
                        break;
                    case Del:
                    case Nop:
                }
                break;
            case Del:
                switch( op2p.getType()){
                    case Add:
                        break;
                    case Del:
                        if (op1p.getElement().equals(op2p.getElement())){
                            ret=op1p;
                            ret.convToNop();
                        }
                        break;
                    case Nop:
                }
                break;
            case Nop:
                
        }
        return ret;
    }

    @Override
    public SOCT2OperationInterface transposeBackward(SOCT2OperationInterface op1, SOCT2OperationInterface op2) {
                OTSetOperations op1p=(OTSetOperations)op1;
        OTSetOperations op2p=(OTSetOperations)op2;
        OTSetOperations ret=op1p;
        switch( op1p.getType()){
            case Add:
                switch( op2p.getType()){
                    case Add:
                        if (op1p.getElement().equals(op2p.getElement())){
                            ret=op1p;
                            ret.convFromNop();
                        }
                        break;
                    case Del:
                    case Nop:
                }
                break;
            case Del:
                switch( op2p.getType()){
                    case Add:
                        break;
                    case Del:
                         if (op1p.getElement().equals(op2p.getElement())){
                            ret=op1p;
                            ret.convFromNop();
                        }
                        break;
                    case Nop:
                }
                break;
            case Nop:
                
        }
        return ret;
    }
    
}
