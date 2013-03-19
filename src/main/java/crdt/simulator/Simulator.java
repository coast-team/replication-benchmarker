/**
 * Replication Benchmarker
 * https://github.com/score-team/replication-benchmarker/
 * Copyright (C) 2012 LORIA / Inria / SCORE Team
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
package crdt.simulator;

import crdt.CRDT;
import crdt.Factory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author urso
 */
public abstract class Simulator {
    final public Map<Integer,CRDT> replicas;
    final public List<CRDT> forSerializ;
    
    // memoryUsed
    final protected List<Long> memUsed; 

    // operation's generation time
    final protected List<Long> genTime;
    
    // operation's integrate time 
    final protected List<Long> remoteTime;
    
    // operation's generation size
    final protected List<Integer> genSize;

   
    //final protected Map<Integer, List<Long>> remoteTime;
    
    final private Factory<? extends CRDT> rf;
    

    // logging file (null if no log)
    protected String logging = null;

    public String getLogging() {
        return logging;
    }

    public void setLogging(String logging) {
        this.logging = logging;
    }
    
    
    /**
     * Constructor of a Simulator. 
     * Replicas will be instanciated at run time.
     * @param rf a replica factory
     */
    public Simulator(Factory<? extends CRDT> rf) {
        this.replicas = new HashMap<Integer,CRDT>();
        this.memUsed = new ArrayList();
        this.genTime = new ArrayList<Long>();
        this.remoteTime = new ArrayList<Long>();
        this.genSize = new ArrayList<Integer>();
        this.rf = rf;
        this.forSerializ = new ArrayList<CRDT>();
    }

    public Map<Integer, CRDT> getReplicas() {
        return replicas;
    }

    public abstract void run(Trace trace) throws Exception ;
    
    /**
     * Runs a trace of operations. 
     * Iterates trough trace and apply each operation. 
     * Instanciate replica when needed using the replica factory.
     * For each operation apply (localy or remotely) store execution time. 
     * Optionally, computes the memory usage (costly operation)
     * @param trace a trace, i.e. a enumeration of TraceOperation 
     * @param detail true for a detail of each execution time false for only the overall sum.
     * @param nbrTrace frequency with which the calculated memory usage, 0 for no memory measurement.
     * @param overhead true for replica memory memory measurement, false for document measurement.
     * @throws Exception if the Trace is incorrect (non causal, etc..)
     * @see Trace, TraceOperation, crdt.CRDT
     */
   // public abstract void run(Trace trace, boolean detail, int nbrTrace, boolean overhead) throws Exception;    
    
    /**
     * Instanciate a new replica with classes given at construction. 
     * Adds the crated replica to the map.
     */
    public CRDT newReplica(int number) {

       CRDT r = rf.create();
        replicas.put(number, r);
        r.setReplicaNumber(number);
        return r;
    }

    /**
     * Memory occupied by whole framework.
     * One entry per measurement : trace size divided by nbrTrace.
     * @return a list of size in bytes, empty list if nbrTrace was 0
     */
    public List<Long> getMemUsed() {
        return memUsed;
    }
    
    /**
     * Replica generation times.
     * One entry per traceoperation.
     * @return a list of generation times in nanoseconds, empty list if detail was false
     */
    public List<Long> getGenerationTimes() {
        return genTime;
    }
    
    /**
     * Time for execution remote operation.
     * One entry per traceoperation.
     * @return a list of generation times in nanoseconds, empty list if detail was false
     */
    public List<Long> getRemoteTimes() {
        return remoteTime;
    }
}
