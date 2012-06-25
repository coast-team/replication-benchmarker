/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker.ot.soct2;

import collect.VectorClock;
import crdt.Factory;
import java.io.Serializable;
import java.util.Map;
import jbenchmarker.core.Operation;

/**
 *
 * @param <O> Is type of operation managed in this algorithm
 * @author Stephane Martin
 * Algorithm SOCT2 With document, Log with transformations, and an vector clock.
 */
public class SOCT2 <O extends Operation> implements OTAlgorithm<O>, Serializable {

    final private VectorClock siteVC;
    final private SOCT2Log<O> log;
    //private Document doc;
    private int replicaNumber;

    final private GarbageCollector gc;
  
    /**
     * Make soct2 instance. 
     */
    public SOCT2(int siteId, Factory<SOCT2Log<O>> log, Factory<GarbageCollector> gc) {
        this.siteVC = new VectorClock();
        this.log = log.create();
        this.replicaNumber = siteId;
        this.gc = gc == null ? null : gc.create();            
    }
    
    /**
     * Make soct2 instance. 
     */
    public SOCT2(SOCT2TranformationInterface ot, int siteId, Factory<GarbageCollector> gc) {
        this(siteId, new SOCT2Log<O>(ot), gc);            
    }
    
    /**
     * Make soct2 instance. 
     */
    public SOCT2(SOCT2TranformationInterface ot, Factory<GarbageCollector> gc) {
        this(ot, 0, gc);
    }
    
    /**
     * Make soct2 instance. 
     */
    public SOCT2(Factory<SOCT2Log<O>> log, Factory<GarbageCollector> gc) {
        this(0, log, gc);
    }
    
    /**
     * 
     * @return the vector clock of the instance
     */
    @Override
    public VectorClock getSiteVC() {
        return siteVC;
    }

    /**
     * @return return log object
     */
    @Override
    public SOCT2Log getLog() {
        return log;
    }

    /**
     * Check if the operation is ready by its vector clock
     * @param siteId Site of operation
     * @param vcOp Vector clock of this operation.
     * @return true if its ready false else.
     */
    @Override
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
    @Override
    public OTMessage estampileMessage(O op) {
        this.siteVC.inc(this.replicaNumber);
        OTMessage ret = new OTMessage(new VectorClock(siteVC), replicaNumber, op);
        //this.siteVC.inc(this.siteId); For garbage collection
        this.log.add(ret);
        //doc.apply((Operation)op);
        if (gc != null) {
            this.gc.garbage(this); //Garbage collector
        }
        return ret;
    }

    /**
     * Integre operation sent by another site or replicats.
     * The operation is returned to apply on document
     * @param soct2message Is a message which contains the operation and vector clock
     * @return operation to performe on document
     */
    @Override
    public Operation integrateRemote(OTMessage soct2message) {

        if (this.readyFor(soct2message.getSiteId(), soct2message.getClock())) {
            if (gc != null) {
                this.gc.collect(this, soct2message); //Garbage collector
            }
            Operation op = this.log.merge(soct2message);
            if (gc != null) {
                this.gc.garbage(this); //Garbage collector
            }
            this.siteVC.inc(soct2message.getSiteId());
            return op;
        } else {
            throw new RuntimeException("it seems causal reception is broken in " + this.replicaNumber + " v: " + siteVC + " vs " + soct2message.getClock() + " from " + soct2message.getSiteId());
        }
    }

    @Override
    public void setReplicaNumber(int siteId) {
        this.replicaNumber = siteId;
    }

    @Override
    public OTAlgorithm<O> create() {
        return new SOCT2<O>(log, gc);
    }

    @Override
    public int getReplicaNumber() {
        return replicaNumber;
    }
}
