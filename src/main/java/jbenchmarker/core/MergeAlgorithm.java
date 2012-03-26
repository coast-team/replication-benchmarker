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

    private static final boolean DEBUG = false;
    
    // Supported Document
    final private Document doc;
       
    final private List<String> states  = new ArrayList<String>(1);  
    
    
    /*
     * Constructor
     */
    public MergeAlgorithm(Document doc, int r) {
        this.setReplicaNumber(r);
        this.doc = doc;
    }

    /**
     * To be define by the concrete merge algorithm
     */
    protected abstract void integrateLocal(SequenceMessage op) throws IncorrectTraceException;
    
    /**
     * To be define by the concrete merge algorithm
     */
    protected abstract List<SequenceMessage> generateLocal(SequenceOperation opt) throws IncorrectTraceException;
        
    public Document getDoc() {
        return doc;
    }

    public void reset() {
    }

    @Override
    public CRDTMessage applyLocal(crdt.Operation op) throws PreconditionException {
        List<SequenceMessage> l = generateLocal((SequenceOperation) op);
        SequenceMessage m = null;
        for (SequenceMessage n : l) {
            if (m == null) { 
                m = n;
            } else {
                m.concat(n);
            }
        }        
        if (DEBUG) states.add(doc.view());

        return m;
    }

    @Override
    public void applyRemote(CRDTMessage msg) {
        try {
            SequenceMessage s = (SequenceMessage) msg;
            integrateLocal(s);    
            for (Object m : s.getMsgs()) {
                integrateLocal((SequenceMessage) m);
            }
        } catch (IncorrectTraceException ex) {
            throw new IllegalStateException(ex);
        }
        
        if (DEBUG) states.add(doc.view());
    }

    @Override
    public String lookup() {
        return doc.view();
    }
}
