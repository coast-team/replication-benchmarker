package jbenchmarker.RGALogootSplitTree;

import crdt.Operation;
import jbenchmarker.core.SequenceOperation;
import jbenchmarker.core.SequenceOperation.OpType;
import java.util.List;



public class RgaSOperation<T> implements Operation {

	private OpType type;
	private List<T> content;
	
	private IdentifierInterval nodeIdPos;
	private IdentifierInterval newNodeID;
	
	private int offset1;
	private int offset2;
	
	private int pos;
	private int length;



	
	/*
	 *		Constructors
	 */

	public RgaSOperation(OpType type, List<T> c, IdentifierInterval nodeIdPos, IdentifierInterval newNodeID, int off1, int off2, int pos, int length) {
		this.type = type;
		this.content = c;
		this.newNodeID = newNodeID;
		this.nodeIdPos = nodeIdPos;
		this.offset1 = off1;
		this.offset2 = off2;
		this.pos=pos;
		this.length=length;
	}

	public RgaSOperation(List<T> c, IdentifierInterval nodeIdPos, IdentifierInterval newNodeID, int off1, int pos ) {
		this(OpType.insert, c, nodeIdPos, newNodeID, off1, 0,pos, 0);
	}

	public RgaSOperation(IdentifierInterval nodeIdPos, int off1, int off2, int pos, int length) {
		this(OpType.delete, null, nodeIdPos, nodeIdPos, off1, off2, pos, length);
	}

	@Override
	public Operation clone() {
		return new RgaSOperation(type, content, nodeIdPos == null ? nodeIdPos : nodeIdPos.clone(),
				newNodeID == null ? newNodeID : newNodeID.clone(), offset1, offset2, pos, length);
	}




	/*
	 *		toString, getReplica
	 */

	 public int getPos() {
		return pos;
	}

	public void setPos(int pos) {
		this.pos = pos;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	@Override
	 public String toString() {
		String ret = new String();
		if (getType() == SequenceOperation.OpType.delete) {
			ret += "del(";
		} else {
			ret += "ins(\'" + content + "\',";
		}
		String s3va = nodeIdPos == null ? "null" : nodeIdPos.toString();
		String s3vb = newNodeID == null ? "null" : newNodeID.toString();
		ret += ", sv3pos: " + s3va + ", sv3tms: " + s3vb + ", off1: " + offset1 + ", off2: " + offset2  ;

		return ret;
	 }

 
	 /*
	  *		Getters || Setters
	  */

	 public OpType getType() {
		 return type;
	 }

	 public void setType(OpType type) {
		 this.type = type;
	 }
	 
	 public List<T> getContent() {
		 return content;
	 }

	 public void setContent(List<T> content) {
		 this.content = content;
	 }

	 public IdentifierInterval getNodeIdPos() {
		 return nodeIdPos;
	 }

	 public void setNodeIdPos(IdentifierInterval nodeIdPos) {
		 this.nodeIdPos = nodeIdPos;
	 }

	 public IdentifierInterval getNewNodeID() {
		 return newNodeID;
	 }

	 public void setNewNodeID(IdentifierInterval newNodeID) {
		 this.newNodeID = newNodeID;
	 }

	 public int getOffset1() {
		 return offset1;
	 }

	 public void setOffset1(int offset1) {
		 this.offset1 = offset1;
	 }

	 public int getOffset2() {
		 return offset2;
	 }

	 public void setOffset2(int offset2) {
		 this.offset2 = offset2;
	 }

}


