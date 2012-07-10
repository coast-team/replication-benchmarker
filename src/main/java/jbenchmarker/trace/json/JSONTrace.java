
package jbenchmarker.trace.json;

import collect.VectorClock;
import com.google.gson.Gson;
import crdt.simulator.Trace;
import crdt.simulator.TraceOperation;
import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import jbenchmarker.core.SequenceOperation;
import jbenchmarker.trace.TraceGenerator;

/**
 * @author damien
 */

public class JSONTrace implements Trace{

    private ArrayList<TraceOperation> ops; 
       
    public JSONTrace(String nomFichier) throws FileNotFoundException, IOException {
        VectorClockCSMapper vectorClockMapper = new VectorClockCSMapper();
        Gson gson = new Gson();
        this.ops = new ArrayList<TraceOperation>();
        //FileReader f = new FileReader("/home/damien/etherpad-lite/var/dirtyCS.db");
        FileReader f = new FileReader(nomFichier);
        BufferedReader buf = new BufferedReader(f);
        String res = buf.readLine();
        
        FileWriter fw = new FileWriter("/home/damien/test1.txt");  
               
        while (res != null) {
            StringReader s = new StringReader(res);
            ElementJSON e = gson.fromJson(s, ElementJSON.class);
            
            String op = e.getVal().getOperation();
            int offset = e.getVal().getNumber_charDeleted();
            String str = e.getVal().getChars_inserted();
            int pos = e.getVal().getPosition();
            String uid = e.getVal().getUserId();
            VectorClockCS vc = e.getVal().getVector_clock();            
            int repli = vectorClockMapper.userId(uid);
            VectorClock v = vectorClockMapper.toVectorClock(vc);
            
            ElementCS ecs = new ElementCS(op,offset,str,pos,repli,v); 
            ElementJSON ejs = new ElementJSON(e.getKey(),ecs);
            String sir = gson.toJson(ejs);
            fw.write(sir+"\n");
            
            TraceOperation so = TraceGenerator.oneJSON2OP(e, vectorClockMapper);
            ops.add(so);         
            s.close();
            res = buf.readLine();
        }
        buf.close();
        fw.close();
        f.close();
    }
    
    
    public JSONTrace(String nomFichier,String padid) throws FileNotFoundException, IOException {
        VectorClockCSMapper vectorClockMapper = new VectorClockCSMapper();
        Gson gson = new Gson();
        this.ops = new ArrayList<TraceOperation>();
        
        FileReader f = new FileReader(nomFichier);
        BufferedReader buf = new BufferedReader(f);
        String res = buf.readLine();
        
        FileWriter fw = new FileWriter("/home/damien/test1.txt");  
  
        while (res != null) {
            StringReader s = new StringReader(res);
            ElementJSON e = gson.fromJson(s, ElementJSON.class);
            
            if(e.getKey().contains("key:padid:"+padid)){              
                String op = e.getVal().getOperation();
                int offset = e.getVal().getNumber_charDeleted();
                String str = e.getVal().getChars_inserted();
                int pos = e.getVal().getPosition();
                String uid = e.getVal().getUserId();
                VectorClockCS vc = e.getVal().getVector_clock();            
                int repli = vectorClockMapper.userId(uid);
                VectorClock v = vectorClockMapper.toVectorClock(vc);
            
                ElementCS ecs = new ElementCS(op,offset,str,pos,repli,v); 
                ElementJSON ejs = new ElementJSON(e.getKey(),ecs);
                String sir = gson.toJson(ejs);
                fw.write(sir+"\n");
                
                TraceOperation so = TraceGenerator.oneJSON2OP(e, vectorClockMapper);
                ops.add(so);                
            }
            s.close();
            res = buf.readLine();
        }
        buf.close();
        fw.close();
        f.close();
    }
    
    @Override
    public Enumeration<TraceOperation> enumeration() {
        return new Enumeration<TraceOperation>() {
            private Iterator<TraceOperation> it = ops.iterator();
            
            @Override
            public boolean hasMoreElements() {
                return it.hasNext();
            }

            @Override
            public TraceOperation nextElement() {
                return it.next();
            }            
        };
    }
    
    
    
    
    
}
