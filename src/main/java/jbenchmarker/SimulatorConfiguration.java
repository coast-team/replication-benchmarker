/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker;

import crdt.CRDT;
import crdt.Factory;
import crdt.simulator.Trace;
import java.io.Serializable;

/**
 *
 * @author Stephane Martin <stephane.martin@loria.fr>
 */
public class SimulatorConfiguration implements Serializable {

    private Trace trace;
    private Factory<CRDT> rf;
    private int thresold;
    private int scaleMemory;
    private boolean timeExecution;
    private boolean overHead;
    private int nbexec;
    private String outLog;

    public String getOutLog() {
        return outLog;
    }

    public void setOutLog(String outLog) {
        this.outLog = outLog;
    }
    
    
    public int getNbexec() {
        return nbexec;
    }

    public void setNbexec(int nbexec) {
        this.nbexec = nbexec;
    }

    public boolean isOverHead() {
        return overHead;
    }

    public void setOverHead(boolean overHead) {
        this.overHead = overHead;
    }

    public boolean isTimeExecution() {
        return timeExecution;
    }

    public void setTimeExecution(boolean timeExecution) {
        this.timeExecution = timeExecution;
    }

    public SimulatorConfiguration(Trace trace, Factory<CRDT> rf, int thresold, int scaleMemory) {
        this.trace = trace;
        this.rf = rf;
        this.thresold = thresold;
        this.scaleMemory = scaleMemory;
    }

    public int getScaleMemory() {
        return scaleMemory;
    }

    public void setScaleMemory(int scaleMemory) {
        this.scaleMemory = scaleMemory;
    }

    public int getThresold() {
        return thresold;
    }

    public void setThresold(int thresold) {
        this.thresold = thresold;
    }

    public Factory<CRDT> getRf() {
        return rf;
    }

    public void setRf(Factory<CRDT> rf) {
        this.rf = rf;
    }

    public Trace getTrace() {
        return trace;
    }

    public void setTrace(Trace trace) {
        this.trace = trace;
    }
}
