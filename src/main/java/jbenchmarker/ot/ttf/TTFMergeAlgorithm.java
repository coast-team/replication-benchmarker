/**
 * Replication Benchmarker
 * https://github.com/score-team/replication-benchmarker/ Copyright (C) 2011
 * INRIA / LORIA / SCORE Team
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package jbenchmarker.ot.ttf;

import collect.VectorClock;
import crdt.CRDT;
import crdt.simulator.IncorrectTraceException;
import java.util.ArrayList;
import java.util.List;
import jbenchmarker.core.*;
import jbenchmarker.ot.soct2.SOCT2;
import jbenchmarker.ot.soct2.SOCT2Log;

/**
 *
 * @author oster
 */
public class TTFMergeAlgorithm extends MergeAlgorithm {

    private SOCT2<TTFOperation> soct2;

    public TTFMergeAlgorithm(Document doc, int siteId) {
        super(doc, siteId);
        soct2 = new SOCT2<TTFOperation>(doc,new TTFTransformations(), siteId);

    }

    public VectorClock getClock() {
        return soct2.getSiteVC();
    }

    public SOCT2Log getHistoryLog() {
        return soct2.getLog();
    }

    /*
     *
     */
    @Override
    public List<SequenceMessage> generateLocal(SequenceOperation opt) throws IncorrectTraceException {
        TTFDocument doc = (TTFDocument) this.getDoc();
        List<SequenceMessage> generatedOperations = new ArrayList<SequenceMessage>();

        int mpos = doc.viewToModel(opt.getPosition());
        switch (opt.getType()) {
            case del:
                int visibleIndex = 0;
                for (int i = 0; i < opt.getNumberOf(); i++) {
                    // TODO: could be improved with an iterator on only visible characters
                    while (!doc.getChar(mpos + visibleIndex).isVisible()) {
                        visibleIndex++;
                    }
                    TTFOperation op = new TTFOperation(SequenceOperation.OpType.del, mpos + visibleIndex, soct2.getSiteId());

                    generatedOperations.add(new TTFSequenceMessage(soct2.estampileMessage(op), opt));
                }
                break;
            case ins:
                for (int i = 0; i < opt.getContent().size(); i++) {
                    TTFOperation op = new TTFOperation(SequenceOperation.OpType.ins,
                            mpos + i,
                            opt.getContent().get(i),
                            soct2.getSiteId());

                    generatedOperations.add(new TTFSequenceMessage(soct2.estampileMessage(op), opt));
                    
                }
                break;
            case unsupported:
                UnsupportedOperation op = UnsupportedOperation.create(opt);
                //this.siteVC.inc(this.getReplicaNumber());
                generatedOperations.add(op);
                break;

            case up:
                
                break;

        }

        return generatedOperations;
    }

    @Override
    public CRDT<String> create() {
        return new TTFMergeAlgorithm(new TTFDocument(), 0);
    }

    @Override
    public void integrateLocal(SequenceMessage op) throws IncorrectTraceException {
        soct2.integrateRemote(((TTFSequenceMessage) op).getSoct2Message());
    }
}
