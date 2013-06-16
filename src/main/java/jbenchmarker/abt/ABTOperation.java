/**
 * Replication Benchmarker
 * https://github.com/score-team/replication-benchmarker/
 * Copyright (C) 2013 LORIA / Inria / SCORE Team
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

import crdt.Operation;
import collect.VectorClock;
import collect.VectorClock.Causality;
import jbenchmarker.core.SequenceOperation;
import jbenchmarker.core.SequenceOperation.OpType;

/**
*
* @author Roh
*/
public class ABTOperation<T> implements Operation{

	protected T 			c;
	protected int 			pos, replica;
	protected VectorClock 	vc;
        final OpType type;
	//protected final int		sid;	

    public ABTOperation(OpType type, int replica, int pos, T c, VectorClock vc) {
        this.c = c;
        this.pos = pos;
        this.replica = replica;
        this.vc = vc;
        this.type = type;
    }

    public int getReplica() {
        return replica;
    }
	


	public static Causality getRelation(ABTOperation op1, ABTOperation op2){
		Causality c = VectorClock.comp(op1.getReplica(), op1.vc, op2.getReplica(), op2.vc);
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
	
    // FIXME: should be moved to Operation class?
    public OpType getType() {
        return type;
    }
	
	public Operation clone() {
		// TODO Auto-generated method stub
				// do not copy vector clock. 
		return new ABTOperation(type, replica, pos, c, vc);
	}
        
	
    @Override
	public String toString(){
		String ret = new String();
		Formatter fmt = new Formatter();
		Formatter fmt2 = new Formatter();
		fmt.format("%4d", this.pos);
		fmt2.format("%2d", this.getReplica());
		ret=fmt2+".";	
		
		if(getType()==OpType.insert) {
			ret+="ins("+fmt+",\'";			
		} else if(getType()==OpType.delete) {
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
