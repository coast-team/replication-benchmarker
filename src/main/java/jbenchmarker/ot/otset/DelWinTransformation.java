/**
 * Replication Benchmarker
 * https://github.com/score-team/replication-benchmarker/
 * Copyright (C) 2013 LORIA / Inria / SCORE Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
