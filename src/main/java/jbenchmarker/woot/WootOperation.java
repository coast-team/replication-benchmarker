package jbenchmarker.woot;

import jbenchmarker.core.Operation;
import jbenchmarker.trace.TraceOperation;
import jbenchmarker.trace.TraceOperation.OpType;

/**
 *
 * @author urso
 */
public class WootOperation extends Operation {
    final private WootIdentifier id;
    final private WootIdentifier ip;   // previous
    final private WootIdentifier in;   // next   
    final private char content;
        
    /**
     * Constructor for insert operation
     * @param o a trace insert
     * @param id identifier to insert
     * @param ip identifier of previous element
     * @param in identifier of next element
     * @param content content of element
     */
    public WootOperation(TraceOperation o, WootIdentifier id, WootIdentifier ip, WootIdentifier in, char content) {
        super(o);
        this.id = id;
        this.ip = ip;
        this.in = in;
        this.content = content;
    }

    /**
     * Constructore for delete operation
     * @param o a trace delete
     * @param id identifier to delete
     */
    public WootOperation(TraceOperation o, WootIdentifier id) {
        super(o);
        this.id = id;
        this.ip = null;
        this.in = null;
        this.content = (char) 0;        
    }


    public OpType getType() {
        return this.getOriginalOp().getType();
    }

    public WootIdentifier getId() {
        return id;
    }

    public WootIdentifier getIn() {
        return in;
    }

    public WootIdentifier getIp() {
        return ip;
    }

    public char getContent() {
        return content;
    }

    @Override
    public Operation clone() {
        return (ip == null) ? new WootOperation(this.getOriginalOp(), id.clone()) : 
                new WootOperation(this.getOriginalOp(), id.clone(), ip.clone(), in.clone(), content);
    }
}
