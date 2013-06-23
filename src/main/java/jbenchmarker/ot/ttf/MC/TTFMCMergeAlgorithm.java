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
package jbenchmarker.ot.ttf.MC;

import jbenchmarker.ot.ttf.*;
import collect.VectorClock;
import crdt.CRDT;
import crdt.Factory;
import crdt.Operation;
import crdt.simulator.IncorrectTraceException;
import java.util.ArrayList;
import java.util.List;
import jbenchmarker.core.*;
import jbenchmarker.ot.soct2.OTAlgorithm;
import jbenchmarker.ot.soct2.OTMessage;
import jbenchmarker.ot.soct2.OTReplica;
import jbenchmarker.ot.soct2.SOCT2;
import jbenchmarker.ot.soct2.SOCT2TranformationInterface;

/**
 * This TTF Merge Algorithm uses SOCT2 algorithm with TTF method
 *
 * @author Mehdi
 */
public class TTFMCMergeAlgorithm extends TTFMergeAlgorithm {

    /**
     * Make new TTFMerge algorithm with docuement (TTFDocument) and site id or
     * replicat id.
     *
     * @param doc TTF Document
     * @param siteId SiteID
     */
    public TTFMCMergeAlgorithm(TTFMCDocument doc, int siteId, Factory<OTAlgorithm<TTFOperation>> otAlgo) {
        super(doc, siteId, otAlgo);
    }
    
    public TTFMCMergeAlgorithm(int siteId) {
        this(new TTFMCDocument(), siteId, new SOCT2<TTFOperation>(new TTFMCTransformations(), siteId, null));
    }

    public TTFMCMergeAlgorithm(Factory<OTAlgorithm<TTFOperation>> otAlgo) {
        this(new TTFMCDocument(), 0, otAlgo);
    }

    @Override
    public TTFMCDocument getDoc() {
        return (TTFMCDocument) super.getDoc();
    }


    @Override
    protected TTFOperation deleteOperation(int pos) {
        return new TTFOperation(SequenceOperation.OpType.delete, pos, getReplicaNumber());
    }
    
    @Override
    protected TTFOperation insertOperation(int pos, Object content) {
        return new TTFOperation(SequenceOperation.OpType.insert, pos, content, getReplicaNumber());
    }
    
    /**
     * Make a new mergeAlgorithm with 0 as site id.
     *
     * @return new TTFMergeAlgorithm
     */
    @Override
    public CRDT<String> create() {
        return new TTFMCMergeAlgorithm(new TTFMCDocument(), 0, getOtAlgo());
    }
}
