/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker.ot.soct2;

import collect.VectorClock;
import java.util.Map;
import jbenchmarker.core.Operation;

/**
 *
 * @param <O> Is type of operation managed in this algorithm
 * @author Stephane Martin
 * Algorithm SOCT2 With document, Log with transformations, and an vector clock.
 */
public class SOCT2 <O extends Operation> {

    private VectorClock siteVC;
    private SOCT2Log<O> log;
    //private Document doc;
    private int siteId;

    private SOCT2GarbageCollector gc;
        
    
    /**
     * Make soct2 instance with document (to apply modification) transformations, and replicat number or site id
     * @param ot Tranformations
     * @param siteId Site identifier
     * @param gc is number of remote operation count before gc 0 disable the gc.
     */
    public SOCT2(SOCT2TranformationInterface ot, int siteId,int gcFrequency) {
        this.siteVC = new VectorClock();
        this.log = new SOCT2Log(ot);
        
        this.siteId = siteId;
        if (gcFrequency>0){
            this.gc=new SOCT2GarbageCollector(this,gcFrequency);
        }
    }

     /**
     * Make soct2 instance with document (to apply modification) transformations, and replicat number or site id
     * @param ot Tranformations
     * @param siteId Site identifier
     * @param gc is boolean to enable or disable gc with recommanded value.
     */
    public SOCT2(SOCT2TranformationInterface ot, int siteId,boolean  gc) {
        this.siteVC = new VectorClock();
        this.log = new SOCT2Log(ot);
        
        this.siteId = siteId;
        if (gc){
            this.gc=new SOCT2GarbageCollector(this);
        }
    }
    /**
     * 
     * @return the vector clock of the instance
     */
    public VectorClock getSiteVC() {
        return siteVC;
    }

    /**
     * @return return log object
     */
    public SOCT2Log getLog() {
        return log;
    }

    /**
     * @return replicat number or site identifier
     */
    public int getSiteId() {
        return siteId;
    }

    /**
     * Check if the operation is ready by its vector clock
     * @param siteId Site of operation
     * @param vcOp Vector clock of this operation.
     * @return true if its ready false else.
     */
    public boolean readyFor(int siteId, VectorClock vcOp) {
        //if (this.siteVC.getSafe(siteId) != vcOp.getSafe(siteId)) { Garbage collection
        if (this.siteVC.getSafe(siteId)+1 != vcOp.getSafe(siteId)) {
            return false;
        }
        for (Map.Entry<Integer, Integer> e : vcOp.entrySet()) {
            if ((e.getKey() != siteId) && (this.siteVC.getSafe(e.getKey()) < e.getValue())) {
                return false;
            }
        }
        return true;
    }

    /**
     * This method put on operation the vector clock, increse the vector clock
     * the operation is not applyed
     * @param op The operation 
     * @return Soct2Message with vector clock.
     */
    public SOCT2Message estampileMessage(O op) {
        this.siteVC.inc(this.siteId);
        SOCT2Message ret = new SOCT2Message(new VectorClock(siteVC), siteId, op);
        //this.siteVC.inc(this.siteId); For garbage collection
        this.log.add(ret);
        //doc.apply((Operation)op);

        return ret;
    }

    /**
     * Integre operation sent by another site or replicats.
     * The operation is returned to apply on document
     * @param soct2message Is a message which contains the operation and vector clock
     * @return operation to performe on document 
     */
    public Operation integrateRemote(SOCT2Message soct2message) {

        if (this.readyFor(soct2message.getSiteId(), soct2message.getClock())) {
            this.log.merge(soct2message);
            //this.getDoc().apply((Operation) Soct2message.getOperation());
            this.log.add(soct2message);
            this.siteVC.inc(soct2message.getSiteId());
            if (gc!=null)
                this.gc.collect(soct2message); //Garbage collector

            return soct2message.getOperation();
        } else {
            throw new RuntimeException("it seems causal reception is broken in "+this.siteId+" v: "+siteVC+" vs "+soct2message.getClock()+" from "+soct2message.getSiteId());
        }
    }

    public void setSiteId(int siteId) {
        this.siteId = siteId;
    }
    public void setReplicaNumber(int siteId) {
        this.siteId = siteId;
    }
}
