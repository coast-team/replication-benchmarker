package jbenchmarker.ot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import jbenchmarker.core.Document;
import jbenchmarker.core.MergeAlgorithm;
import jbenchmarker.core.Operation;
import jbenchmarker.core.VectorClock;
import jbenchmarker.trace.IncorrectTrace;
import jbenchmarker.trace.TraceOperation;

/**
 *
 * @author oster
 */
public class SOCT2MergeAlgorithm extends MergeAlgorithm {

    private VectorClock siteVC;
    private SOCT2Log log;
    private OperationGarbageCollector gc;

    public SOCT2MergeAlgorithm(Document doc, int r) {
        super(doc, r);
        this.siteVC = new VectorClock();
        this.log = new SOCT2Log();
        this.gc = new OperationGarbageCollector(this);
    }

    public VectorClock getClock() {
        return this.siteVC;
    }
    
    public SOCT2Log getHistoryLog() {
        return this.log;
    }

    @Override
    protected void integrateLocal(Operation op) {
        TTFOperation oop = (TTFOperation) op;

        if (this.readyFor(oop.getSiteId(), oop.getClock())) {
            this.log.merge(oop);
            this.getDoc().apply(oop);
            this.log.add(oop);
            this.siteVC.inc(oop.getSiteId());
            
            this.gc.collect(oop);
        } else {
            throw new RuntimeException("it seems causal reception is broken");
        }
    }

    @Override
    protected List<Operation> generateLocal(TraceOperation opt) throws IncorrectTrace {
        TTFDocument doc = (TTFDocument) this.getDoc();
        List<Operation> generatedOperations = new ArrayList<Operation>();

        int mpos = doc.viewToModel(opt.getPosition());
        if (opt.getType() == TraceOperation.OpType.del) {
            for (int i = 0; i < opt.getOffset(); i++) {
                TTFOperation op = TTFOperation.delete(opt, mpos + i, new VectorClock(this.siteVC));
                this.siteVC.inc(this.getReplicaNb());
                generatedOperations.add(op);
                this.log.add(op);
                doc.apply(op);
            }
        } else {
            for (int i = 0; i < opt.getContent().length(); i++) {
                TTFOperation op = TTFOperation.insert(opt, mpos + i, opt.getContent().charAt(i), new VectorClock(this.siteVC));
                this.siteVC.inc(this.getReplicaNb());
                generatedOperations.add(op);
                this.log.add(op);
                doc.apply(op);
            }
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
}
