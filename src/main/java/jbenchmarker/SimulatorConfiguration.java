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
