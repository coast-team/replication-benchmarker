package jbenchmarker.core;

import jbenchmarker.trace.TraceOperation;

/**
 * An operation for a replication algorithm.
 * @author urso
 */
public abstract class Operation {
    final private TraceOperation originalOp;       // Trace operation issuing this one

    public TraceOperation getOriginalOp() {
        return originalOp;
    }
    
    public Operation(TraceOperation o) {
        this.originalOp = o;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Operation other = (Operation) obj;
        if (this.originalOp != other.originalOp && (this.originalOp == null || !this.originalOp.equals(other.originalOp))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 17 * hash + (this.originalOp != null ? this.originalOp.hashCode() : 0);
        return hash;
    }
    
    abstract public Operation clone();
}
