package jbenchmarker.RGALogootSplitTree;

import collect.VectorClock;

import java.io.Serializable;

public class RgaSS3Vector implements Comparable<RgaSS3Vector>, Serializable {

	private int position;
	private int sid;
	private int sum;
	private int offset;

	public static final int AFTER = 1;
	public static final int EQUAL = 0;
	public static final int BEFORE = -1;


	/*
	 *		Constructors
	 */

	public RgaSS3Vector(int position, int sid, int sum, int offset) {
		this.position=position;
		this.sid = sid;
		this.sum = sum;
		this.offset = offset;
	}

	public RgaSS3Vector(int position, int sid, VectorClock vc, int offset) {
		this.position=position;
		this.sid = sid;
		this.sum = vc.getSum();
		this.offset = offset;
	}

	public RgaSS3Vector clone() {
		return new RgaSS3Vector(position,sid, sum, offset);
	}




	/*
	 *		Getters && Setters
	 */

	public int getSid() {
		return sid;
	}

	public void setSid(int sid) {
		this.sid = sid;
	}

	public int getSum() {
		return sum;
	}

	public void setSum(int sum) {
		this.sum = sum;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}




	/*
	 *		toString, compareTo, equals, hashCode
	 */

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + offset;
		result = prime * result + position;
		result = prime * result + sid;
		result = prime * result + sum;
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
		RgaSS3Vector other = (RgaSS3Vector) obj;
		if (offset != other.offset)
			return false;
		if (position != other.position)
			return false;
		if (sid != other.sid)
			return false;
		if (sum != other.sum)
			return false;
		return true;
	}


	@Override
	public String toString() {
		return "RgaSS3Vector [position=" + position + ", sid=" + sid + ", sum="
				+ sum + ", offset=" + offset + "]";
	}

	public int compareTo(RgaSS3Vector s3v) {

		if (this.position > s3v.position) {
			return AFTER;
		} else if (this.position < s3v.position) {
			return BEFORE;
		
		} else {
			if (this.sum > s3v.sum) {
				return AFTER;
			} else if (this.sum < s3v.sum) {
				return BEFORE;
		
			} else { // this.sum == s3v.sum
				if (this.sid > s3v.sid) {
					return AFTER;
				} else if (this.sid < s3v.sid) {
					return BEFORE;
			
				} else { // this.sid == s3v.sid
					if (this.offset > s3v.offset) {
						return AFTER;
					} else if (this.offset < s3v.offset) {
						return BEFORE;
				
					} else { // this.offset == s3v.offset
						return EQUAL;
					}
				}
			}
		}
	}









}

