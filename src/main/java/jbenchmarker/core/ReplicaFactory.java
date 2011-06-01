package jbenchmarker.core;

/**
 * Abstract factory to create replicas
 * @author urso
 */
public interface ReplicaFactory {
    public MergeAlgorithm createReplica(int r);
}
