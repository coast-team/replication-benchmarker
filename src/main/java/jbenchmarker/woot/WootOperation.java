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

import com.sun.org.apache.bcel.internal.generic.INSTANCEOF;
import jbenchmarker.core.SequenceMessage;
import jbenchmarker.core.SequenceOperation;
import jbenchmarker.core.SequenceOperation.OpType;

/**
 *
 * @author urso
 */
public class WootOperation<T> extends SequenceMessage {
    final private Cloneable identifier;   // next   
    final private T content;
        
    /**
     * Constructor for insert operation
     * @param o a trace insert
     * @param id identifier to insert
     * @param ip identifier of previous element
     * @param in identifier of next element
     * @param content content of element
     */
    public WootOperation(SequenceOperation o, WootIdentifier id, WootIdentifier ip, WootIdentifier in, T content) {
        super(o);
        this.identifier = new WootPosition(id, ip, in);
        this.content = content;
    }

    /**
     * Constructore for delete operation
     * @param o a trace delete
     * @param id identifier to delete
     */
    public WootOperation(SequenceOperation o, WootIdentifier id) {
        super(o);
        this.identifier = id;
        this.content = null;        
    }

    private WootOperation(SequenceOperation o, Cloneable id, T content) {
        super(o);
        this.identifier = id;
        this.content = content;        
    }
    
    public OpType getType() {
        return this.getOriginalOp().getType();
    }

    public WootIdentifier getId() {
        return identifier instanceof WootIdentifier ? 
                (WootIdentifier) identifier : 
                ((WootPosition) identifier).getId();
    }

    public WootIdentifier getIn() {
        return ((WootPosition) identifier).getIn();
    }

    public WootIdentifier getIp() {
        return ((WootPosition) identifier).getIp();
    }

    public T getContent() {
        return content;
    }

    @Override
    public SequenceMessage copy() {
        return new WootOperation(this.getOriginalOp(), 
                identifier instanceof WootIdentifier ? 
                ((WootIdentifier) identifier).clone() : 
                ((WootPosition) identifier).clone(), 
                content);
    }
}
