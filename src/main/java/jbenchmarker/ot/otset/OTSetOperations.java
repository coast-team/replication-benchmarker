/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker.ot.otset;

import jbenchmarker.core.Operation;
import crdt.OperationBasedMessagesBag;

/**
 *
 * @author stephane martin
 */
public class OTSetOperations<Element> implements Operation {

    @Override
    public Operation clone() {
       return new OTSetOperations(type, e, siteId);
    }

    
  
   /* @Override
    protected String toString() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected OperationBasedMessagesBag clone() {
        throw new UnsupportedOperationException("Not supported yet.");
    }*/

    

    public enum OpType {
        Add, Del, Nop
    };
    private OpType type;
    private OpType noped;
    private Element e;
    private final int siteId;
    
    public OTSetOperations(OpType type, Element e, int siteId) {
        this.type = type;
        this.e = e;
        this.siteId = siteId;
    }
    
    public Element getElement() {
        return e;
    }
    
    public OpType getType() {
        return type;
    }

    public void convToNop() {
        noped = type;
        type= OpType.Nop;
    }

    public void convFromNop() {
        type = noped;
        noped = null;
    }

    @Override
    public String toString() {
        return "OTSetOperations{" + "type=" + type + ", noped=" + noped + ", e=" + e + ", siteId=" + siteId + '}';
    }
   
}
