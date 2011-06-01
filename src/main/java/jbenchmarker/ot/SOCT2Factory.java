package jbenchmarker.ot;

import jbenchmarker.core.MergeAlgorithm;
import jbenchmarker.core.ReplicaFactory;

/**
 *
 * @author oster
 */
public class SOCT2Factory implements ReplicaFactory {
    public MergeAlgorithm createReplica(int siteId) {
        return new SOCT2MergeAlgorithm(new TTFDocument(), siteId);
    }
}
