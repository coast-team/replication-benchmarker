package jbenchmarker.rgaTreeSplitBalanced;

import collect.VectorClock;

import java.io.Serializable;

public class RgaSS3Vector implements Comparable<RgaSS3Vector>, Serializable {

	private int sid;
	private int sum;
	private int offset;

	public static final int AFTER = 1;
	public static final int EQUAL = 0;
	public static final int BEFORE = -1;




	/*
	 *		Constructors
	 */

	public RgaSS3Vector(int sid, int sum, int offset) {
		this.sid = sid;
		this.sum = sum;
		this.offset = offset;
	}

	public RgaSS3Vector(int sid, VectorClock vc, int offset) {
		this.sid = sid;
		this.sum = vc.getSum();
		this.offset = offset;
	}

	public RgaSS3Vector clone() {
		return new RgaSS3Vector(sid, sum, offset);
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




	/*
	 *		toString, compareTo, equals, hashCode
	 */

	@Override
	public String toString() {
		return "["+ sid + "," + sum + "," + offset + "]";
	}

	public int compareTo(RgaSS3Vector s3v) {

		if (this.sum > s3v.sum) {
			return AFTER;
		} else if (this.sum < s3v.sum) {
			return BEFORE;
		} else { // this.sum == s3v.sum
			if (this.sid > s3v.sid) {
				return AFTER;
			} else if (this.sid < s3v.sid) {
				return BEFORE;
			}  else {
				return EQUAL;
			}
		}
	}


	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final RgaSS3Vector other = (RgaSS3Vector) obj;

		if (this.sid != other.sid) {
			return false;
		}
		if (this.sum != other.sum) {
			return false;
		}
		if (this.offset != other.offset) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 79 * hash + this.sid;
		hash = 79 * hash + this.sum;
		hash = 79 * hash + this.offset;
		return hash;
	}

}

