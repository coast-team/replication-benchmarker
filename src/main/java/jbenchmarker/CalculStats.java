package jbenchmarker;

import crdt.simulator.Trace;
import crdt.simulator.TraceOperation;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import jbenchmarker.core.SequenceOperation;
import jbenchmarker.core.SequenceOperation.OpType;
import jbenchmarker.trace.TraceGenerator;

/**
 * @author damien
 */


public class CalculStats {

    public static void main (String[]args) throws IOException{
        Trace trace = TraceGenerator.traceFromJson("../../traces/json/dirtyCSGerald3.db","notes003");
        Enumeration<TraceOperation> en = trace.enumeration();
        ArrayList<Double> ins = new ArrayList<Double>();  
        ArrayList<Double> del = new ArrayList<Double>();
        ArrayList<Double> up = new ArrayList<Double>();
        ArrayList<Double> total = new ArrayList<Double>();
        
        
        while(en.hasMoreElements()){
            SequenceOperation so = (SequenceOperation)en.nextElement();
            if (so.getType().equals(OpType.ins)){  
                    ins.add((double)so.getContentAsString().length());
                }else if(so.getType().equals(OpType.del)){
                     del.add((double)so.getNumberOf());
                }else if(so.getType().equals(OpType.up)){
                    up.add((double)(so.getContentAsString().length()-so.getNumberOf()));
                }
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
//        System.out.println("del" +del);
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
//        System.out.println("del "+del);
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
        
    }
}
