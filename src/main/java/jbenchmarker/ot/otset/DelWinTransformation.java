/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker.ot.otset;

import jbenchmarker.ot.soct2.SOCT2TranformationInterface;

/**
 *
 * @author stephane martin
 */
public class DelWinTransformation implements SOCT2TranformationInterface<OTSetOperations> {

   @Override
    public OTSetOperations transpose(OTSetOperations op1, OTSetOperations op2) {
        OTSetOperations op1p=(OTSetOperations)op1;
        OTSetOperations op2p=(OTSetOperations)op2;
        OTSetOperations ret=op1p;
        
        switch( op1p.getType()){
            case Add:
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
            case Del:
                switch( op2p.getType()){
                    case Add:
                        break;
                    case Del:
                        /*if (op1p.getElement().equals(op2p.getElement())){
                            ret=op1p;
                            ret.convToNop();
                        }*/
                        break;
                    case Nop:
                        
                }
                break;
            case Nop:
                
                
        }
        return ret;
    }

    @Override
    public OTSetOperations transposeBackward(OTSetOperations op1, OTSetOperations op2) {
                OTSetOperations op1p=(OTSetOperations)op1;
        OTSetOperations op2p=(OTSetOperations)op2;
        OTSetOperations ret=op1p;
        switch( op1p.getType()){
            case Add:
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
            case Del:
                switch( op2p.getType()){
                    case Add:
                        break;
                    case Del:
                         /*if (op1p.getElement().equals(op2p.getElement())){
                            ret=op1p;
                            ret.convFromNop();
                        }*/
                        break;
                    case Nop:
                }
                break;
            case Nop:
                
        }
        return ret;
    }

    @Override
    public String toString() {
        return "DelWinTransformation";
    }
    
}
