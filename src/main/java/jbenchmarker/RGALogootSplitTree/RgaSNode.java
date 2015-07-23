package jbenchmarker.RGALogootSplitTree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class RgaSNode<T> implements Serializable {

	private IdentifierInterval nodeID;	 
	private List<T> content;

	private RgaSNode prev;
	private RgaSNode next;
	private RgaSTree tree;



	/*
	 *		Constructors
	 */

	public RgaSNode(IdentifierInterval iD, RgaSNode prev, RgaSNode next,
			RgaSTree tree, List<T> content) {
		super();
		this.nodeID = iD;
		this.prev = prev;
		this.next = next;
		this.tree = tree;
		this.content = content;
	}
	
	public RgaSNode() {
		this(null, null, null, null, null);
	}

	public RgaSNode(IdentifierInterval iD, List<T> content) {
		this(iD, null, null, null, content);
	}


	public RgaSNode(RgaSNode clone, List b, int offsetAbs) {
		this(clone.getNodeID().clone(), null, null, null, b);
		this.getNodeID().setBegin(offsetAbs);
		this.getNodeID().setEnd(offsetAbs+b.size()-1);
		
	}

	public RgaSNode clone(){
		return new RgaSNode(nodeID, prev, next, tree, content);
	}




	/*
	 *		toString, getContentAsString, equals, makeTombstone, and hashCode
	 */


	public String getContentAsString() {
		StringBuilder s = new StringBuilder();
		if (content!=null){
			for (T t : content) {

				s.append(t.toString());
			}
		}
		return s.toString();
	}

	@Override
	public String toString() {
		return "Nd[" + nodeID + ", " + content + "]";
	}



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((nodeID == null) ? 0 : nodeID.hashCode());
		result = prime * result + ((content == null) ? 0 : content.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RgaSNode other = (RgaSNode) obj;
		if (nodeID == null) {
			if (other.nodeID != null)
				return false;
		} else if (!nodeID.equals(other.nodeID))
			return false;
		if (content == null) {
			if (other.content != null)
				return false;
		} else if (!content.equals(other.content))
			return false;
		return true;
	}





	/*
	 *		Getters || Setters
	 */







	public RgaSNode getPrev() {
		return prev;
	}

	public void setPrev(RgaSNode prev) {
		this.prev = prev;
	}



	public RgaSNode getNext() {
		return next;
	}

	public IdentifierInterval getNodeID() {
		return nodeID;
	}

	public void setNodeID(IdentifierInterval iD) {
		nodeID = iD;
	}

	public void setNext(RgaSNode next) {
		this.next = next;
		if (next!=null) next.setPrev(this);
	}

	public RgaSTree getTree() {
		return tree;
	}

	public void setTree(RgaSTree tree) {
		this.tree = tree;
	}

	public List<T> getContent() {
		return content;
	}

	public void setContent(List<T> content) {
		this.content = content;
	}

	public void makeTombstone() {

		this.content = null;
		this.tree=null;
		
		
	}

	public int size(){
		if (this==null || this.content==null || this.equals(new RgaSNode())){
			return 0;
		} else {
			return (nodeID.getEnd()-nodeID.getBegin()+1) ;
			
		}
	}

	public List<Integer> getBase(){
		return nodeID.getBase();
	}

	public int getBegin(){
		return nodeID.getBegin();
	}

	public int getEnd(){
		return nodeID.getEnd();
	}

	public int getOffset(){
		return nodeID.getBegin();
	}
}
