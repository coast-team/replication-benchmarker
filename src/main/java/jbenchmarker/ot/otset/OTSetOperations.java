/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker.ot.otset;

import collect.VectorClock;
import jbenchmarker.core.OperationBasedMessages;
import jbenchmarker.core.Document;

/**
 *
 * @author stephane martin
 */
public class OTSetOperations<Element> {

    @Override
    public OperationBasedMessages clone() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String toString() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

   /* @Override
    protected String visu() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected OperationBasedMessages copy() {
        throw new UnsupportedOperationException("Not supported yet.");
    }*/

    

    public enum OpType {
        Add, Del, Nop
    };
    private OpType type;
    private OpType noped;
    private Element e;
    private VectorClock clock;
    private final int siteId;
    
    public OTSetOperations(OpType type, Element e, VectorClock clock, int siteId) {
        this.type = type;
        this.e = e;
        this.clock = clock;
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
    }

    public void convFromNop() {
        type = noped;
        noped = null;
    }
    
    
    public void doOp(Document doc) throws Exception {
        OTSet docSet;
        if (doc instanceof OTSet) {
            throw new Exception("Bad type");
        }
        docSet=(OTSet)doc;
        switch (this.type) {
            case Add:
                
                break;
            case Del:
                break;
            case Nop:
            
        }
    }
}
