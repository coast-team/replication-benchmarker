/**
 * Replication Benchmarker
 * https://github.com/score-team/replication-benchmarker/
 * Copyright (C) 2012 LORIA / Inria / SCORE Team
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

        if (args.length != 2) {
            System.err.println("Arguments : ");
            System.err.println("- 1 ----> Bloc");
            System.err.println("- 2 ----> Ins");
            System.err.println("- 3 ----> Replica");
            System.err.println("- 4 ----> Operation");
            System.err.println("- [--with]----> with time Execution");
            System.exit(1);
        }
        
        int arg = Integer.parseInt(args[0]);
        String[] algo = new String[]{"LogootList", "Logoot" , "RGA", "Treedoc", "WootH", "WootO", "LogootS", "LogootList", "TTFWithoutGC"};
        //String[] algo = new String[]{"Logoot"};
        int nbFilebloc = 0, nbFileIns = 0, ins = 0, Replica = 0,
                operation = 0, nbFileReplic = 0, nbFileOp = 0;
        long tabUsr[] = null, tabGen[] = null, tabMem[] = null;
        String file = "";
        boolean with = Arrays.asList(args).contains("--with");
        for (int j = 0; j < algo.length; j++) {
            if (!algo[j].equals("TTFWithoutGC")) {
                nbFilebloc = 20;
                nbFileIns = 11;
                ins = 100;
                Replica = 50;
                operation = 50000;
                nbFileReplic = 10;
                nbFileOp = 9;
            } else {
                nbFilebloc = 11;
                nbFileIns = 7;
                ins = 80;
                Replica = 15;
                operation = 20000;
                nbFileReplic = 3;
                nbFileOp = 3;
            }

            if (arg == 1) { //Bloc
                tabUsr = new long[nbFilebloc];
                tabGen = new long[nbFilebloc];
                tabMem = new long[nbFilebloc];
                for (int i = 0; i < nbFilebloc; i++) {
                    if (i < 10) {
                        file = "trace00";
                    } else {
                        file = "trace0";
                    }
                    if (with) {
                        tabUsr[i] = keepAvgFile(algo[j] + "-" + file + i + "-usr.res");
                        tabGen[i] = keepAvgFile(algo[j] + "-" + file + i + "-gen.res");
                    }
                    tabMem[i] = keepAvgFile(algo[j] + "-" + file + i + "-mem.res");
                }
            } else if (arg == 2) { //Ins
                tabUsr = new long[nbFileIns];
                tabGen = new long[nbFileIns];
                tabMem = new long[nbFileIns];
                for (int i = 50, k = 0; i <= ins; i += 5, k++) {
                    if (with) {
                        tabUsr[k] = keepAvgFile(algo[j] + "-trace" + i + "-usr.res");
                        tabGen[k] = keepAvgFile(algo[j] + "-trace" + i + "-gen.res");
                    }
                    tabMem[k] = keepAvgFile(algo[j] + "-trace" + i + "-mem.res");
                }
            } else if (arg == 3) { //Replica
                tabUsr = new long[nbFileReplic];
                tabGen = new long[nbFileReplic];
                tabMem = new long[nbFileReplic];
                for (int i = 5, k = 0; i <= Replica; i += 5, k++) {
                    if (with) {
                        tabUsr[k] = keepAvgFile(algo[j] + "-trace" + i + "-usr.res");
                        tabGen[k] = keepAvgFile(algo[j] + "-trace" + i + "-gen.res");
                    }
                    tabMem[k] = keepAvgFile(algo[j] + "-trace" + i + "-mem.res");
                }
            } else { //Operation
                tabUsr = new long[nbFileOp];
                tabGen = new long[nbFileOp];
                tabMem = new long[nbFileOp];
                for (int i = 10000, k = 0; i <= operation; i += 5000, k++) {
                    if (with) {
                        tabUsr[k] = keepAvgFile(algo[j] + "-trace" + i + "-usr.res");
                        tabGen[k] = keepAvgFile(algo[j] + "-trace" + i + "-gen.res");
                    }
                    tabMem[k] = keepAvgFile(algo[j] + "-trace" + i + "-mem.res");
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
                    out.append(data[op]/1000 + "\n");//miliseconde? divice 1000
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
}
