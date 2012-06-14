
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
            
            SequenceOperation so = TraceGenerator.oneJSON2OP(e, vectorClockMapper);
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
        
        ArrayList<Double> ins = new ArrayList<Double>();  
        ArrayList<Double> del = new ArrayList<Double>();
        ArrayList<Double> up = new ArrayList<Double>();
        ArrayList<Double> total = new ArrayList<Double>();
        
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
                
                SequenceOperation so = TraceGenerator.oneJSON2OP(e, vectorClockMapper);
                ops.add(so);
                
                 if (op.equals("insertion")){  
                    ins.add((double)str.length());
                }else if(op.equals("suppression")){
                     del.add((double)offset);
                }else if(op.equals("remplacement")){
                    up.add((double)(str.length()-offset));
                }
                
            }
            s.close();
            res = buf.readLine();
        }
                      
        //calcul variance et moyenne insertion
        double moy = 0;
        double v = 0;
        
        for(int i = 0; i< ins.size(); i++){
            moy = moy+ins.get(i);
        }
        double moyIns = moy/ins.size();
        
        for(int i = 0; i< ins.size(); i++){
            if(ins.get(i) > (3*moyIns)){
                ins.remove(i);
                i--;
            }
        }
        moy = 0;      
        for(int i = 0; i< ins.size(); i++){            
            moy = moy+ins.get(i);
            v = v+(ins.get(i)*ins.get(i));
            total.add((double)ins.get(i));
        }
        moyIns = moy/ins.size();
        double varIns = (v/ins.size())-(moyIns*moyIns);
   
        
        //calcul variance et moyenne suppression
        moy = 0;
        for(int i = 0; i< del.size(); i++){
            moy = moy+del.get(i);
        }
        System.out.println("del" +del);
        double moyDel = moy/del.size();       
        for(int i = 0; i < del.size(); i++){
            if(del.get(i) > (3*moyDel)){
                del.remove(i);
                i--;
            }
        }
        moy = 0;
        v = 0;
        for(int i = 0; i < del.size(); i++){            
            moy = moy+del.get(i);
            v = v+(del.get(i)*del.get(i));
            total.add(del.get(i));
        }
        System.out.println("del "+del);
        moyDel = moy/del.size();
        double varDel = (v/del.size())-(moyDel*moyDel);
        
        
        //calcul variance et moyenne update
        moy = 0;
        for(int i = 0; i < up.size(); i++){
            moy = moy+up.get(i);
        }
        double moyUp = moy/up.size();
        for(int i = 0; i< up.size(); i++){
            if(Math.abs(up.get(i)) > Math.abs(3*moyUp)){
               up.remove(i);
               i--;
            }
        }
        moy = 0;
        v = 0;
        for(int i = 0; i < up.size(); i++){            
            moy = moy+up.get(i);
            v = v+(up.get(i)*up.get(i));
            total.add(up.get(i));
        }
        moyUp = moy/up.size();
        double varUp = (v/up.size())-(moyUp*moyUp);
       
        
        //calcul variance et moyenne totale
        moy = 0;
        v = 0;
        for(int i = 0; i < total.size(); i++){            
            moy = moy+total.get(i);
            v = v+(total.get(i)*total.get(i));            
        }
       double moyTotale = moy/total.size();
       double varTotale = (v/total.size())-(moyTotale*moyTotale);
      
        
        System.out.println("%insertion : "+((double)ins.size()/(double)total.size())*100+"\n%suppresion : "+((double)del.size()/(double)total.size())*100+"\n%update : "+((double)up.size()/(double)total.size())*100);      
        System.out.println("moyenne insertion = "+moyIns+"\nmoyenne suppression = "+moyDel+"\nmoyenne update = "+moyUp);
        System.out.println("variance insertion = "+varIns+"\nvariance suppression = "+varDel+"\nvariance update = "+varUp);
        System.out.println("moyenne totale = "+moyTotale+"\nvariance totale = "+varTotale);
        
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
