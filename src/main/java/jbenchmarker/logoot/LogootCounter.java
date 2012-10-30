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

import jbenchmarker.core.*;

/**
 * Logoot document for counting concurrent deletes. 
 * Not most efficient implementation.
 * @author urso
 */
@Deprecated
public class LogootCounter extends LogootDocument {
    public static class Factory extends ReplicaFactory {
        @Override public MergeAlgorithm create(int r) {
                return new LogootMerge(new LogootCounter(r, new BoundaryStrategy(64, 1000000000)), 1);
        }
    }
    
    public static int count = 0;

    public LogootCounter(int r, LogootStrategy strategy) {
        super(r, strategy, null, null);
    }
    
    @Override
    public void apply(Operation op) {
        LogootOperation lg = (LogootOperation) op;
        ListIdentifier idToSearch = lg.getIdentifiant();
        int pos = dicho(idToSearch);
        //Insertion et Delete
        if ((lg.getType() == SequenceMessage.MessageType.ins) && !getId(pos).equals(idToSearch)) {
            count++;
        }
        super.apply(op);
    }
}
