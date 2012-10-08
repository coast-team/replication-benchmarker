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
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 *
 * @author urso
 */
public abstract class Simulator {
    final public Map<Integer,CRDT> replicas;
    
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
     * Constructor of a Simulator. Replicas and Document will be instaciated at run time.
     * @param rf the factory
     */
    public Simulator(Factory<? extends CRDT> rf) {
        this.replicas = new HashMap<Integer,CRDT>();
        this.memUsed = new ArrayList<Long>();
        this.genTime = new ArrayList<Long>();
        this.remoteTime = new ArrayList<Long>();
        this.genSize = new ArrayList<Integer>();
        this.rf = rf;
    }

    public Map<Integer, CRDT> getReplicas() {
        return replicas;
    }

    /*
     * Runs a trace of operations. Iterate trough trace and construct replica with documents while needed.
     */
    public abstract void run(Trace trace, boolean detail, int saveTrace,
    int nbrTrace, boolean o) throws Exception;    
    
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
     * Memory occupied by whole framework. One entry per traceoperation.
     */
    public List<Long> getMemUsed() {
        return memUsed;
    }
    
    /**
     * Replica generation times
     * @return 
     */
    public List<Long> replicaGenerationTimes() {
        return genTime;
    }
    
    /**
     * Time for execution remote operation
     * @return 
     */
    public List<Long> getRemoteTimes() {
        return remoteTime;
    }
}
