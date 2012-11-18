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
package jbenchmarker.logootOneId;

import jbenchmarker.core.SequenceMessage;
import jbenchmarker.core.SequenceOperation;
import jbenchmarker.core.SequenceOperation.OpType;

/**
 *
 * @author mehdi urso
 */
public class LogootOneIdOperation<T> extends SequenceMessage
{

    final private LogootOneIdentifier identif;
    
    final private T content;
    final private MessageType type;

    private LogootOneIdOperation(SequenceOperation o, MessageType type, LogootOneIdentifier identif, T content) {
        super(o);
        this.type = type;
        this.identif = identif;
        this.content = content;
    }
    
    public MessageType getType() {
        return this.type;
    }

    public LogootOneIdentifier getIdentifiant() {
        return identif;
    }
    
    public T getContent() {
        return content;
    }

    static <T> LogootOneIdOperation insert(SequenceOperation o, LogootOneIdentifier idf, T cont) {
        return new LogootOneIdOperation(o, MessageType.ins, idf, cont);
    }

    public static LogootOneIdOperation Delete(SequenceOperation o, LogootOneIdentifier idf) {
        return new LogootOneIdOperation(o, MessageType.del, idf, null);
    }

    @Override
    public SequenceMessage clone() {
        return new LogootOneIdOperation(this.getOriginalOp(), type, this.identif.clone(), this.content);
    }

}
