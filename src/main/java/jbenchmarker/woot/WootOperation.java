/**
 * Replication Benchmarker
 * https://github.com/score-team/replication-benchmarker/
 * Copyright (C) 2011 INRIA / LORIA / SCORE Team
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
package jbenchmarker.woot;

import jbenchmarker.core.SequenceMessage;
import jbenchmarker.trace.TraceOperation;
import jbenchmarker.trace.TraceOperation.OpType;

/**
 *
 * @author urso
 */
public class WootOperation extends SequenceMessage {
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
    public SequenceMessage copy() {
        return (ip == null) ? new WootOperation(this.getOriginalOp(), id.clone()) : 
                new WootOperation(this.getOriginalOp(), id.clone(), ip.clone(), in.clone(), content);
    }
}
