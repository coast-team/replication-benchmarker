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
package jbenchmarker.logoot;

import jbenchmarker.core.SequenceMessage;
import jbenchmarker.trace.SequenceOperation;
import jbenchmarker.trace.SequenceOperation.OpType;

/**
 *
 * @author mehdi urso
 */
public class LogootOperation extends SequenceMessage
{

    final private LogootIdentifier identif;
    
    final private char content;

    private LogootOperation(SequenceOperation o, LogootIdentifier identif, char content) {
        super(o);
        this.identif = identif;
        this.content = content;
    }
    
    public OpType getType() {
        return this.getOriginalOp().getType();
    }

    public LogootIdentifier getIdentifiant() {
        return identif;
    }
    
    public char getContent() {
        return content;
    }

    static LogootOperation insert(SequenceOperation o, LogootIdentifier idf, char cont) {
        return new LogootOperation(o, idf, cont);
    }

    public static LogootOperation Delete(SequenceOperation o, LogootIdentifier idf) {
        return new LogootOperation(o, idf, (char) 0);
    }

    // FIXME: shoud clone the operation and its parameters
    @Override
    public SequenceMessage copy() {
        return new LogootOperation(this.getOriginalOp(), this.identif.clone(), this.content);
    }

}
