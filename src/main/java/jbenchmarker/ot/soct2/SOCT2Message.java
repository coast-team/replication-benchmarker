/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker.ot.soct2;

import collect.VectorClock;
import jbenchmarker.core.Operation;

/**
 *
 * @author Stephane Martin Message contains remote operation.
 */
public class SOCT2Message<Op extends Operation>{
    

    VectorClock vc;
    int siteID;
    Op operation;

    public SOCT2Message(VectorClock vc, int siteID, Op operation) {
        //super(op);
        this.vc = vc;
        this.siteID = siteID;
        this.operation = operation;
    }

    public Op getOperation() {
        return operation;
    }

    public void setOperation(Op operation) {
        this.operation = operation;
    }

    public int getSiteId() {
        return siteID;
    }

    public void setSiteId(int siteID) {
        this.siteID = siteID;
    }

    public VectorClock getVc() {
        return vc;
    }

    public VectorClock getClock() {
        return vc;
    }

    public void setVc(VectorClock vc) {
        this.vc = vc;
    }
    
    @Override
    public SOCT2Message clone(){
        return new SOCT2Message(new VectorClock(vc),siteID,operation.clone() );
    }

    
}
