package jbenchmarker.RGALogootSplitTree;

public class LogootOperations {
	RgaSDocument doc;
	
	public LogootOperations(RgaSDocument doc) {
		this.doc= doc;
	}

	public void remoteInsert(RgaSOperation rgaop){
		System.out.println("\n\nINSERT");
		System.out.println(rgaop.getNewNodeID().getBaseFamily().getNodeList());
	}

	public void remoteDelete(RgaSOperation rgaop){
		System.out.println("\n\nDELETE");
	}
}
