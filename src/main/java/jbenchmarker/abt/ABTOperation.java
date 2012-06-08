/**
 * Replication Benchmarker
 * https://github.com/score-team/replication-benchmarker/
 * Copyright (C) 2011 INRIA / LORIA / SCORE Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package jbenchmarker.abt;

import java.util.Formatter;

import jbenchmarker.core.SequenceMessage;
import collect.VectorClock;
import collect.VectorClock.Causality;
import jbenchmarker.core.SequenceOperation;
import jbenchmarker.core.SequenceOperation.OpType;

/**
*
* @author Roh
*/
public class ABTOperation<T> extends SequenceMessage{

	protected T 			c;
	protected int 			pos;
	protected VectorClock 	vc;
	protected final int		sid;	
	
	public ABTOperation(SequenceOperation o){
		super(o);
		this.sid = this.getOriginalOp().getReplica();
//		this.c	 ='\0';
	}
	
	//delete
	public ABTOperation(SequenceOperation o, int p, VectorClock vc) {
		super(o);
		// TODO Auto-generated constructor stub
		this.sid = this.getOriginalOp().getReplica();
		this.pos = p;		
		this.vc  = new VectorClock(vc);
//		this.c   = '\0';
	}
	
	//insert
	public ABTOperation(SequenceOperation o, int p, T c, VectorClock vc) {
		super(o);
		// TODO Auto-generated constructor stub
		this.sid = this.getOriginalOp().getReplica();
		this.pos = p;
		this.vc  = new VectorClock(vc);
		this.c	 = c;
	}

	public static Causality getRelation(ABTOperation op1, ABTOperation op2){
		Causality c = VectorClock.comp(op1.sid, op1.vc, op2.sid, op2.vc);
/*		boolean x=op1.vc.greaterThan(op2.vc);
		boolean y=op2.vc.greaterThan(op1.vc);
		if(x && y) {
			System.err.println("impossible case1");
			System.exit(1);
		} else if(x && !y && c!=Causality.HA){
			System.err.println("impossible case2 "+op1.sid+":"+op1.vc+" "+op2.sid+":"+op2.vc);
			System.exit(1);
		} else if(!x && y && c!=Causality.HB){
			System.err.println("impossible case3");
			System.exit(1);
		} else if(!x && !y && c!=Causality.CO){
			System.err.println("impossible case4");
			System.exit(1);
		}*/
		return c;
	}
	
    // FIXME: should be moved to SequenceMessage class?
    public OpType getType() {
        return this.getOriginalOp().getType();
    }
	
	@Override
	public SequenceMessage copy() {
		// TODO Auto-generated method stub
		ABTOperation op = new ABTOperation(getOriginalOp());
		op.pos = this.pos;
		op.c   = this.c;
		op.vc  = this.vc;		// do not copy vector clock. 
		return op;
	}
        
	
    @Override
	public String toString(){
		String ret = new String();
		Formatter fmt = new Formatter();
		Formatter fmt2 = new Formatter();
		fmt.format("%4d", this.pos);
		fmt2.format("%2d", this.sid);
		ret=fmt2+".";	
		
		if(getType()==OpType.ins) {
			ret+="ins("+fmt+",\'";			
		} else if(getType()==OpType.del) {
			ret+="del("+fmt+",\'";			
		}
//		if(this.c=='\0') ret+="\\0";
//		else if(this.c=='\n') ret+="\\n";
//		else if(this.c=='\t') ret+="\\t";
//		else 
                    ret+=this.c;
		ret+="\')";
		return ret;
	}
	
}
