package jbenchmarker.rgaTreeSplitBalanced;

import crdt.Operation;
import jbenchmarker.core.SequenceOperation;
import jbenchmarker.core.SequenceOperation.OpType;
import java.util.List;



public class RgaSDeletion<T> implements RgaSOperation {

	private OpType type;
	private RgaSS3Vector s3vpos;

	private int offset1;
	private int offset2;
	



	
	/*
	 *		Constructors
	 */

	public RgaSDeletion(OpType type, RgaSS3Vector s3vpos, int off1, int off2) {
		this.type = type;

		this.s3vpos = s3vpos;
		this.offset1 = off1;
		this.offset2 = off2;

	}
	

	public RgaSDeletion(RgaSS3Vector s3vpos, int off1, int off2 ) {
		this(OpType.delete, s3vpos, off1, off2);
	}

	@Override
	public Operation clone() {
		return new RgaSDeletion(s3vpos == null ? s3vpos : s3vpos.clone(),
				 offset1, offset2);
	}

	public int getReplica() {
		 return s3vpos.getSid();
	 }


	/*
	 *		toString, getReplica
	 */

	

	@Override
	 public String toString() {
		String ret = new String();
		if (getType() == SequenceOperation.OpType.delete) {
			ret += "del(";
		} else {
		}
		String s3va = s3vpos == null ? "null" : s3vpos.toString();
		
		ret += ", sv3pos: " + s3va + ", off1: " + offset1 + ", off2: " + offset2  ;

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
	 
	
	 public RgaSS3Vector getS3vpos() {
		 return s3vpos;
	 }

	 public void setS3vpos(RgaSS3Vector s3vpos) {
		 this.s3vpos = s3vpos;
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


