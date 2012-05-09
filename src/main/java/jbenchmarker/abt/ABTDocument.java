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

import java.util.ArrayList;
import java.util.List;

import jbenchmarker.core.Document;
import jbenchmarker.core.SequenceMessage;
import jbenchmarker.core.SequenceOperation.OpType;

/**
*
* @author Roh
*/
public class ABTDocument<T> implements Document{
	protected List<T> model;
	
	public ABTDocument(){
		model = new ArrayList<T>();
	}
	
	@Override
	public String view() {
		// TODO Auto-generated method stub
		return model.toString();
	}

	@Override
	public void apply(SequenceMessage op) {
		// TODO Auto-generated method stub
		ABTOperation abtop = (ABTOperation)op;
		try{
			if(abtop.getType()==OpType.del){
//				if(abtop.c=='\0') abtop.c = model.charAt(abtop.pos-1);
//				else {
					if(abtop.c != model.get(abtop.pos-1)) {
						System.err.println("Intention violation:"+abtop);						
						System.err.println("intention:"+abtop.c);
						System.err.println("but:"+model.get(abtop.pos-1));						
//						System.err.println("["+model.subList(0,abtop.pos-1)+"]"+model.get(abtop.pos-1)+"["+model.subList(abtop.pos)+"]");
						throw new RuntimeException(abtop+" ");
					}
//				}
				if(abtop.pos<1){
					throw new RuntimeException("Incorrect parameter");
				} else if(abtop.pos>model.size()){
					throw new RuntimeException("Incorrect parameter");
				} else {
//					model = model.subList(0,abtop.pos-1)+model.subList(abtop.pos);
				}

			} else {
				if(abtop.pos<0){
					throw new RuntimeException("Incorrect parameter");
				} else if(abtop.pos==0){
//					model = abtop.c+model;				 
				} else if(abtop.pos>model.size()) {					
					throw new RuntimeException("Incorrect parameter");
				} else {					
//					model = model.subList(0,abtop.pos)+abtop.c+model.subList(abtop.pos);					
				}
			}
		} catch(StringIndexOutOfBoundsException sioobe){
			sioobe.printStackTrace();
			System.exit(1);
		}

	}

}
