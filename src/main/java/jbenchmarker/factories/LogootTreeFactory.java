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
package jbenchmarker.factories;

import jbenchmarker.core.ReplicaFactory;
import jbenchmarker.logoot.BoundaryStrategy;
import jbenchmarker.logoot.LogootStrategy;
import jbenchmarker.logoot.tree.LogootTreeDocument;
import jbenchmarker.logoot.tree.LogootTreeMerge;

public class LogootTreeFactory extends ReplicaFactory {

    @Override
    public LogootTreeMerge create(int r) {         
        return new LogootTreeMerge(createDoc(r, 64, 1000000000), r);
    }
    
    static public LogootTreeDocument createDoc(int r, int base, int bound) {
        LogootStrategy s = new BoundaryStrategy(base, bound);
        return new LogootTreeDocument(r, s); 
    }
}
