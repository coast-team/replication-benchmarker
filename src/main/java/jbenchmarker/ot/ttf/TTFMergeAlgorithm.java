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
import crdt.CRDTMessage;
import crdt.simulator.IncorrectTraceException;
import java.util.ArrayList;
import java.util.List;
import jbenchmarker.core.*;
import jbenchmarker.ot.soct2.SOCT2;
import jbenchmarker.ot.soct2.SOCT2Log;
import jbenchmarker.ot.soct2.OTMessage;

/**
 * This TTF Merge Algorithm uses SOCT2 algorithm with TTF method
 * @author oster
 */
public class TTFMergeAlgorithm extends MergeAlgorithm {

    private SOCT2<TTFOperation> soct2;
    //private TTFDocument ;
    /**
     * Make new TTFMerge algorithm with docuement (TTFDocuement) and site id or replicat id.
     * @param doc TTF Document
     * @param siteId SiteID
     */
    public TTFMergeAlgorithm(Document doc, int siteId) {
        super(doc, siteId);
        soct2 = new SOCT2<TTFOperation>(new TTFTransformations(), siteId, null);

    }

    /**
     * @return Vector Clock of site
     */
    public VectorClock getClock() {
        return soct2.getSiteVC();
    }

    
    public CRDTMessage generateLocalCRDT(SequenceOperation opt){
        TTFDocument doc = (TTFDocument) this.getDoc();
        List<SequenceMessage> generatedOperations = new ArrayList<SequenceMessage>();
        CRDTMessage ret=null;
        int mpos = doc.viewToModel(opt.getPosition());
        switch (opt.getType()) {
            case del:
                int visibleIndex = 0;
                for (int i = 0; i < opt.getNumberOf(); i++) {
                    // TODO: could be improved with an iterator on only visible characters
                    while (!doc.getChar(mpos + visibleIndex).isVisible()) {
                        visibleIndex++;
                    }
                    TTFOperation op = new TTFOperation(SequenceOperation.OpType.del, mpos + visibleIndex, soct2.getReplicaNumber());

                    if (ret==null){
                        ret=soct2.estampileMessage(op);
                    }else{
                        ret.concat(soct2.estampileMessage(op));
                    }
                        
                    doc.apply(op);
                }
                break;
            case ins:
                for (int i = 0; i < opt.getContent().size(); i++) {
                    TTFOperation op = new TTFOperation(SequenceOperation.OpType.ins,
                            mpos + i,
                            opt.getContent().get(i),
                            soct2.getReplicaNumber());

                    if (ret==null){
                        ret=soct2.estampileMessage(op);
                    }else{
                        ret.concat(soct2.estampileMessage(op));
                    }
                    doc.apply(op);
                    
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

        return ret;
    }
    
    /*
     *This integrate local modifications and generate message to another replicas
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
                    TTFOperation op = new TTFOperation(SequenceOperation.OpType.del, mpos + visibleIndex, soct2.getReplicaNumber());

                    generatedOperations.add(new TTFSequenceMessage(soct2.estampileMessage(op), opt));
                    doc.apply(op);
                }
                break;
            case ins:
                for (int i = 0; i < opt.getContent().size(); i++) {
                    TTFOperation op = new TTFOperation(SequenceOperation.OpType.ins,
                            mpos + i,
                            opt.getContent().get(i),
                            soct2.getReplicaNumber());

                    generatedOperations.add(new TTFSequenceMessage(soct2.estampileMessage(op), opt));
                    doc.apply(op);
                    
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

    /**
     * Make a new mergeAlgorithm with 0 as site id.
     * @return new TTFMergeAlgorithm
     */
    @Override
    public CRDT<String> create() {
        return new TTFMergeAlgorithm(new TTFDocument(), 0);
    }

    /*
     * IntegrateRemote Operation
     */
    @Override
    public void integrateRemote(SequenceMessage mess) throws IncorrectTraceException {
        integrateOneRemoteOperation(((TTFSequenceMessage) mess).getSoct2Message());
    }
    public void integrateRemote(CRDTMessage mess) throws IncorrectTraceException {
        OTMessage soctMess= (OTMessage)mess;
        integrateOneRemoteOperation(soctMess);
        for(Object m:soctMess.getMsgs()){
            integrateOneRemoteOperation((OTMessage)m);
        }
        
    }
    private void integrateOneRemoteOperation(OTMessage mess){
        Operation op=soct2.integrateRemote(mess);
        this.getDoc().apply(op);
    }
}
