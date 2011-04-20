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

import jbenchmarker.core.Operation;
import jbenchmarker.trace.TraceOperation;
import jbenchmarker.trace.TraceOperation.OpType;

/**
*
* @author Roh
*/
public class RGAOperation extends Operation {
	
	public static boolean LOCAL 		= true;
	public static boolean REMOTE 	= false;
	
	private RGAS4Vector 	s4vpos;
	private RGAS4Vector  s4vtms;
	private char 				content;
	private boolean			lor;  // to be local or remote
	private int					intpos;
	
	public RGAOperation(TraceOperation o) {
		super(o);
		lor = LOCAL; 
	}
	
	public void setLocal(){
		lor = LOCAL;
	}
	
	public void setRemote(){
		lor = REMOTE;
	} 
	
	public boolean getLoR(){
		return lor;
	}
	
	public String toString(){
		String ret =new String();
		if(getType()==TraceOperation.OpType.del) ret +="del(";
		else ret+="ins(\'"+content+"\',";
		String s4va = s4vpos==null ? "null":s4vpos.toString();
		String s4vb = s4vtms==null ? "null":s4vtms.toString();
		ret += intpos + "," + s4vpos + ") with "+s4vtms; 
		
		return ret;
	}
	/*
	 * for insert
	 */
	public RGAOperation(TraceOperation o, int pos, RGAS4Vector s4vpos, char c, RGAS4Vector s4vtms){
		super(o);
		this.s4vpos 	= s4vpos;
		this.s4vtms	= s4vtms;
		this.intpos		= pos;
		this.content 	= c;
		this.lor = LOCAL;
	}
	
	/*
	 * for delete
	 */
	public RGAOperation(TraceOperation o, int pos, RGAS4Vector s4vpos, RGAS4Vector s4vtms){
		super(o);
		this.s4vpos	= s4vpos;
		this.s4vtms	= s4vtms;
		this.intpos 	= pos;
		this.lor = LOCAL;
	}	
	
	public int getIntPos(){
		return this.intpos;
	}
	
	public RGAS4Vector getS4VPos(){
		return this.s4vpos;	
	}
	
	public RGAS4Vector getS4VTms(){
		return this.s4vtms;
	}
	
	public char getContent(){
		return this.content;
	}
	
	public OpType getType(){
		return this.getOriginalOp().getType();
	}

    @Override
    public Operation clone() {
        return new RGAOperation(this.getOriginalOp(), intpos, 
                s4vpos == null ? s4vpos : s4vpos.clone(), content,  
                s4vtms == null ? s4vtms :s4vtms.clone());
    }
}
