package jbenchmarker.sim;

import java.util.List;
import jbenchmarker.core.*;
import jbenchmarker.trace.IncorrectTrace;
import jbenchmarker.trace.TraceOperation;


/**
 * Used to measure the base memory/time required to simulate a trace.
 * @author urso
 */
public class PlaceboFactory implements ReplicaFactory {

    public static class PlaceboOperation extends Operation {

        public PlaceboOperation(TraceOperation o) {
            super(o);
        }

        @Override
        public Operation clone() {
            return new PlaceboOperation(this.getOriginalOp());
        }        
    }
    
    public MergeAlgorithm createReplica(int r) {
        return new MergeAlgorithm(new Document() {
            public String view() {
                return "";
            }
            public void apply(Operation op) {
            }
        }, r) {
            protected void integrateLocal(Operation op) throws IncorrectTrace {
                this.getDoc().apply(op);
            }
            protected List<Operation> generateLocal(TraceOperation opt) throws IncorrectTrace {
                int nbop = (opt.getType() == TraceOperation.OpType.del) ? opt.getOffset() : opt.getContent().length();
                List<Operation> l = new java.util.ArrayList<Operation>(nbop);
                for (int i = 0; i < nbop; i++)
                    l.add(new PlaceboOperation(opt));
                return l;
            }
        };
    }

}
