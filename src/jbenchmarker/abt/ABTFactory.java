package jbenchmarker.abt;

import jbenchmarker.core.MergeAlgorithm;
import jbenchmarker.core.ReplicaFactory;

public class ABTFactory implements ReplicaFactory {
	public MergeAlgorithm createReplica(int r) {
		return new ABTMerge(new ABTDocument(), r);
	}
}
