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
import org.jdom.*;
import org.jdom.output.*;

/**
 *
 * @author score
 */
public class TraceSimul2XML {
    
    static Element racine = new Element("Traces");
    static Element trace = new Element("Trace");
    static org.jdom.Document document = new Document(racine);
    
    public static void main(String[] args) throws Exception {
        if (args.length < 1 || args.length > 2) {
            System.err.println("Arguments : ");
            System.err.println("- trace : file of traces ");
            System.err.println("- xml : file xml to produce");

//            System.err.println("- duration ");
//            System.err.println("- probability  ");
//            System.err.println("- perIns  ");
//            System.err.println("- perBlock  ");
//            System.err.println("- avgBlockSize  ");
//            System.err.println("- sdvBlockSize  ");
//            System.err.println("- delay  ");
//            System.err.println("- replicas  ");
//            System.err.println("- Algorithme ");
            
            System.exit(1);
        }
         System.out.println("-----------------------");
        //creationTraceLog(args);
        
        racine.addContent(trace);
        try {
            InputStream ips = new FileInputStream(args[0]);
            InputStreamReader ipsr = new InputStreamReader(ips);
            BufferedReader br = new BufferedReader(ipsr);
            String line = "";
            int i=0;
            while ((line = br.readLine()) != null) {
                
                String[] data = line.split("\\|");
                String[] vh = ExtraireVH(data[3]);
                transformerXML(data, vh);
            }
            save(args[1]);
            br.close();
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }
    
    static void creationTraceLog(String[] arg) throws Exception {
        String[] args = new String[]{arg[10], "1", arg[2] , arg[4], arg[5], arg[6], arg[7], arg[3], arg[8], "10", arg[9], "2", "0", "Logoot-G1"};
        MainSimulation mn = new MainSimulation();
        mn.main(args);
    }
    
    static void save(String fileOutPut) {
        try {
            XMLOutputter sortie = new XMLOutputter(Format.getPrettyFormat());
            sortie.output(document, new FileOutputStream(fileOutPut));
        } catch (java.io.IOException e) {
        }
    }
    
    static void transformerXML(String[] data, String[] vh) {
         Element operation = new Element("Operation");
         trace.addContent(operation);
         
         Element type = new Element("Type");
         Element pos = new Element("Position");
         Element replica = new Element("NumReplica");
         Element doc = new Element("NumDocument");
         Element c;
         if (data[0].equals("Ins")) {
            c = new Element("Text");
        } else {
            c = new Element("Offset");
        }
        type.setText(data[0]);
        c.setText(data[1]);
        pos.setText(data[2]);
        replica.setText(data[4]);
        doc.addContent("1");
        
        operation.addContent(type);
        operation.addContent(c);
        operation.addContent(pos);
        operation.addContent(replica);
        operation.addContent(doc);

        Element vecteurClock = new Element("VectorClock");
        for (int i = 0; i < vh.length-1; i++) {//last is "{"
            Element entry = new Element("Entry");
            Element rep = new Element("Replica");
            Element clock = new Element("Clock");
            String[] rc = extractReplicClock(vh[i]);
            rep.setText(rc[0]);
            clock.setText(rc[1]);
            entry.addContent(rep);
            entry.addContent(clock);
            vecteurClock.addContent(entry);
        }
        operation.addContent(vecteurClock);
    }
    
    static String[] extractReplicClock(String chaine)
    {
        String[] rc = chaine.split(",");
        rc[0] = rc[0].replace("(", "");
        return rc;
    }
    static String[] ExtraireVH(String Chaine) {
        String[] vectH = Chaine.split("\\),");
        vectH[0] = vectH[0].replace("{", "");        
        vectH[vectH.length-1] = vectH[vectH.length-1].replace(",}", "");
        return vectH;
    }

}
