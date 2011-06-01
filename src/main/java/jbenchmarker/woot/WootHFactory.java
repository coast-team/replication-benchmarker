package jbenchmarker.woot;

import jbenchmarker.core.MergeAlgorithm;
import jbenchmarker.core.ReplicaFactory;
import jbenchmarker.woot.wooth.WootHashDocument;
import jbenchmarker.woot.wooth.WootHashMerge;
/**
 *
 * @author urso
 */
public class WootHFactory implements ReplicaFactory {
    public MergeAlgorithm createReplica(int r) {
        return new WootHashMerge(new WootHashDocument(), r);
    }
}
