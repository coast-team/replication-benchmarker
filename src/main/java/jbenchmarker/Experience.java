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


/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

/**
 *
 * @author score
 */
public abstract class Experience<T> {

    abstract String createName(String[] args);    
    
    public static void extractFiles(File dir, String gitdir, List<String> paths) {
        for (File f : dir.listFiles()) {
            if (f.isFile()) {
                paths.add(f.getAbsolutePath().substring(gitdir.length() + 1));
            } else if (f.isDirectory() && !".git".equals(f.getName())) {
                extractFiles(f, gitdir, paths);
            }
        }
    }

    public void toArrayLong(long[] t, List<Long> l) {
        int min = Math.min(t.length, l.size());
        for (int i = 0; i < min; ++i) {
            t[i] = l.get(i);
        }
    }


    public int getLastValue(String ligne) {
        String tab[] = ligne.split("\t");
        float t = Float.parseFloat(tab[(tab.length) - 1]);
        return ((int) t);
    }

    public void computeAverage(long[][] data, double thresold, int l) {
        int nbExpe = data.length - 1;

        for (int op = 0; op < l; ++op) {
            long sum = 0;
            for (int ex = 0; ex < nbExpe; ++ex) {
                sum += data[ex][op];
            }
            long moy = sum / nbExpe, sum2 = 0, k = 0;
            for (int ex = 0; ex < nbExpe; ++ex) {
                if (data[ex][op] < thresold * moy) {
                    sum2 += data[ex][op];
                    k++;
                }
            }
            if (k != 0) {
                data[nbExpe][op] = sum2 / k;
            }
        }
    }

    public long calcul(long[][] data, int l, String type, String file, String s) throws IOException {
        long avg = 0L;
        if (type.equals("mem") && !file.contains("Logoot")) {
            for (int ex = 0; ex < l; ++ex) {
                avg += data[0][ex];
            }
        } else {
            for (int ex = 0; ex < l; ++ex) {
                avg += data[data.length - 1][ex];
            }
        }
        if (s.equals("avg") && l > 0) {
            avg = avg / l;
        }
        return avg;
    }
    
        public void writeToFile(long[][] data, String fileName, String type) throws IOException {

        String nameFile = fileName + '-' + type + ".res";
        nameFile = nameFile.replaceAll("/", "-");
        BufferedWriter out = new BufferedWriter(new FileWriter(nameFile));
        if (type.equals("mem") && !fileName.contains("Logoot")) {
            for (int op = 0; op < data[0].length; ++op) {
                out.append(data[0][op] + "\n");
            }
        } else {
            for (int op = 0; op < data[0].length; ++op) {
                for (int ex = 0; ex < data.length; ++ex) {
                    out.append(data[ex][op] + "\t");
                }
                out.append("\n");
            }
        }
        out.close();
        //return nameFile;
    }
    
   public void writeTofile(String file, String s) throws IOException {
        FileWriter local = new FileWriter(file + ".csv", true);

        local.write(s);

        if (local != null) {
            local.close();
        }
    }
   
   
    public void writeTofile(String file, List<String> s) throws IOException {

        for (int i = 0; i < s.size(); i++) {
            FileWriter local = new FileWriter(file + ".csv", true);

            local.write(s.get(i) + "\n");

            if (local != null) {
                local.close();
            }
        }

    }

    
    public int serializ(Map m) throws IOException {
        ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
        ObjectOutputStream stream = new ObjectOutputStream(byteOutput);
        stream.writeObject(m);

        int sizeMessage = byteOutput.size();
        
        //System.out.println("replica :"+m.getReplicaNumber()+" has "+byteOutput.size()+" byte");
        
        byteOutput.reset();
        stream.reset();
        stream.flush();
        stream.close();
        byteOutput.flush();
        byteOutput.close();
        return sizeMessage;
        //System.out.println("After: replica :"+m.getReplicaNumber()+" has "+byteOutput.size()+" byte");
    }

}
