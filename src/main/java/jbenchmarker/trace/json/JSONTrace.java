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
        
        //FileWriter fw = new FileWriter("/home/damien/test1.txt");  
               
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
            //String sir = gson.toJson(ejs);
            //fw.write(sir+"\n");
            
            TraceOperation so = TraceGenerator.oneJSON2OP(e, vectorClockMapper);
            ops.add(so);         
            s.close();
            res = buf.readLine();
        }
        buf.close();
        //fw.close();
        f.close();
    }
    
    
    public JSONTrace(String nomFichier,String padid) throws FileNotFoundException, IOException {
        VectorClockCSMapper vectorClockMapper = new VectorClockCSMapper();
        Gson gson = new Gson();
        this.ops = new ArrayList<TraceOperation>();
        
        FileReader f = new FileReader(nomFichier);
        BufferedReader buf = new BufferedReader(f);
        String res = buf.readLine();
        
        //FileWriter fw = new FileWriter("/home/damien/test1.txt");  
  
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
               //String sir = gson.toJson(ejs);
                //fw.write(sir+"\n");
                
                TraceOperation so = TraceGenerator.oneJSON2OP(e, vectorClockMapper);
                ops.add(so);                
            }
            s.close();
            res = buf.readLine();
        }
        buf.close();
        //fw.close();
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
