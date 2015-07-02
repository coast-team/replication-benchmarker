
package jbenchmarker.factories;

import jbenchmarker.core.MergeAlgorithm;
import jbenchmarker.core.ReplicaFactory;
import jbenchmarker.rgasplit.*;


public class RgaSFactory extends ReplicaFactory {

	@Override
	public MergeAlgorithm create(int r) {
		return new RgaSMerge(new RgaSDocument(), r);

	}

	static RgaSDocument createDoc(int r, int base) {
		return new RgaSDocument();
	}
}

