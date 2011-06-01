package jbenchmarker.woot;

import jbenchmarker.core.MergeAlgorithm;
import jbenchmarker.core.ReplicaFactory;
import jbenchmarker.woot.original.WootOriginalDocument;

/**
 *
 * @author urso
 */
public class WootFactory implements ReplicaFactory {
    public MergeAlgorithm createReplica(int r) {
        return new WootMerge(new WootOriginalDocument(), r);
    }
}
