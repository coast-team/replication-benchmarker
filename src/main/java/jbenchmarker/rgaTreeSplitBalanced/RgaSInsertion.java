package jbenchmarker.rgaTreeSplitBalanced;

import crdt.Operation;
import jbenchmarker.core.SequenceOperation;
import jbenchmarker.core.SequenceOperation.OpType;
import java.util.List;



public class RgaSInsertion<T> implements RgaSOperation {

	private OpType type;
	private List<T> content;
	private RgaSS3Vector s3vpos;
	private RgaSS3Vector s3vtms;
	private int offset1;

	



	
	/*
	 *		Constructors
	 */

	public RgaSInsertion(OpType type, List<T> c, RgaSS3Vector s3vpos, RgaSS3Vector s3vtms, int off1) {
		this.type = type;
		this.content = c;
		this.s3vtms = s3vtms;
		this.s3vpos = s3vpos;
		this.offset1 = off1;

	}
	

	public RgaSInsertion(List<T> c, RgaSS3Vector s3vpos, RgaSS3Vector s3vtms, int off1 ) {
		this(OpType.insert, c, s3vpos, s3vtms, off1);
	}


	@Override
	public Operation clone() {
		return new RgaSInsertion(type, content, s3vpos == null ? s3vpos : s3vpos.clone(),
				s3vtms == null ? s3vtms : s3vtms.clone(), offset1);
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
			ret += "ins(\'" + content + "\',";
		}
		String s3va = s3vpos == null ? "null" : s3vpos.toString();
		String s3vb = s3vtms == null ? "null" : s3vtms.toString();
		ret += ", sv3pos: " + s3va + ", sv3tms: " + s3vb + ", off1: " + offset1  ;

		return ret;
	 }

	 public int getReplica() {
		 return s3vtms.getSid();
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

	 public RgaSS3Vector getS3vpos() {
		 return s3vpos;
	 }

	 public void setS3vpos(RgaSS3Vector s3vpos) {
		 this.s3vpos = s3vpos;
	 }

	 public RgaSS3Vector getS3vtms() {
		 return s3vtms;
	 }

	 public void setS3vtms(RgaSS3Vector s3vtms) {
		 this.s3vtms = s3vtms;
	 }

	 public int getOffset1() {
		 return offset1;
	 }

	 public void setOffset1(int offset1) {
		 this.offset1 = offset1;
	 }


}


