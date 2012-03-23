/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker;

import crdt.CRDT;
import crdt.Factory;
import crdt.simulator.CausalSimulator;
import crdt.simulator.Trace;
import crdt.simulator.random.RandomTrace;
import crdt.simulator.random.StandardSeqOpProfile;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.List;
/**
 *
 * @author score
 */
public class MainSimulation {

    static int base = 100;

    static public void main(String[] args) throws Exception {

        if (args.length < 13) {
            System.err.println("Arguments :");
            System.err.println("- Factory : a jbenchmaker.core.ReplicaFactory implementation ");
            System.err.println("- Number of execu : ");
            System.err.println("- duration : ");
            System.err.println("- perIns : ");
            System.err.println("- perBlock : ");
            System.err.println("- avgBlockSize : ");
            System.err.println("- sdvBlockSize : ");
            System.err.println("- probability : ");
            System.err.println("- delay : ");
            System.err.println("- sdv : ");
            System.err.println("- replicas : ");
            System.err.println("- thresold : ");
            System.err.println("- scale for serealization : ");
            System.exit(1);
        }
        Factory<CRDT> rf = (Factory<CRDT>) Class.forName(args[0]).newInstance();
        
        int nbExec = Integer.valueOf(args[1]);
        int nb = 1;
        if (nbExec > 1) {
            nb = nbExec + 1;
        }
        long duration = Long.valueOf(args[2]);
        double perIns = Double.valueOf(args[3]);
        double perBlock = Double.valueOf(args[4]);
        int avgBlockSize = Integer.valueOf(args[5]);
        double sdvBlockSize = Double.valueOf(args[6]);
        double probability = Double.valueOf(args[7]);
        long delay = Long.valueOf(args[8]);
        double sdv = Double.valueOf(args[9]);
        int replicas = Integer.valueOf(args[10]);
        int thresold = Integer.valueOf(args[11]);
        int scaleMemory = Integer.valueOf(args[12]);
        
        long ltime[][] = null, rtime[][] = null, mem[][] = null;
        int minSizeGen = 0, minSizeInteg = 0, minSizeMem = 0;
        int cop = 0, uop = 0, mop = 0;
        String[] nameUsr = args[0].split("\\.");
        
        for (int ex = 0; ex < nbExec; ex++) {
            System.out.println("execution : "+ ex);
            Trace trace = new RandomTrace(duration, RandomTrace.FLAT,
                    new StandardSeqOpProfile(perIns, perBlock, avgBlockSize, sdvBlockSize), probability, delay, sdv, replicas);
            CausalSimulator cd = new CausalSimulator(rf);
            cd.runWithMemory(trace, scaleMemory);

            if (ltime == null) {
                cop = cd.replicaRemoteTimes().get(0).size();
                uop = cd.replicaGenerationTimes().size();
                mop = cd.getMemUsed().size();
                ltime = new long[nb][uop];
                rtime = new long[nb][cop];
                mem = new long[nb][mop];
                minSizeGen = uop;
                minSizeInteg = cop;
                minSizeMem = mop;
            }
            
            List<Long> l = cd.replicaGenerationTimes();
            if(l.size() < minSizeGen)
                minSizeGen = l.size();
            toArrayLong(ltime[ex], l, minSizeGen);  
            computeAverage(ltime, thresold, minSizeGen);
            
            List<Long> m = cd.getMemUsed();
            if(m.size() < minSizeMem)
                minSizeMem = m.size();
            toArrayLong(mem[ex], m, minSizeMem);
            computeAverage(mem, thresold, minSizeMem);
            
            for (int r : cd.replicaRemoteTimes().keySet()) {
                if (minSizeInteg > cd.replicaRemoteTimes().get(r).size()) {
                    minSizeInteg = cd.replicaRemoteTimes().get(r).size();
                }
                for (int i = 0; i < minSizeInteg - 1; i++) {
                    rtime[ex][i] += cd.replicaRemoteTimes().get(r).get(i);
                }
            }
            for (int i = 0; i < minSizeInteg-1; i++) {
                rtime[ex][i] /= replicas;
            }
            computeAverage(rtime, thresold, minSizeInteg);
            
            cd = null;
            trace = null;
            System.gc();
            
        }

        String file = writeToFile(ltime, nameUsr[1], "usr", minSizeGen);
        treatFile(nameUsr[1], file, "usr", base);
        String file2 = writeToFile(rtime, nameUsr[1], "local", minSizeInteg);
        treatFile(nameUsr[1], file2, "local", base);
        String file3 = writeToFile(mem,nameUsr[1], "mem", minSizeMem);
        treatFile(nameUsr[1], file3, "mem", 1);
    }
    
    
     private static void toArrayLong(long[] t, List<Long> l, int minSize) {
        for (int i = 0; i < minSize-1; i++) {
            t[i] = l.get(i);
        }
    }
     
     /**
     * Write all array in a file
     */
    private static String writeToFile(long[][] data, String algo, String type, int minSize) throws IOException {
        String nameFile = algo + '-' + type + ".res";
        BufferedWriter out = new BufferedWriter(new FileWriter(nameFile));
        for (int op = 0; op < minSize-1; op++) {
            for (int ex = 0; ex < data.length; ex++) {
                out.append(data[ex][op] + "\t");
            }
            out.append("\n");
        }
        out.close();
        return nameFile;
    }
    
    
    static void treatFile(String Algo,String File,String result, int baz) throws IOException
    {
        int Tmoyen = 0;
        int cmpt = 0;
        String Line;
        String fileName = Algo+"-"+result+".data";
        PrintWriter ecrivain =  new PrintWriter(new BufferedWriter(new FileWriter(fileName)));
        InputStream ips1=new FileInputStream(File);
        InputStreamReader ipsr1=new InputStreamReader(ips1);
        BufferedReader br1=new BufferedReader(ipsr1);
        try{
           Line= br1.readLine();
            while (Line != null)
            {
                for(int i=0 ; i<baz; i++)
                {
                    if(Line != null)
                    {
                        Tmoyen += getLastValue(Line);
                        Line=  br1.readLine();
                    	cmpt++;
                    }
                    else
                        break;
                }
               Tmoyen = Tmoyen/cmpt;
               float tMicro  = Tmoyen;
               
                if (result.equals("mem"))
                    tMicro = Tmoyen / 1000; // microSeconde
               
               ecrivain.println(tMicro);
               Tmoyen=0;cmpt = 0;
            }
             br1.close(); 
             ecrivain.close();
          }		
          catch (Exception e){
                System.out.println(e.toString());
        }
        
    }
    static int getLastValue(String ligne)
    {
       String tab[] = ligne.split("\t");
       float t = Float.parseFloat(tab[(tab.length)-1]);
       return ((int)t);
    }
    

    public static void computeAverage(long[][] data, double thresold, int minSize) {
        int nbExpe = data.length - 1;//une colonne réserver à la moyenne
        for (int op = 0; op < minSize-1; op++) {
            long sum = 0;
            for (int ex = 0; ex < nbExpe; ex++) { // calculer moyenne de la ligne
                sum += data[ex][op];
            }
            long moy = 0, sum2 = 0, k = 0;
            if(nbExpe == 0)
            moy =sum;
            else moy = sum / nbExpe;
            
            for (int ex = 0; ex < nbExpe; ex++) {
                if (data[ex][op] < thresold * moy) {
                    sum2 += data[ex][op];
                    k++;
                }
            }
            if(k != 0)
                data[nbExpe][op] = sum2 /k;
        }
    }
    
}