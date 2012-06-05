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
package results;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 *
 * @author mehdi urso
 */
public class TreatResultFile {
//    private static double thresold = 2.0;

    static long getLast(String line) {
        String tab[] = line.split("\t");
        return Long.parseLong(tab[tab.length - 1].trim());
    }
    
    static List<Long> fileToArray(File file) throws FileNotFoundException, IOException {
        BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        ArrayList<Long> l = new ArrayList<Long>();
        String line = r.readLine();
        while (line != null) {
            l.add(getLast(line));
            line = r.readLine();
        }
        return l;
    }

    static List<Long> smooth(List<Long> moy, int scale) {
       List<Long> l = new ArrayList<Long>();
       for (int i = 0; moy.size() - i > scale; i += 100) {
           long sum = 0;
           for (int j = 0; j < scale; ++j) {
               sum += moy.get(i+j);
           }
           l.add(sum/scale);
       }
       return l;
    }
    
    static double standardDeviation(double moy, List<Long> moyTab) {
        double carre = 0;

        for (long l : moyTab) {
            carre += Math.pow(l - moy, 2) / moyTab.size();
        }
        return Math.sqrt(carre);
    }

    static class ResultFilter implements FilenameFilter {
        String res;

        public ResultFilter(String res) {
            this.res = res;
        }

        public boolean accept(File file, String string) {
            return string.matches("\\w+-\\w+-" + res + ".res");
        }
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 4) {
            System.err.println("Arguments : dir_result smooth_usr smooth_gen smooth_mem");
            System.err.println("- dir_result : directory containing results");
            System.err.println("- smooth_usr : smooth used for user results ");
            System.err.println("- smooth_gen : smooth used for generated results ");
            System.err.println("- smooth_mem : smooth used for memory results ");
            System.exit(1);
        }
        
        Map<String, Integer> smooth = new java.util.HashMap<String, Integer>();
        File dir = new File(args[0]);

        smooth.put("usr", Integer.parseInt(args[1]));
        smooth.put("gen", Integer.parseInt(args[2]));
        smooth.put("mem", Integer.parseInt(args[3]));
        
        for (String res : smooth.keySet()) {
            int scale = smooth.get(res);
            File[] fs = dir.listFiles(new ResultFilter(res));
            Arrays.sort(fs, new Comparator<File>() { 
                public int compare(File t, File t1) {
                    return t.getName().compareTo(t1.getName());
                }                
            });
            for (File f : fs) {
                List<Long> moy = fileToArray(f);
                int nbOp = moy.size();
                String cut[] = f.getName().split("-");
                String algo = cut[0], trace = cut[1];                

                List<Long> s = smooth(moy, scale);               
                String fileName = algo + "-" + trace + "-" + res + ".data";                
                PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(fileName)));
                for (int i = 0; i < s.size(); i++) {
                    writer.println((i * scale) + '\t' + res);
                }

                long sum = 0, max = 0;
                for (long m : moy) {
                    if (max < m) {
                        max = m;
                    }
                    sum += m;
                }
                
                
                final String out = res + " - " + algo + " - " + trace;  
                System.out.println("AVG : " + out + " = " + (sum / nbOp));
                System.out.println("MAX : " + out + " = " + max);
                System.out.println("SDV : " + out + " = "
                        + (long) standardDeviation((double) sum / nbOp, moy));
                System.out.println();
            }
        }
    }

  

//                int nbOp = 0;
//            BufferedReader lr = new BufferedReader(new InputStreamReader(new FileInputStream(fs[0])));
//            while ((lr.readLine()) != null) {
//                nbOp++;
//            }
//            long[] moy;
//            if (fs.length > 1) {
//                long data[][] = new long[fs.length + 1][nbOp];
//                for (int i = 0; i < fs.length; i++) {
//                    fileToArray(data[i], fs[i]);
//                }
//                computeAverage(data, thresold);
//                moy = data[fs.length]; 
//            } else {
}
