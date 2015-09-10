package jbenchmarker.rgaTreeSplitBalanced;

import jbenchmarker.core.SequenceOperation.OpType;
import crdt.Operation;




public interface RgaSOperation<T> extends Operation {

	public int getReplica();
	public OpType getType();
}
