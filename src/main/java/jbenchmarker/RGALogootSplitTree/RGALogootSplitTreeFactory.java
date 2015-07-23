package jbenchmarker.RGALogootSplitTree;
import jbenchmarker.core.MergeAlgorithm;
import jbenchmarker.core.ReplicaFactory;
import jbenchmarker.RGALogootSplitTree.*;

public class RGALogootSplitTreeFactory extends ReplicaFactory{

	@Override
	public MergeAlgorithm create(int r) {
		return new RgaSMerge(new RgaSDocument(), r);

	}

	static RgaSDocument createDoc(int r, int base) {
		return new RgaSDocument();
	}

	public static class ShortList<T> extends ReplicaFactory {
		@Override
		public RgaSMerge create(int r) {         
			return new RgaSMerge(createDoc(r, 16), r);
		}
	}



}
