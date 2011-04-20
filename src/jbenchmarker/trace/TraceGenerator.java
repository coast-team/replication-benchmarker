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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import jbenchmarker.core.VectorClock;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.xml.sax.SAXException;


/**
 *
 * @author urso
 */
public class TraceGenerator {
    
    
    
    /*
     * Returns a association between replica number to the ordered list of trace operation. 
     */
    public static Map<Integer, List<TraceOperation>> historyPerReplica(List<TraceOperation> trace) {
        Map<Integer, List<TraceOperation>> histories = new HashMap<Integer, List<TraceOperation>>();
        for (TraceOperation opt : trace) {
            int r = opt.getReplica();
            List<TraceOperation> l = histories.get(r);
            if (l==null) {
                l = new ArrayList<TraceOperation>();
                histories.put(r, l);
            }
            l.add(opt);
        }
        return histories;
    }
    
    /**
     * Parses one elmeent into a trace operation
     * @param e JDOM Element
     * @return the parsed Trace operation 
     */
    public static TraceOperation oneXML2OP(Element e) {
        int p = Integer.parseInt(e.getChildText("Position"));
        int r = Integer.parseInt(e.getChildText("NumReplica"));
        List vce = e.getChild("VectorClock").getChildren();
        VectorClock v = new VectorClock();

        for (Object q : vce) {
            Element entry = (Element) q;
            v.put(Integer.parseInt(entry.getChildText("Replica")),
                    Integer.parseInt(entry.getChildText("Clock")));
        }
        if (e.getChildText("Type").equals("Ins")) {
            return TraceOperation.insert(r, p, e.getChildText("Text"), v);
        } else {
            return TraceOperation.delete(r, p, Integer.parseInt(e.getChildText("Offset")), v);
        }
    }
    
    
    public static class TraceIterator implements Iterator<TraceOperation> {
        Iterator children;
        int docnum;
        TraceOperation next;
        boolean goNext;
        int size;
        int line;
        

        public TraceIterator(int docnum, Iterator children) {
            this.children = children;
            this.docnum = docnum;
            this.goNext = true;
            this.size = -1;
        }

        private TraceIterator(int docnum, Iterator children, int size) {
            this.children = children;
            this.docnum = docnum;
            this.goNext = true;
            this.size = size;
        }

        @Override
        public boolean hasNext() {
            if (line == size) return false;
            if (!children.hasNext()) return false;
            if (goNext) {
                Element e = (Element) children.next();
                while (children.hasNext() && Integer.parseInt(e.getChildText("NumDocument")) != docnum) {
                    e = (Element) children.next();
                }
                if (Integer.parseInt(e.getChildText("NumDocument")) != docnum) return false;
                next = oneXML2OP(e);  
                goNext = false;
                return true;
            }
            return children.hasNext();
        }

        @Override
        public TraceOperation next() {
            if (goNext && !hasNext()) throw new NoSuchElementException();
            goNext = true;
            line++;
            return next;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }
    
    /**
     *  Extract trace form XML JDOM document
     */
    public static Iterator<TraceOperation> traceFromXML(Document document, int docnum) throws JDOMException, IOException  {
        List trace = document.getRootElement().getChild("Trace").getChildren();
        
        return new TraceIterator(docnum, trace.iterator()); 
    }

    /**
     *  Extract trace form XML JDOM document up to size operations
     */
    public static Iterator<TraceOperation> traceFromXML(Document document, int docnum, int size) throws JDOMException, IOException  {
        List trace = document.getRootElement().getChild("Trace").getChildren();
        
        return new TraceIterator(docnum, trace.iterator(), size); 
    }
    
    
    /**
     * Extract trace form XML uri.
     */
    public static Iterator<TraceOperation> traceFromXML(String uri, int docnum) throws JDOMException, IOException  {        
        return traceFromXML((new SAXBuilder()).build(uri), docnum);
    }

    /**
     * Extract trace form XML uri.
     */
    public static Iterator<TraceOperation> traceFromXML(String uri, int docnum, int size) throws JDOMException, IOException  {        
        return traceFromXML((new SAXBuilder()).build(uri), docnum, size);
    }
    
    
    /**
     * Verifies causality of an operation according to replicas VectorClock.
     */
    public static void causalCheck(TraceOperation opt, Map<Integer, VectorClock> vcs) throws IncorrectTrace {
        int r = opt.getReplica();
        if (opt.getVC().getSafe(r) == 0) {
            throw new IncorrectTrace("Zero/no entry in VC for replica" + opt);
        }
        VectorClock clock = vcs.get(r);
        if (clock.getSafe(r) > opt.getVC().get(r) - 1) {
            throw new IncorrectTrace("Already seen clock operation " + opt);
        }
        if (clock.getSafe(r) < opt.getVC().getSafe(r) - 1) {
            throw new IncorrectTrace("Missing operation before " + opt);
        }
        for (int i : opt.getVC().keySet()) {
            if ((i != r) && (opt.getVC().get(i) > 0)) {
                if (vcs.get(i)==null) 
                    throw new IncorrectTrace("Missing replica " + i + " for " + opt);
                if (vcs.get(i).getSafe(i) < opt.getVC().get(i))
                    throw new IncorrectTrace("Missing causal operation before " + opt + " on replica " + i);
            }
        }
    }
}
