/**
 *   This file is part of ReplicationBenchmark.
 *
 *   ReplicationBenchmark is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   ReplicationBenchmark is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with ReplicationBenchmark.  If not, see <http://www.gnu.org/licenses/>.
 *
 **/

package jbenchmarker.rga;

import jbenchmarker.core.VectorClock; 
/**
*
* @author Roh
*/
public class RGAS4Vector implements Comparable<RGAS4Vector> {
	public static final int AFTER 	= 1;
	public static final int EQUAL 	= 0;
	public static final int BEFORE	= -1;
	
	
	private int ssn;
	private int sid;
	private int sum;
	private int seq;
	
	public RGAS4Vector(int ssn, int sid, VectorClock vc){
		this.ssn		= ssn;	
		this.sid 		= sid;
		this.sum 	= vc.getSum();
		this.seq		= vc.getSafe(sid);
	}
	
	public RGAS4Vector(int sid, VectorClock vc){
		this(0, sid, vc);
	}
	@Override 
	public String toString(){
		return "["+ssn+","+sid+","+sum+"]";
	}
	
	@Override 
	public int compareTo(RGAS4Vector s4v) {
		// TODO Auto-generated method stub
		if(this.ssn > s4v.ssn) return  AFTER;
		else if (this.ssn < s4v.ssn) return BEFORE;
		else { // this.ssn==s4v.ssn
			if(this.sum > s4v.sum) return AFTER;
			else if(this.sum < s4v.ssn) return BEFORE;
			else { // this.sum == s4v.sum
				if(this.sid > s4v.sid) return AFTER;
				else if(this.sid < s4v.sid) return BEFORE;
				else return EQUAL;
			}
		}
	}

    public RGAS4Vector(int ssn, int sid, int sum, int seq) {
        this.ssn = ssn;
        this.sid = sid;
        this.sum = sum;
        this.seq = seq;
    }

    public RGAS4Vector clone() {
        return new RGAS4Vector(ssn, sid, sum, seq);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final RGAS4Vector other = (RGAS4Vector) obj;
        if (this.ssn != other.ssn) {
            return false;
        }
        if (this.sid != other.sid) {
            return false;
        }
        if (this.sum != other.sum) {
            return false;
        }
        if (this.seq != other.seq) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + this.ssn;
        hash = 79 * hash + this.sid;
        hash = 79 * hash + this.sum;
        hash = 79 * hash + this.seq;
        return hash;
    }

}
