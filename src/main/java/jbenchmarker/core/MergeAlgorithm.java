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
package jbenchmarker.core;

import crdt.CRDT;
import crdt.CRDTMessage;
import crdt.PreconditionException;
import crdt.simulator.IncorrectTraceException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author urso
 */
public abstract class MergeAlgorithm extends CRDT<String> implements Serializable{
    
    // Supported Document
    final private Document doc;
   
    // Local operations generated
    final private List<SequenceOperation> geneHistory; 

    // All operations executed
    final private List<SequenceMessage> history;
    
    final private List<String> states;  
    
    
    // Local operations generated time
    final private List<Long> geneExecTime;
    
    // All operations executed  time 
    final private List<Long> execTime;
    
    /*
     * Constructor
     */
    public MergeAlgorithm(Document doc, int r) {
        this.execTime = new ArrayList<Long>();
        this.geneExecTime = new ArrayList<Long>();
        this.history = new ArrayList<SequenceMessage>();
        this.geneHistory = new ArrayList<SequenceOperation>();
        this.setReplicaNumber(r);
        this.doc = doc;
        
        this.states = new ArrayList<String>();
    }

    /**
     * To be define by the concrete merge algorithm
     */
    protected abstract void integrateLocal(SequenceMessage op) throws IncorrectTraceException;
    
    /**
     * To be define by the concrete merge algorithm
     */
    protected abstract List<SequenceMessage> generateLocal(SequenceOperation opt) throws IncorrectTraceException;
    
   
    
    /*
     * Integration of a remote operation
     * Adds operation in history and execution time
     */
    @Deprecated
    public void integrate(SequenceMessage op) throws IncorrectTraceException {
        history.add(op);
        long startTime = System.nanoTime();
        integrateLocal(op); 
        execTime.add(System.nanoTime() - startTime);
        
        states.add(doc.view());
    }
    
    /**
     *  Generation of a local trace operation, returns a patch of operation
     * Throws IncorrectTraceException iff operation is not generable in the context.
     **/    
    @Deprecated
    public List<SequenceMessage> generate(SequenceOperation opt) throws IncorrectTraceException {
        geneHistory.add(opt);
        long startTime = System.nanoTime();
        List<SequenceMessage> l = generateLocal(opt);
        long t = System.nanoTime() - startTime, oh = t/l.size();
        int i = history.size();
        geneExecTime.add(t);
        for (SequenceMessage o : l) {
            execTime.add(oh);
            history.add(o);
            i++;
        }        
        
        states.add(doc.view());
        
        return l;
    }

    public List<Long> getExecTime() {
        return execTime;
    }

    public List<SequenceMessage> getHistory() {
        return history;
    }

    public List<Long> getLocalExecTime() {
        return geneExecTime;
    }

    public List<SequenceOperation> getLocalHistory() {
        return geneHistory;
    }
    
    public Document getDoc() {
        return doc;
    }

    public void reset() {
        this.execTime.clear();
        this.geneExecTime.clear();
        this.getHistory().clear();
        this.geneHistory.clear();       
    }

    @Override
    public Long lastExecTime() {
        return this.geneExecTime.get(this.geneExecTime.size()-1);
    }

    @Override
    public CRDTMessage applyLocal(crdt.Operation op) throws PreconditionException {
        List<SequenceMessage> l = generateLocal((SequenceOperation) op);
        SequenceMessage m = null;
        for (SequenceMessage n : l) {
            history.add(n);
            if (m == null) { 
                m = n;
            } else {
                m.concat(n);
            }
        }
        
        states.add(doc.view());

        return m;
    }

    @Override
    public void applyRemote(CRDTMessage msg) {
        try {
            SequenceMessage s = (SequenceMessage) msg;
            history.add(s);
            integrateLocal(s);    
            for (Object m : s.getMsgs()) {
                integrateLocal((SequenceMessage) m);
            }
        } catch (IncorrectTraceException ex) {
            throw new IllegalStateException(ex);
        }
        
        states.add(doc.view());
    }

    @Override
    public String lookup() {
        return doc.view();
    }
}
