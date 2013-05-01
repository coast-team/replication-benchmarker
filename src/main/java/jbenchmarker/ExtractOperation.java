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

import java.io.*;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
/**
 *
 * @author score
 */
public class ExtractOperation {
    
      public static void main(String[] args) throws Exception {
          
          if (args.length < 3) {
              System.err.println("Arguments :");
              System.err.println("- File name");
              System.err.println("- number of increase point");
              System.err.println("- number of decrease point");
              System.exit(1);
          }
          int inc = Integer.parseInt(args[1]);
          int dec = Integer.parseInt(args[2]);
          Vector<Integer> TabInc = new Vector();
          Vector<Integer> TabDec = new Vector();
          Vector<Double> average = averageInTable(args[0]);

          //rechercher les éléments
          for (int j = 0; j < Math.max(inc, dec); j++) {
              double plus =0, moin=Double.MAX_VALUE;
              int lignePlus = 0, ligneMoins= 0;
              for (int i = 0; i < average.size(); i++) {
                  if(average.get(i)>plus && !TabInc.contains(i))
                  {
                      plus = average.get(i);
                      lignePlus = i;
                  }
                  if(average.get(i)<moin && !TabDec.contains(i))
                  {
                      moin = average.get(i);
                      ligneMoins = i;
                  }
              }
              TabInc.add(lignePlus);
              TabDec.add(ligneMoins);
          }
          
          //table begin by 0 and file by 1
          
          System.out.println("Max value at line : "+TabInc);
          System.out.println("Min Value at line : "+TabDec);
          
      }
      
      
    static Vector<Double> averageInTable(String filename) throws IOException {
        Vector<Double> avg = new Vector<Double>();

        InputStream ips1 = new FileInputStream(filename);
        InputStreamReader ipsr1 = new InputStreamReader(ips1);
        BufferedReader br1 = new BufferedReader(ipsr1);
        String Line;
        try {
            Line = br1.readLine();
            while (Line != null) {
                String tab[] = Line.split("\t");
                avg.add(Double.parseDouble(tab[(tab.length) - 1]));
                Line = br1.readLine();
                
            }
            br1.close();
        } catch (Exception e) {
            System.out.println(e.toString());
        }

        return avg;
    }
    
}
