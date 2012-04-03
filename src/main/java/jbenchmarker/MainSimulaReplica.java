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
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author score
 */
public class MainSimulaReplica {

    static public void main(String[] args) throws Exception {
        if (args.length < 14) {
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
            System.err.println("- name File : ");
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
        String nameUsr = args[13];

        double ltime[] = null, rtime[] = null, mem[] = null;
        for (int ex = 0; ex < nbExec; ex++) {
            System.out.println("algorithm : "+nameUsr+",execution : " + ex + ", with " + replicas + " replica");
            Trace trace = new RandomTrace(duration, RandomTrace.FLAT,
                    new StandardSeqOpProfile(perIns, perBlock, avgBlockSize, sdvBlockSize), probability, delay, sdv, replicas);
            CausalSimulator cd = new CausalSimulator(rf);
            cd.runWithMemory(trace, scaleMemory);

            if (ltime == null) {
                ltime = new double[nbExec];
                rtime = new double[nbExec];
                mem = new double[nbExec];
            }
            ltime[ex] = cd.getLocalAvg();
            rtime[ex] = cd.getRemoteAvg();
            double tab[] = new double[cd.getMemUsed().size()];
            toArrayDouble(tab, cd.getMemUsed());
            mem[ex] = calculAverag(tab);
        }

        writeFile(calculAverag(ltime), "usr", nameUsr);
        writeFile(calculAverag(rtime), "gen", nameUsr);
        writeFile(calculAverag(mem), "mem", nameUsr);
    }
    private static void toArrayDouble(double[] tab, List<Long> memUsed) {
        for(int i=0; i<memUsed.size(); i++)
            tab[i] = memUsed.get(i).doubleValue();
    }
    
    private static void writeFile(double val, String type, String algo) throws IOException {

        FileWriter file = new FileWriter(algo +"-"+ type + ".data", true);
        file.write(val+"\n");

        if (file != null) {
            file.close();
        }
    }
    
     private static double calculAverag(double[] data)
     {
         double val = 0;
         for(int i=0; i<data.length; i++)
             val += data[i];
         return (val/data.length);
     }
}
