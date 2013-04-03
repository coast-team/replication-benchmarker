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
package jbenchmarker;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;


/*
 * given all files result in parameter
 * result : one file ".final"
 */
public class ComputeAvgFile {

    public static void main(String[] args) throws FileNotFoundException, IOException {

        if (args.length != 4) {
            System.err.println("Arguments : ");
            System.err.println("- args[0]: 1.Bloc, 2.Ins, 3.Replica, 4.nbOp");
            System.err.println("- args[1] ----> Algorithm [split by ,]");
            System.err.println("- args[2] ----> Max");
            System.err.println("- [--with]----> with time Execution");
            System.exit(1);
        }
        
        int arg = Integer.parseInt(args[0]);
        //String[] algo = new String[]{"LogootList", "Logoot" , "RGA", "Treedoc", "WootH", "WootO", "LogootS", "LogootList", "TTFWithoutGC"};
            String[] algo = args[1].split(",");
        int nbFilebloc = 0, nbFileIns = 0, ins = 0, Replica = 0,
                operation = 0, nbFileReplic = 0, nbFileOp = 0;
        long tabUsr[] = null, tabGen[] = null, tabMem[] = null;
        String file = "";
        boolean with = Arrays.asList(args).contains("--with");
        for (int j = 0; j < algo.length; j++) {

            if (arg == 1) { //Bloc
                nbFilebloc = (Integer.parseInt(args[2])/10)+1;
                tabUsr = new long[nbFilebloc];
                tabGen = new long[nbFilebloc];
                tabMem = new long[nbFilebloc];
                for (int i = 0; i < nbFilebloc; i++) {
                        file = "trace.0."+i+"0";
                    if (with) {
                        tabUsr[i] = keepAvgFile(algo[j] + "-" + file +"-usr.res");
                        tabGen[i] = keepAvgFile(algo[j] + "-" + file +"-gen.res");
                    }
                    tabMem[i] = keepAvgFile(algo[j] + "-" + file +"-mem.res");
                }
            } else if (arg == 2) { //Ins
                ins = Integer.parseInt(args[2]);
                nbFileIns = ((ins-50)/5)+1;
                tabUsr = new long[nbFileIns];
                tabGen = new long[nbFileIns];
                tabMem = new long[nbFileIns];
                for (int i = 50, k = 0; i <= ins; i += 5, k++) {
                    if (with) {
                        tabUsr[k] = keepAvgFile(algo[j] + "-trace." + i + "-usr.res");
                        tabGen[k] = keepAvgFile(algo[j] + "-trace." + i + "-gen.res");
                    }
                    tabMem[k] = keepAvgFile(algo[j] + "-trace." + i + "-mem.res");
                }
            } else if (arg == 3) { //Replica
                Replica = Integer.parseInt(args[2]);
                nbFileReplic = nombreElementReplica(Replica);
                tabUsr = new long[nbFileReplic];
                tabGen = new long[nbFileReplic];
                tabMem = new long[nbFileReplic];
                for (int i = 2, k = 0; i <= Replica; i *= 2, k++) {
                    if (with) {
                        tabUsr[k] = keepAvgFile(algo[j] + "-trace." + i + "-usr.res");
                        tabGen[k] = keepAvgFile(algo[j] + "-trace." + i + "-gen.res");
                    }
                    tabMem[k] = keepAvgFile(algo[j] + "-trace." + i + "-mem.res");
                }
            } else { //Operation
                operation = Integer.parseInt(args[2]);
                nbFileOp = (operation/10000);
                tabUsr = new long[nbFileOp];
                tabGen = new long[nbFileOp];
                tabMem = new long[nbFileOp];
                for (int i = 10000, k = 0; i <= operation; i = i+10000, k++) {
                    if (with) {
                        tabUsr[k] = keepAvgFile(algo[j] + "-trace." + i + "-usr.res");
                        tabGen[k] = keepAvgFile(algo[j] + "-trace." + i + "-gen.res");
                    }
                    tabMem[k] = keepAvgFile(algo[j] + "-trace." + i + "-mem.res");
                }
            }
            String chaine = "";
            if (arg == 1) {
                chaine = "bloc";
            }
            if (arg == 2) {
                chaine = "ins";
            }
            if (arg == 3) {
                chaine = "rep";
            }
            if (arg == 4) {
                chaine = "op";
            }
            writeToFile(tabUsr, "../" + algo[j] + "-usr-" + chaine + ".final");
            writeToFile(tabGen, "../" + algo[j] + "-gen-" + chaine + ".final");
            writeToFile(tabMem, "../" + algo[j] + "-mem-" + chaine + ".final");
        }

    }

    private static String writeToFile(long[] data, String nameFile) throws IOException {
        BufferedWriter out = new BufferedWriter(new FileWriter(nameFile));
        for (int op = 0; op < data.length; ++op) {
            {
                //double d = (double)data[op]/1000;//MicroSecond
                if (!nameFile.contains("mem")) {
                    out.append(data[op] + "\n");//miliseconde? divice 1000
                } else {
                    out.append(data[op] + "\n");
                }
            }
        }
        out.close();
        return nameFile;
    }

    private static long keepAvgFile(String file) throws FileNotFoundException {
        long avg = 0L;
        String line;
        InputStream ips1 = new FileInputStream(file);
        InputStreamReader ipsr1 = new InputStreamReader(ips1);
        BufferedReader br1 = new BufferedReader(ipsr1);
        int nbLine = 0;
        try {
            line = br1.readLine();
            while (line != null) {
                nbLine++;
                String[] tab = line.split("\t");

                avg += (long) Double.parseDouble(tab[tab.length - 1]);
                line = br1.readLine();
            }
            br1.close();
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return avg / nbLine;
    }
    
    static int nombreElementReplica(int Replica)
    {
        //raison 2
        int somme = 0;
        for(int i=2; i<100; i*=2)
            somme += i;
        
        double k = somme/2; //divise premier terme (2)
        k = k+1;
        
        int n = (int)(Math.log(k)/Math.log(2));   
        
        return n;
    }
}
