package jbenchmarker.abt;

import java.util.ArrayList;
import java.util.List;

import jbenchmarker.core.Document;
import jbenchmarker.core.Operation;
import jbenchmarker.trace.TraceOperation.OpType;

/**
*
* @author Roh
*/
public class ABTDocument implements Document{
	protected String model;
	
	public ABTDocument(){
		model = new String();
	}
	
	@Override
	public String view() {
		// TODO Auto-generated method stub
		
		return model;
	}

	@Override
	public void apply(Operation op) {
		// TODO Auto-generated method stub
		ABTOperation abtop = (ABTOperation)op;
		//System.out.println("{"+model+"}");
		try{
			if(abtop.getType()==OpType.del){
				if(abtop.pos<1){
					throw new RuntimeException("Incorrect parameter");
				} else if(abtop.pos==1){
					model = model.substring(abtop.pos-1);
				} else if(abtop.pos == model.length()){
					model = model.substring(0,abtop.pos-1);
				} else if(abtop.pos>model.length()){
					throw new RuntimeException("Incorrect parameter");
				} else {
					model = model.substring(0,abtop.pos-1)+model.substring(abtop.pos);
				}
			} else {
				//System.out.println(model.length()+"  "+abtop.pos);
				if(abtop.pos<0){
					throw new RuntimeException("Incorrect parameter");
				} else if(abtop.pos==0){
					model = abtop.c+model;
				} else if(abtop.pos==model.length()){
					model = model+abtop.c;
				} else if(abtop.pos>model.length()) {
					System.err.println(abtop.sid+" "+abtop.pos+"  "+model.length());
					throw new RuntimeException("Incorrect parameter");
				} else {					
					model = model.substring(0,abtop.pos)+abtop.c+model.substring(abtop.pos);
				}
			}
		} catch(StringIndexOutOfBoundsException sioobe){
			sioobe.printStackTrace();
			System.exit(1);
		}
		//System.out.println("["+model+"]");
		
	}

}
