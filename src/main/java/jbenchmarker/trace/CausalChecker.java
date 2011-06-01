package jbenchmarker.trace;

import java.util.ArrayList;
import java.util.List;
import jbenchmarker.core.Document;
import jbenchmarker.core.MergeAlgorithm;
import jbenchmarker.core.Operation;
import jbenchmarker.core.VectorClock;

/**
 * Check that operation are received in causal order.
 * @author urso
 */
public class CausalChecker extends MergeAlgorithm {

    public CausalChecker(int r) {
        super(new Document() {
            public String view() { return ""; }
            public void apply(Operation op) { }
        }, r);
    }
    
    private VectorClock vc = new VectorClock();
    
    
    @Override
    protected void integrateLocal(Operation op) throws IncorrectTrace {
        check(op.getOriginalOp());
        this.getDoc().apply(op);
        vc.inc(op.getOriginalOp().getReplica());
    }

    @Override
    protected List<Operation> generateLocal(TraceOperation opt) throws IncorrectTrace {
        check(opt);
        List<Operation> l = new ArrayList<Operation>(1);
        Operation op = new Operation(opt) {
            public Operation clone() {
                return this;
            }
        };
        l.add(op);
        this.getDoc().apply(op);
        vc.inc(this.getReplicaNb());
        return l;
    }

    private void check(TraceOperation op) throws IncorrectTrace {
        if (!vc.readyFor(op.getReplica(), op.getVC()))          
            throw new IncorrectTrace("Replica " + this.getReplicaNb() + " not ready for " + op);
    }
}
