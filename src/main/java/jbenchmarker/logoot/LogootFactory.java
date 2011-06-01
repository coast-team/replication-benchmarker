package jbenchmarker.logoot;

import jbenchmarker.core.MergeAlgorithm;
import jbenchmarker.core.ReplicaFactory;

/**
 *
 * @author urso
 */
public class LogootFactory implements ReplicaFactory {
    public MergeAlgorithm createReplica(int r) {
        return new LogootMerge(new LogootDocument(Long.MAX_VALUE), r, 64, new BoundaryStrategy(1000000000));
    }
}