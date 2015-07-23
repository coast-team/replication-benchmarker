/*
 *  Replication Benchmarker
 *  https://github.com/score-team/replication-benchmarker/
 *  Copyright (C) 2013 LORIA / Inria / SCORE Team
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package jbenchmarker.RGALogootSplitTree;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author Stephane Martin <stephane@stephanemartin.fr>
 */
public class IdentifierInterval implements Serializable {
  

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((baseFamily == null) ? 0 : baseFamily.hashCode());
		result = prime * result + begin;
		result = prime * result + end;
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
		IdentifierInterval other = (IdentifierInterval) obj;
		if (baseFamily == null) {
			if (other.baseFamily != null)
				return false;
		} else if (!baseFamily.equals(other.baseFamily))
			return false;
		if (begin != other.begin)
			return false;
		if (end != other.end)
			return false;
		return true;
	}

	private LogootBaseFamily baseFamily;
    private int begin;
    private int end;

    public IdentifierInterval(LogootBaseFamily base, int begin, int end) {
        this.baseFamily = base;
        this.begin = begin;
        this.end = end;
    }

    public LogootBaseFamily getBaseFamily() {
        return baseFamily;
    }

    public List<Integer> getBase() {
        return baseFamily.getBase();
    }
 
    public int getBegin() {
        return begin;
    }

    public int getEnd() {
        return end;
    }
 
    public void setBegin(int begin) {
        this.begin = begin;
    }

    public void setEnd(int end) {
        this.end = end;
    }
     public void addBegin(int begin) {
        this.begin += begin;
    }

    public void addEnd(int end) {
        this.end += end;
    }

    @Override
  	public String toString() {
  		return baseFamily + ", "
  				+ begin + ", " + end + "]";
  	}
    
    public IdentifierInterval clone(){
    	return new IdentifierInterval(baseFamily, begin, end);
    }
    
    public int getOffset(){
    	return begin;
    }
    
}
