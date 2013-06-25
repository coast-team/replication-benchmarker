/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker.ot.ttf.MC;

import crdt.Factory;
import jbenchmarker.ot.soct2.OTAlgorithm;
import jbenchmarker.ot.soct2.SOCT2;
import jbenchmarker.ot.ttf.TTFMergeAlgorithm;
import jbenchmarker.ot.ttf.TTFOperation;

/**
 *
 * @author score
 */
public class TTFMCMergeAlgorithm extends TTFMergeAlgorithm{
    
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
    
}
