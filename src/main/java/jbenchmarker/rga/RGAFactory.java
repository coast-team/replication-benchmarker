package jbenchmarker.rga;

import jbenchmarker.core.MergeAlgorithm;
import jbenchmarker.core.ReplicaFactory;

public class RGAFactory implements ReplicaFactory {

	public MergeAlgorithm createReplica(int r) {
		return new RGAMerge(new RGADocument(), r);
	}
}
