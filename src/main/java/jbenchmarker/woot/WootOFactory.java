package jbenchmarker.woot;

import jbenchmarker.core.MergeAlgorithm;
import jbenchmarker.core.ReplicaFactory;
import jbenchmarker.woot.wooto.WootOptimizedDocument;
/**
 *
 * @author urso
 */
public class WootOFactory implements ReplicaFactory {
    public MergeAlgorithm createReplica(int r) {
        return new WootMerge(new WootOptimizedDocument(), r);
    }
}
