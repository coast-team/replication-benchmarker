/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker.ot.otset;

import collect.VectorClock;
import jbenchmarker.ot.SOCT2OperationInterface;

/**
 *
 * @author stephane martin
 */
public class OTSetOperations <Element> implements SOCT2OperationInterface{
    public enum OpType{Add,Del,Nop};
    
    OpType type;
    OpType noped;
    Element e;

    public Element getElement() {
        return e;
    }

    public OpType getType() {
        return type;
    }
    public void convToNop(){
        noped=type;
    }
    public void convFromNop(){
        type=noped;
        noped=null;
    }
    
    @Override
    public VectorClock getClock() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getSiteId() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public void doOp(){
        
    }
}
