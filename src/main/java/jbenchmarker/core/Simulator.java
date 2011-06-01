package jbenchmarker.core;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import jbenchmarker.trace.TraceOperation;

/**
 *
 * @author urso
 */
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
    public abstract void run(Iterator<TraceOperation> trace) throws Exception;    
    
    /**
     * Instanciate a new replica with classes given at construction. 
     * Adds the crated replica to the map.
     */
    public MergeAlgorithm newReplica(int number) {
        MergeAlgorithm r = rf.createReplica(number);
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
