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
package jbenchmarker.trace;

import collect.VectorClock;
import crdt.simulator.IncorrectTraceException;
import crdt.simulator.Trace;
import crdt.simulator.TraceOperation;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import jbenchmarker.core.SequenceOperation;
import jbenchmarker.trace.json.ElementJSON;
import jbenchmarker.trace.json.JSONTrace;
import jbenchmarker.trace.json.VectorClockCS;
import jbenchmarker.trace.json.VectorClockCSMapper;
import jbenchmarker.trace.json.moulinette.DocumentJSON;
import jbenchmarker.trace.json.moulinette.attributs.ElementModif;
import jbenchmarker.trace.json.moulinette.attributs.TypeModif;
import jbenchmarker.trace.json.moulinette.attributs.VectorClockMapper;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import crdt.simulator.TraceOperationImpl;

/**
 *
 * @author urso
 */
public class TraceGenerator {

    /*
     * Returns a association between replica number to the ordered list of trace
     * operation.
     */
    public static Map<Integer, List<TraceOperation>> historyPerReplica(List<TraceOperation> trace) {
        Map<Integer, List<TraceOperation>> histories = new HashMap<Integer, List<TraceOperation>>();
        for (TraceOperation opt : trace) {
            int r = opt.getReplica();
            List<TraceOperation> l = histories.get(r);
            if (l == null) {
                l = new ArrayList<TraceOperation>();
                histories.put(r, l);
            }
            l.add(opt);
        }
        return histories;
    }

    /**
     * Parses one element into a trace operation
     *
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
            return new TraceOperationImpl(SequenceOperation.insert(p, e.getChildText("Text")), r, v);
        } else {
            return new TraceOperationImpl(SequenceOperation.delete(p, Integer.parseInt(e.getChildText("Offset"))), r, v);
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
            TraceOperation next;
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
     * Parses one elmeent into a trace operation
     *
     * @param e JSON Element
     * @return the parsed Trace operation
     */
    public static TraceOperationImpl oneJSON2OP(ElementJSON e, VectorClockCSMapper vectorClockMapper) {
        int pos = e.getVal().getPosition();
        String ui = e.getVal().getUserId();
        int repli = vectorClockMapper.userId(ui);
        VectorClockCS vc = e.getVal().getVector_clock();
        VectorClock v = vectorClockMapper.toVectorClock(vc);

        if (e.getVal().getOperation().equals("insertion")) {
            return new TraceOperationImpl(SequenceOperation.insert( pos, e.getVal().getChars_inserted()),repli, v);
        } else if (e.getVal().getOperation().equals("suppression")) {
            return new TraceOperationImpl(SequenceOperation.delete( pos, e.getVal().getNumber_charDeleted()),repli, v);
        } else if (e.getVal().getOperation().equals("remplacement")) {
            return new TraceOperationImpl(SequenceOperation.update( pos, e.getVal().getNumber_charDeleted(), e.getVal().getChars_inserted()),repli, v);
        } else {//stylage
            return  new TraceOperationImpl(SequenceOperation.unsupported(),repli, v);
        }
    }

    /**
     * Parses one JSON elmeent from a CouchBase DataBase into a trace operation
     * TO DO : variable pos is wrong. It must be the
     *
     * @return the parsed Trace operation
     */
    public static TraceOperation oneJSONDB2OP(int rep, DocumentJSON d, ElementModif e, VectorClockMapper vectorClockMapper) {
        int pos = d.get(e);

        int repli = rep;

        //clone VectorClock : TO TEST
        VectorClock v = new VectorClock(vectorClockMapper.get(rep));

        v.inc(rep);
        //v is now the last vectorClock of replica rep
        vectorClockMapper.put(rep, v);

        if (e.getType() == TypeModif.addText) {
            return new TraceOperationImpl(SequenceOperation.insert( pos, e.getLigne()),repli, v);
        } else if (e.getType() == TypeModif.delText) {
            //when a line deleted, offset is the length of the line deleted
            return new TraceOperationImpl(SequenceOperation.delete( pos, e.getLigne().length()),repli, v);
        } else {
            return new TraceOperationImpl(SequenceOperation.unsupported(),repli, v);
        }
    }

    /**
     * Extract trace form JSON document
     */
    public static Trace traceFromJson(String nomFichier) throws FileNotFoundException, IOException {
        return new JSONTrace(nomFichier);
    }

    public static Trace traceFromJson(String nomFichier, String padid) throws FileNotFoundException, IOException {
        return new JSONTrace(nomFichier, padid);
    }

    /**
     * Extract trace form XML JDOM document
     */
    public static Trace traceFromXML(Document document, int docnum) throws JDOMException, IOException {
        List trace = document.getRootElement().getChild("Trace").getChildren();

        return new XMLTrace(docnum, trace.iterator());
    }

    /**
     * Extract trace form XML JDOM document up to size operations
     */
    public static Trace traceFromXML(Document document, int docnum, int size) throws JDOMException, IOException {
        List trace = document.getRootElement().getChild("Trace").getChildren();

        return new XMLTrace(docnum, trace.iterator(), size);
    }

    /**
     * Extract trace form XML uri.
     */
    public static Trace traceFromXML(String uri, int docnum) throws JDOMException, IOException {
        return traceFromXML((new SAXBuilder()).build(uri), docnum);
    }

    /**
     * Extract trace form XML uri.
     */
    public static Trace traceFromXML(String uri, int docnum, int size) throws JDOMException, IOException {
        return traceFromXML((new SAXBuilder()).build(uri), docnum, size);
    }

    /**
     * Verifies causality of an operation according to replicas VectorClock.
     */
    public static void causalCheck(TraceOperation opt, Map<Integer, VectorClock> vcs) throws IncorrectTraceException {
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
                if (vcs.get(i) == null) {
                    throw new IncorrectTraceException("Missing replica " + i + " for " + opt);
                }
                if (vcs.get(i).getSafe(i) < opt.getVectorClock().get(i)) {
                    throw new IncorrectTraceException("Missing causal operation before " + opt + " on replica " + i);
                }
            }
        }
    }
}
