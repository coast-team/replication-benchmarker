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
package jbenchmarker.trace;

import jbenchmarker.core.SequenceOperation;
import crdt.simulator.IncorrectTraceException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import collect.VectorClock;
import crdt.simulator.Trace;
import crdt.simulator.TraceOperation;
import java.util.Enumeration;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;


/**
 *
 * @author urso
 */
public class TraceGenerator {
    
    
    
    /*
     * Returns a association between replica number to the ordered list of trace operation. 
     */
    public static Map<Integer, List<SequenceOperation>> historyPerReplica(List<SequenceOperation> trace) {
        Map<Integer, List<SequenceOperation>> histories = new HashMap<Integer, List<SequenceOperation>>();
        for (SequenceOperation opt : trace) {
            int r = opt.getReplica();
            List<SequenceOperation> l = histories.get(r);
            if (l==null) {
                l = new ArrayList<SequenceOperation>();
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
    public static SequenceOperation oneXML2OP(Element e) {
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
            return SequenceOperation.insert(r, p, e.getChildText("Text"), v);
        } else {
            return SequenceOperation.delete(r, p, Integer.parseInt(e.getChildText("Offset")), v);
        }
    }
    
    static class XMLTrace implements Trace {

        int docnum;
        Iterator children;
        int size;

        XMLTrace(int docnum, Iterator children) {
            this.docnum = docnum;
            this.children = children;
            this.size = -1;
        }

        public XMLTrace(int docnum, Iterator children, int size) {
            this.docnum = docnum;
            this.children = children;
            this.size = size;
        }

        private static class TraceIterator implements Enumeration<TraceOperation> {

            Iterator children;
            int docnum;
            SequenceOperation next;
            boolean goNext;
            int size;
            int line;

            private TraceIterator(int docnum, Iterator children, int size) {
                this.children = children;
                this.docnum = docnum;
                this.goNext = true;
                this.size = size;
            }

            @Override
            public boolean hasMoreElements() {
                if (line == size) {
                    return false;
                }
                if (!children.hasNext()) {
                    return false;
                }
                if (goNext) {
                    Element e = (Element) children.next();
                    while (children.hasNext() && Integer.parseInt(e.getChildText("NumDocument")) != docnum) {
                        e = (Element) children.next();
                    }
                    if (Integer.parseInt(e.getChildText("NumDocument")) != docnum) {
                        return false;
                    }
                    next = oneXML2OP(e);
                    goNext = false;
                    return true;
                }
                return children.hasNext();
            }

            @Override
            public TraceOperation nextElement() {
                if (goNext && !hasMoreElements()) {
                    throw new NoSuchElementException();
                }
                goNext = true;
                line++;
                return next;
            }
        }

        @Override
        public Enumeration<TraceOperation> enumeration() {
            return new TraceIterator(docnum, children, size);
        }
    }


    
    /**
     *  Extract trace form XML JDOM document
     */
    public static Trace traceFromXML(Document document, int docnum) throws JDOMException, IOException  {
        List trace = document.getRootElement().getChild("Trace").getChildren();
        
        return new XMLTrace(docnum, trace.iterator()); 
    }

    /**
     *  Extract trace form XML JDOM document up to size operations
     */
    public static Trace traceFromXML(Document document, int docnum, int size) throws JDOMException, IOException  {
        List trace = document.getRootElement().getChild("Trace").getChildren();
        
        return new XMLTrace(docnum, trace.iterator(), size); 
    }
    
    
    /**
     * Extract trace form XML uri.
     */
    public static Trace traceFromXML(String uri, int docnum) throws JDOMException, IOException  {        
        return traceFromXML((new SAXBuilder()).build(uri), docnum);
    }

    /**
     * Extract trace form XML uri.
     */
    public static Trace traceFromXML(String uri, int docnum, int size) throws JDOMException, IOException  {        
        return traceFromXML((new SAXBuilder()).build(uri), docnum, size);
    }
    
    
    /**
     * Verifies causality of an operation according to replicas VectorClock.
     */
    public static void causalCheck(SequenceOperation opt, Map<Integer, VectorClock> vcs) throws IncorrectTraceException {
        int r = opt.getReplica();
        if (opt.getVectorClock().getSafe(r) == 0) {
            throw new IncorrectTraceException("Zero/no entry in VC for replica" + opt);
        }
        VectorClock clock = vcs.get(r);
        if (clock.getSafe(r) > opt.getVectorClock().get(r) - 1) {
            throw new IncorrectTraceException("Already seen clock operation " + opt);
        }
        if (clock.getSafe(r) < opt.getVectorClock().getSafe(r) - 1) {
            throw new IncorrectTraceException("Missing operation before " + opt);
        }
        for (int i : opt.getVectorClock().keySet()) {
            if ((i != r) && (opt.getVectorClock().get(i) > 0)) {
                if (vcs.get(i)==null) 
                    throw new IncorrectTraceException("Missing replica " + i + " for " + opt);
                if (vcs.get(i).getSafe(i) < opt.getVectorClock().get(i))
                    throw new IncorrectTraceException("Missing causal operation before " + opt + " on replica " + i);
            }
        }
    }
}
