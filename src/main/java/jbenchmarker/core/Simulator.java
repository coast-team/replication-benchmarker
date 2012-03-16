/**
 * Replication Benchmarker
 * https://github.com/score-team/replication-benchmarker/
 * Copyright (C) 2011 INRIA / LORIA / SCORE Team
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
package jbenchmarker.core;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import jbenchmarker.trace.SequenceOperation;

/**
 *
 * @author urso
 */
@Deprecated
public abstract class Simulator {
    final protected Map<Integer,MergeAlgorithm> replicas;

    // Local operations generated
    final protected List<Long> memUsed; 

    // replica's generation time
    final protected List<Long> genTime;
    
    final private ReplicaFactory rf;
    

    
    /**
     * Constructor of a Simulator. Replicas and Document will be instaciated at run time.
     * @param rf the factory
     */
    public Simulator(ReplicaFactory rf) {
        this.replicas = new HashMap<Integer,MergeAlgorithm>();
        this.memUsed = new java.util.ArrayList<Long>();
        this.genTime = new java.util.ArrayList<Long>();
        this.rf = rf;
    }

    public Map<Integer, MergeAlgorithm> getReplicas() {
        return replicas;
    }

    /*
     * Runs a trace of operations. Iterate trough trace and construct replica with documents while needed.
     */
    public abstract void run(Iterator<SequenceOperation> trace) throws Exception;    
    
    /**
     * Instanciate a new replica with classes given at construction. 
     * Adds the crated replica to the map.
     */
    public MergeAlgorithm newReplica(int number) {
        MergeAlgorithm r = rf.create(number);
        replicas.put(number, r);
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
}
