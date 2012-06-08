/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker.ot.soct2;

import collect.VectorClock;
import java.util.Map;
import jbenchmarker.core.Document;
import jbenchmarker.core.Operation;

/**
 *
 * @author Stephane Martin
 */
public class SOCT2 <O extends Operation> {

    private VectorClock siteVC;
    private SOCT2Log<O> log;
    private Document doc;
    private int siteId;

    public Document getDoc() {
        return doc;
    }

    public SOCT2(Document doc,SOCT2TranformationInterface ot, int siteId) {
        this.siteVC = new VectorClock();
        this.log = new SOCT2Log(ot);
        this.doc=doc;
        this.siteId = siteId;
    }

    public VectorClock getSiteVC() {
        return siteVC;
    }

    public SOCT2Log getLog() {
        return log;
    }

    public int getSiteId() {
        return siteId;
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

    public SOCT2Message estampileMessage(O op) {
        SOCT2Message ret = new SOCT2Message(new VectorClock(siteVC), siteId, op);
        this.siteVC.inc(this.siteId);
        this.log.add(ret);
        doc.apply((Operation)op);

        return ret;
    }

    public void integrateRemote(SOCT2Message op) {

        if (this.readyFor(op.getSiteId(), op.getClock())) {
            this.log.merge(op);
            this.getDoc().apply((Operation) op.getOperation());
            this.log.add(op);
            this.siteVC.inc(op.getSiteId());

        } else {
            throw new RuntimeException("it seems causal reception is broken");
        }
    }
}
