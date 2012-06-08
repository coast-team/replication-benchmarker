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
package jbenchmarker.ot;

import crdt.CRDT;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import jbenchmarker.core.Document;
import jbenchmarker.core.MergeAlgorithm;
import jbenchmarker.core.SequenceMessage;
import collect.VectorClock;
import crdt.simulator.IncorrectTraceException;
import jbenchmarker.core.*;

/**
 *
 * @author oster
 */
public class SOCT2MergeAlgorithm extends MergeAlgorithm {

    private VectorClock siteVC;
    private SOCT2Log log;

    public SOCT2MergeAlgorithm(Document doc, int r) {
        super(doc, r);
        this.siteVC = new VectorClock();
        this.log = new SOCT2Log(new TTFTransformations());
    }

    public VectorClock getClock() {
        return this.siteVC;
    }
    
    public SOCT2Log getHistoryLog() {
        return this.log;
    }

    @Override
    protected void integrateLocal(SequenceMessage op) {
        if (! (op instanceof TTFOperation)) {
            return ;
        }
        
        TTFOperation oop = (TTFOperation) op;

        if (this.readyFor(oop.getSiteId(), oop.getClock())) {
            this.log.merge(oop);
            this.getDoc().apply(oop);
            this.log.add(oop);
            this.siteVC.inc(oop.getSiteId());
            
        } else {
            throw new RuntimeException("it seems causal reception is broken");
        }
    }

    @Override
    protected List<SequenceMessage> generateLocal(SequenceOperation opt) throws IncorrectTraceException {
        TTFDocument doc = (TTFDocument) this.getDoc();
        List<SequenceMessage> generatedOperations = new ArrayList<SequenceMessage>();

        int mpos = doc.viewToModel(opt.getPosition());
        if (opt.getType() == SequenceOperation.OpType.del) {
            int visibleIndex = 0;
            for (int i = 0; i < opt.getOffset(); i++) {
                // TODO: could be improved with an iterator on only visible characters
                while (! doc.getChar(mpos+visibleIndex).isVisible()) {
                    visibleIndex++;
                }
                TTFOperation op = TTFOperation.delete(opt, mpos + visibleIndex, new VectorClock(this.siteVC));
                this.siteVC.inc(this.getReplicaNumber());
                generatedOperations.add(op);
                this.log.add(op);
                doc.apply(op);
            }
        } else if (opt.getType() == SequenceOperation.OpType.ins) {
            for (int i = 0; i < opt.getContent().size(); i++) {
                TTFOperation op = TTFOperation.insert(opt, mpos + i, opt.getContent().get(i), new VectorClock(this.siteVC));
                this.siteVC.inc(this.getReplicaNumber());
                generatedOperations.add(op);
                this.log.add(op);
                doc.apply(op);
            }
        }else if (opt.getType() == SequenceOperation.OpType.up) {
            
            int visibleIndex = 0;
            for (int i = 0; i < opt.getOffset(); i++) {
                // TODO: could be improved with an iterator on only visible characters
                while (! doc.getChar(mpos+visibleIndex).isVisible()) {
                    visibleIndex++;
                }
                TTFOperation op = TTFOperation.delete(opt, mpos + visibleIndex, new VectorClock(this.siteVC));
                this.siteVC.inc(this.getReplicaNumber());
                generatedOperations.add(op);
                this.log.add(op);
                doc.apply(op);
            }
            for (int i = 0; i < opt.getContent().size(); i++) {
                TTFOperation op = TTFOperation.insert(opt, mpos + i, opt.getContent().get(i), new VectorClock(this.siteVC));
                this.siteVC.inc(this.getReplicaNumber());
                generatedOperations.add(op);
                this.log.add(op);
                doc.apply(op);
            }
        }else if (opt.getType() == SequenceOperation.OpType.unsupported) {
                UnsupportedOperation op = UnsupportedOperation.create(opt, new VectorClock(this.siteVC));
                //this.siteVC.inc(this.getReplicaNumber());
                generatedOperations.add(op);
                //this.log.add(op);
                //doc.apply(op);                       
        }

        return generatedOperations;
    }

    public boolean readyFor(int r, VectorClock op) {
        if (this.siteVC.getSafe(r) != op.getSafe(r)) {
            return false;
        }
        for (Map.Entry<Integer, Integer> e : op.entrySet()) {
            if ((e.getKey() != r) && (this.siteVC.getSafe(e.getKey()) < e.getValue())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public CRDT<String> create() {
        return new SOCT2MergeAlgorithm(new TTFDocument(), 0);
    }
}
