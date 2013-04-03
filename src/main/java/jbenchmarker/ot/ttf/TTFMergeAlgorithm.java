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
package jbenchmarker.ot.ttf;

import collect.VectorClock;
import crdt.CRDT;
import crdt.CRDTMessage;
import crdt.Factory;
import crdt.OperationBasedOneMessage;
import crdt.simulator.IncorrectTraceException;
import java.util.ArrayList;
import java.util.List;
import jbenchmarker.core.*;
import jbenchmarker.ot.soct2.OTAlgorithm;
import jbenchmarker.ot.soct2.OTMessage;
import jbenchmarker.ot.soct2.SOCT2;

/**
 * This TTF Merge Algorithm uses SOCT2 algorithm with TTF method
 *
 * @author oster
 */
public class TTFMergeAlgorithm extends MergeAlgorithm {

    final private OTAlgorithm<TTFOperation> otAlgo;
    //private TTFDocument ;

    /**
     * Make new TTFMerge algorithm with docuement (TTFDocument) and site id or
     * replicat id.
     *
     * @param doc TTF Document
     * @param siteId SiteID
     */
    public TTFMergeAlgorithm(Document doc, int siteId, Factory<OTAlgorithm<TTFOperation>> otAlgo) {
        super(doc);
        this.otAlgo = otAlgo.create();
        setReplicaNumber(siteId);
    }
    
    
    public TTFMergeAlgorithm(Document doc, int siteId) {
        this(doc, siteId, new SOCT2<TTFOperation>(new TTFTransformations(), siteId, null));
        setReplicaNumber(siteId);
    }

    public TTFMergeAlgorithm(Document doc) {
        this(doc, 0, new SOCT2<TTFOperation>(new TTFTransformations(), 0, null));
    }

    public TTFMergeAlgorithm(Document doc, Factory<OTAlgorithm<TTFOperation>> otAlgo) {
        this(doc, 0, otAlgo);
    }
    /**
     * @return Vector Clock of site
     */
    public VectorClock getClock() {
        return otAlgo.getSiteVC();
    }

    /*
     * This integrate local modifications and generate message to another
     * replicas
     */
    @Override
    protected List<SequenceMessage> localDelete(SequenceOperation opt) throws IncorrectTraceException {
        TTFDocument doc = (TTFDocument) this.getDoc();
        List<SequenceMessage> generatedOperations = new ArrayList<SequenceMessage>();

        int mpos = doc.viewToModel(opt.getPosition());
        int visibleIndex = 0;
        for (int i = 0; i < opt.getLenghOfADel(); i++) {
            // TODO: could be improved with an iterator on only visible characters
            while (!doc.getChar(mpos + visibleIndex).isVisible()) {
                visibleIndex++;
            }
            TTFOperation op = new TTFOperation(SequenceOperation.OpType.delete, mpos + visibleIndex, getReplicaNumber());
            generatedOperations.add(new TTFSequenceMessage(otAlgo.estampileMessage(op), opt));
            doc.apply(op);
        }
        return generatedOperations;
    }

    @Override
    protected List<SequenceMessage> localInsert(SequenceOperation opt) throws IncorrectTraceException {
        TTFDocument doc = (TTFDocument) this.getDoc();
        List<SequenceMessage> generatedOperations = new ArrayList<SequenceMessage>();

        int mpos = doc.viewToModel(opt.getPosition());
        for (int i = 0; i < opt.getContent().size(); i++) {
            TTFOperation op = new TTFOperation(SequenceOperation.OpType.insert,
                    mpos + i,
                    opt.getContent().get(i),
                    getReplicaNumber());
            generatedOperations.add(new TTFSequenceMessage(otAlgo.estampileMessage(op), opt));
            doc.apply(op);
        }
        return generatedOperations;
    }

//    @Override
//    public List<SequenceMessage> generateLocal(SequenceOperation opt) throws IncorrectTraceException {
//        TTFDocument doc = (TTFDocument) this.getDoc();
//        List<SequenceMessage> generatedOperations = new ArrayList<SequenceMessage>();
//
//        int mpos = doc.viewToModel(opt.getPosition());
//        switch (opt.getType()) {
//            case del:
//                int visibleIndex = 0;
//                for (int i = 0; i < opt.getLenghOfADel(); i++) {
//                    // TODO: could be improved with an iterator on only visible characters
//                    while (!doc.getChar(mpos + visibleIndex).isVisible()) {
//                        visibleIndex++;
//                    }
//                    TTFOperation op = new TTFOperation(SequenceOperation.OpType.del, mpos + visibleIndex, getReplicaNumber());
//                    generatedOperations.add(new TTFSequenceMessage(otAlgo.estampileMessage(op), opt));
//                    doc.apply(op);
//                }
//                break;
//            case ins:
//                for (int i = 0; i < opt.getContent().size(); i++) {
//                    TTFOperation op = new TTFOperation(SequenceOperation.OpType.ins,
//                            mpos + i,
//                            opt.getContent().get(i),
//                            getReplicaNumber());
//                    generatedOperations.add(new TTFSequenceMessage(otAlgo.estampileMessage(op), opt));
//                    doc.apply(op);
//                }
//                break;
//            case unsupported:
//                UnsupportedOperation op = UnsupportedOperation.create(opt);
//                //this.siteVC.inc(this.getReplicaNumber());
//                generatedOperations.add(op);
//                break;
//            case update:
//                break;
//        }
//        return generatedOperations;
//    }

    /**
     * Make a new mergeAlgorithm with 0 as site id.
     *
     * @return new TTFMergeAlgorithm
     */
    @Override
    public CRDT<String> create() {
        return new TTFMergeAlgorithm(new TTFDocument(), otAlgo);
    }

    /*
     * IntegrateRemote Operation
     */
    @Override
    public void integrateRemote(SequenceMessage mess) throws IncorrectTraceException {
        integrateOneRemoteOperation(((TTFSequenceMessage) mess).getSoct2Message());
    }


    private void integrateOneRemoteOperation(OTMessage mess) {
        Operation op = otAlgo.integrateRemote(mess);
        this.getDoc().apply(op);
    }

    @Override
    public void setReplicaNumber(int replicaNumber) {
        super.setReplicaNumber(replicaNumber);
        otAlgo.setReplicaNumber(replicaNumber);
    }
}
