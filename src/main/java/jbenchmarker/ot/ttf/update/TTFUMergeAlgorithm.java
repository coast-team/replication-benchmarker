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
package jbenchmarker.ot.ttf.update;

import jbenchmarker.ot.ttf.*;
import crdt.CRDT;
import crdt.Factory;
import crdt.simulator.IncorrectTraceException;
import java.util.ArrayList;
import java.util.List;
import jbenchmarker.core.SequenceMessage;
import jbenchmarker.core.SequenceOperation;
import jbenchmarker.core.SequenceOperation.OpType;
import jbenchmarker.ot.soct2.OTAlgorithm;
import jbenchmarker.ot.soct2.SOCT2;

/**
 * This TTF Merge Algorithm uses SOCT2 algorithm with TTF method
 *
 * @author urso
 */
public class TTFUMergeAlgorithm extends TTFMergeAlgorithm {

    /**
     * Make new TTFMerge algorithm with docuement (TTFDocument) and site id or
     * replicat id.
     *
     * @param doc TTF Document
     * @param siteId SiteID
     */
    public TTFUMergeAlgorithm(TTFUDocument doc, int siteId, Factory<OTAlgorithm<TTFOperation>> otAlgo) {
        super(doc, siteId, otAlgo);
    }
    
    public TTFUMergeAlgorithm(int siteId) {
        this(new TTFUDocument(), siteId, new SOCT2<TTFOperation>(new TTFUTransformations(), siteId, null));
    }

    public TTFUMergeAlgorithm(Factory<OTAlgorithm<TTFOperation>> otAlgo) {
        this(new TTFUDocument(), 0, otAlgo);
    }

    @Override
    public TTFUDocument getDoc() {
        return (TTFUDocument) super.getDoc();
    }

    @Override
    protected TTFOperation deleteOperation(int pos) {
        return new TTFOperation(OpType.update, pos, null, getReplicaNumber());
    }

    private TTFOperation updateOperation(int pos, Object value) {
        return new TTFOperation(OpType.update, pos, value, getReplicaNumber());
    }
    
    @Override
    protected List<SequenceMessage> localReplace(SequenceOperation opt) throws IncorrectTraceException {
        List<SequenceMessage> generatedOperations = new ArrayList<SequenceMessage>();

        int mpos = getDoc().viewToModel(opt.getPosition());
        int i = 0;
        while (i < opt.getLenghOfADel()) {
            while (!getDoc().getChar(mpos).isVisible()) {
                ++mpos;
            }
            TTFOperation op = updateOperation(mpos, i < opt.getContent().size() ? opt.getContent().get(i) : null);
            generatedOperations.add(new TTFSequenceMessage(getOtAlgo().estampileMessage(op), opt));
            getDoc().apply(op);
            ++i; 
            ++mpos;
        }
        while (i < opt.getContent().size()) {
            TTFOperation op = insertOperation(mpos, opt.getContent().get(i));
            generatedOperations.add(new TTFSequenceMessage(getOtAlgo().estampileMessage(op), opt));
            getDoc().apply(op);
            ++i;            
            ++mpos;
        }
        return generatedOperations;
    }

    /**
     * Make a new mergeAlgorithm with 0 as site id.
     *
     * @return new TTFMergeAlgorithm
     */
    @Override
    public CRDT<String> create() {
        return new TTFUMergeAlgorithm(new TTFUDocument(), 0, getOtAlgo());
    }
}
