/**
 * Replication Benchmarker
 * https://github.com/score-team/replication-benchmarker/
 * Copyright (C) 2013 LORIA / Inria / SCORE Team
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
import jbenchmarker.core.SequenceOperation;
import jbenchmarker.core.SequenceOperation.OpType;

/**
 *
 * @author urso
 */
public class WootOperation<T> extends SequenceMessage {
    final private Cloneable identifier;   // next   
    final private T content;
    final private SequenceOperation.OpType type;

    public WootOperation(SequenceOperation o, OpType type, Cloneable identifier, T content) {
        super(o);
        this.identifier = identifier;
        this.content = content;
        this.type = type;
    }
    
    public OpType getType() {
        return type;
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
    public SequenceMessage clone() {
        return new WootOperation(this.getOriginalOp(), type,
                identifier instanceof WootIdentifier ? 
                ((WootIdentifier) identifier).clone() : 
                ((WootPosition) identifier).clone(), 
                content);
    }
}
