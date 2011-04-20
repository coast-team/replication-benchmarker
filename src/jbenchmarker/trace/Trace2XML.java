/**
 *   This file is part of ReplicationBenchmark.
 *
 *   ReplicationBenchmark is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   ReplicationBenchmark is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with ReplicationBenchmark.  If not, see <http://www.gnu.org/licenses/>.
 *
 **/

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jbenchmarker.trace;

/**
 *
 * @author medhi
 * @author urso
 */


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import jbenchmarker.core.MergeAlgorithm;
import jbenchmarker.core.Operation;
import jbenchmarker.core.ReplicaFactory;
import jbenchmarker.core.VectorClock;
import jbenchmarker.sim.CausalDispatcher;
import org.jdom.input.DOMBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


public class Trace2XML {
    private static final String fill = "";
//    private static final int fillerReplica = 99999;
    
    /**
     * @param args
     */
//    static InputStreamReader stream = null;
//    static LineNumberReader llog = null;

    public static void transformerXml(Document document, String fichier) throws TransformerConfigurationException, TransformerException {
        // Création de la source DOM
        Source source = new DOMSource(document);

        // Création du fichier de sortie
        File file = new File(fichier);
        Result resultat = new StreamResult(fichier);

        // Configuration du transformer
        TransformerFactory fabrique = TransformerFactory.newInstance();
        Transformer transformer = fabrique.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");

        // Transformation
        transformer.transform(source, resultat);
    }

    public static void main(String[] args) throws Exception  {

        // Création d'un nouveau DOM
        DocumentBuilderFactory fabrique = DocumentBuilderFactory.newInstance();
        DocumentBuilder constructeur = fabrique.newDocumentBuilder();
        Document document = constructeur.newDocument();
        // Propriétés du DOM
        document.setXmlVersion("1.0");
        document.setXmlStandalone(true);

        //Element racine0 = document.createElement("Traces");
        //document.appendChild(racine0);

        //premier serveur
        Element racine1 = document.createElement("Traces");
        document.appendChild(racine1);
        
         Element trace = document.createElement("Trace");
         racine1.appendChild(trace);

        BufferedWriter out = null;
        if (args.length>2) out = new BufferedWriter(new FileWriter(args[2]));
        //Pour le premier groupe
        Decompose(new InputStreamReader(new FileInputStream(args[0])), document, trace, true, true, out);
//        Decompose("2.log", document, racine1);
//        Decompose("4.log", document, racine1);
//        Decompose("5.log", document, racine1);

        transformerXml(document, args[1]);
    }
    
    public static Element operation(Document document, String type, String replica, String position, String timestamp, String doc, Node VC) {
        Element OP = document.createElement("Operation");
        Element t = document.createElement("Type");
        Element p = document.createElement("Position");
        Element r = document.createElement("NumReplica");
        Element ts = document.createElement("Timestamp");
        Element d = document.createElement("NumDocument");
                
        t.setTextContent(type);
        p.setTextContent(position);
        r.setTextContent(replica);
        ts.setTextContent(timestamp);
        d.setTextContent(doc);
        
        OP.appendChild(t);
        OP.appendChild(d);        
        OP.appendChild(r);
        OP.appendChild(p);        
        OP.appendChild(ts);
        OP.appendChild(VC);
        
        return OP;
    }
        
    public static Element insert(Document document, String replica, String position, String content, String timestamp, String doc, Node VC) {
        Element n = operation(document, "Ins", replica, position, timestamp, doc, VC);
        Element t = document.createElement("Text");
                
        t.setTextContent(content);        
        n.appendChild(t);
        return n;
    }

    public static Element delete(Document document, String replica, String position, String offset, String timestamp, String doc, Node VC) {
        Element n = operation(document, "Del", replica, position, timestamp, doc, VC);
        Element o = document.createElement("Offset");
                
        o.setTextContent(offset);        
        n.appendChild(o);
        return n;              
    }
    
    /**
     *  construct XML Entry of a vector clock
     */
    public static Element entry(Document document, String replica, String clock) {
        Element Entry = document.createElement("Entry");
        Element r = document.createElement("Replica");
        Element c = document.createElement("Clock");

        r.setTextContent(replica);
        Entry.appendChild(r);
        c.setTextContent(clock);
        Entry.appendChild(c);
        return Entry;
    }

    /**
     * Translate a input stream log into a XML trace
     * @param stream IO input stream of trace
     * @param document DOM doc to be generated
     * @param racine racine of trace 
     * @param checkCausality can throw IncorrectTrace if true
     * @param checkPosition can throw IncorrectTrace if operation are incorrect (out of bounds) on trace 
     * @param out output produced by log
     * @throws Exception IO or IncorrectTrace
     */
    static void Decompose(InputStreamReader stream, Document document, Element racine, boolean checkCausality, boolean checkPosition, BufferedWriter out) throws Exception {
        String[] Clock = null, Separation = null;
        String myLine;
        String[] Info;
        StringBuffer sb;
        Set<Integer> docs = new HashSet<Integer>();

        // Clocks : doc -> replica -> VC 
//        Map<Integer, Map<Integer, VectorClock>> clocks = new HashMap<Integer, Map<Integer, VectorClock>>();

        DOMBuilder builder = new DOMBuilder();
        
        // To test positions  
        Map<Integer, StringBuilder> docsTest = null;
        if (checkPosition) {
            docsTest = new HashMap<Integer, StringBuilder>();
        }
        
        LineNumberReader llog = new LineNumberReader(stream);
        String pos;
        String text = null;
                    
        try {
            while ((myLine = llog.readLine()) != null) {
                //System.out.println(myLine);
                Element OP;

                Clock = ExtraireVH(myLine);
                Element VH = document.createElement("VectorClock");

                for (int i = 0; i < Clock.length; i++) {
                    Separation = Clock[i].split("-");
                    VH.appendChild(entry(document,Separation[0].replace("[", ""),Separation[1].replace("]", "")));
                }
//                VH.appendChild(entry(document, "" + fillerReplica, "1"));
                                       
                if (myLine.contains("Del")) {
                    Info = DecomposeLigne("Del", myLine);
                    pos = Info[0].replace("(", "");
                    OP = Trace2XML.delete(document, Info[4], pos, Info[1], Info[2], Info[3], VH);
                } else {
                    Info = DecomposeLigne("Ins", myLine);
                    sb = new StringBuffer(Info[0]);
                    sb.delete(0, 1);
                    text = sb.toString();
                    
                    if(text.equals("\"\"\""))
                        text = "\"";
                    else
                        text = text.replace("\"", "");
                    text = text.replace("\\n", "\n");
                    pos = Info[1];
                    OP = Trace2XML.insert(document, Info[4], pos, text, Info[2], Info[3], VH);
                }      

                int doc = Integer.parseInt(Info[3]);
                docs.add(doc);
//                if (checkCausality) {  // brut force
//                    Map<Integer, VectorClock> dclock = clocks.get(doc);
//                    if (dclock == null) {
//                        dclock = new HashMap<Integer, VectorClock>();
//                        clocks.put(doc, dclock);
//                        if (myLine.contains("Del")) {
//                            throw new IncorrectTrace("First operation is del on document " + doc);
//                        } else {
//                        // Filler
//                           text = text + fill;
//                           OP.getLastChild().setTextContent(text);
//                        }
////                        dclock.put(fillerReplica, new VectorClock());
////                        dclock.get(fillerReplica).inc(fillerReplica);
////                        Element fillVector = document.createElement("VectorClock");
////                        fillVector.appendChild(entry(document, "" + fillerReplica, "1"));
////                        racine.appendChild(Trace2XML.insert(document, "" + fillerReplica , "0", filler, Info[2], Info[3], fillVector));
//                    }
//                    org.jdom.Element opd = builder.build(OP);
//                    TraceOperation opt = TraceGenerator.oneXML2OP(opd);
//                    if (!dclock.containsKey(opt.getReplica())) {
//                        dclock.put(opt.getReplica(), new VectorClock());
//                    }
//                    TraceGenerator.causalCheck(opt, dclock);
//                    if (!opt.getVC().greaterThan(dclock.get(opt.getReplica()))) 
//                        throw new IncorrectTrace("Bad order on VC " + opt);
//                    dclock.get(opt.getReplica()).upTo(opt.getVC());
//                }

                if (checkPosition) {
                    StringBuilder s = docsTest.get(doc);
                    if (s == null) {
                        s = new StringBuilder("");
                        docsTest.put(doc, s);
                    }
                    try {
                        int p = Integer.parseInt(pos);
                        if (myLine.contains("Ins")) {
                            s.insert(p, text);
                        } else {                            
                            int e = p + Integer.parseInt(Info[1]);
                            if (s.length()<e) throw new StringIndexOutOfBoundsException();
                            s.delete(p, e);
                        }
                    } catch (StringIndexOutOfBoundsException e) { // Position error
                       throw new IncorrectTrace("Operation position " + pos + " out of bound " + s.length() + " in \n" + s.toString());
                    }
                }
                racine.appendChild(OP);
            }
            if (out != null) {
                for (Entry<Integer, StringBuilder> d : docsTest.entrySet()) {
                    out.write("%%% ---------------------\n%%% Document " + d.getKey() + "\n%%% ---------------------\n");
                    out.write(d.getValue().toString() + "\n");
                }
                out.close();
            }
            if (checkCausality) {
                org.jdom.Element opl = builder.build(racine);
                for (int d : docs) {
                    Iterator<TraceOperation> trace = new TraceGenerator.TraceIterator(d, opl.getChildren().iterator());
                    CausalDispatcher cd = new CausalDispatcher(new ReplicaFactory() {
                        public MergeAlgorithm createReplica(int r) {
                            return new CausalChecker(r);
                        }
                    });
                    cd.run(trace);
                }
            }
            
            System.out.println("OK");
        } catch (Exception e) {
            // --- Gestion erreur lecture du fichier (fichier non existant, illisible, etc.)
            System.err.println("Error line number  : " + llog.getLineNumber());
            throw e;
        }
    }

    static String[] DecomposeLigne(String type, String line) {
        String[] info, aide, fin;

        aide = line.split(type);
        info = aide[1].split(",");

        String Sauv = "";

        for (int i = 0; i < info.length; i++) {
            if (info[i].startsWith("[")) {
                while (!info[i].endsWith("]")) {
                    i++;
                }
            } else {
                Sauv = Sauv + info[i] + "!";
            }
        }

        fin = Sauv.split("!");
        fin[4] = fin[4].replace(")", "");
        return fin;
    }

    static String[] ExtraireVH(String Chaine) {
        String[] divise = Chaine.split(","), fin;


        String essaye = "";

        for (int i = 0; i < divise.length; i++) {
            if (divise[i].startsWith("[")) {
                while (!divise[i].endsWith("]")) {
                    essaye = essaye + divise[i] + "!";
                    i++;
                }
                essaye = essaye + divise[i];
            }
        }

        fin = essaye.split("!");

        for (int j = 0; j < fin.length; j++) {
            if (fin[j].startsWith("[")) {
                fin[j] = fin[j].replace("[", "");
            } else if (fin[j].endsWith("]")) {
                fin[j] = fin[j].replace("]", "");
            }
        }

        return fin;

    }
}
