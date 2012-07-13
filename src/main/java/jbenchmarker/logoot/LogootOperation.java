/**
 * Replication Benchmarker
 * https://github.com/score-team/replication-benchmarker/
 * Copyright (C) 2012 LORIA / Inria / SCORE Team
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
package jbenchmarker.logoot;

import jbenchmarker.core.SequenceMessage;
import jbenchmarker.core.SequenceOperation;
import jbenchmarker.core.SequenceOperation.OpType;

/**
 *
 * @author mehdi urso
 */
public class LogootOperation<T> extends SequenceMessage {
    
    final private LogootIdentifier identif;
    final private T content;
    final private MessageType type;

    private LogootOperation(SequenceOperation o, MessageType type, LogootIdentifier identif, T content) {
        super(o);
        this.type = type;
        this.identif = identif;
        this.content = content;
    }
    
    public MessageType getType() {
        return this.type;
    }

    public LogootIdentifier getIdentifiant() {
        return identif;
    }
    
    public T getContent() {
        return content;
    }

    static <T> LogootOperation insert(SequenceOperation o, LogootIdentifier idf, T cont) {
        return new LogootOperation(o, MessageType.ins, idf, cont);
    }

    public static LogootOperation Delete(SequenceOperation o, LogootIdentifier idf) {
        return new LogootOperation(o, MessageType.del, idf, null);
    }

    // FIXME: shoud clone the operation and its parameters
    @Override
    public SequenceMessage clone() {
        return new LogootOperation(this.getOriginalOp(), type, this.identif.clone(), this.content);
    }

}
