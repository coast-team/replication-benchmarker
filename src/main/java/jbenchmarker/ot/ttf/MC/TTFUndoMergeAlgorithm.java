/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker.ot.ttf.MC;

import crdt.Factory;
import jbenchmarker.ot.soct2.OTAlgorithm;
import jbenchmarker.ot.ttf.TTFMergeAlgorithm;
import jbenchmarker.ot.ttf.TTFOperation;

/**
 *
 * @author score
 */
public class TTFUndoMergeAlgorithm extends TTFMCMergeAlgorithm{
    
    public TTFUndoMergeAlgorithm(TTFMCDocument doc, int siteId, Factory<OTAlgorithm<TTFOperation>> otAlgo){
        super(doc, siteId, otAlgo);
    }
    
    @Override
    public TTFOperation deleteOperation(int pos) {
        return new TTFUndoOperation(pos, getReplicaNumber(), 
                -((TTFUndoVisibilityChar) this.getDoc().getModel().get(pos)).getVisibility() );
    }

    @Override
    protected TTFOperation insertOperation(int pos, Object content) {
        TTFUndoVisibilityChar o = (TTFUndoVisibilityChar) this.getDoc().getModel().get(pos);
        if(o.getChar().equals(content) && !o.isVisible()){
        return new TTFUndoOperation(pos, getReplicaNumber(), 
                -((TTFUndoVisibilityChar) this.getDoc().getModel().get(pos)).getVisibility()+1 );
        }
        return super.insertOperation(pos, content);
    }
}
