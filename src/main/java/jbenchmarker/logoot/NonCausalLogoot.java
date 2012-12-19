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


/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker.logoot;

import collect.HashVectorWithHoles;
import collect.VectorWithHoles;
import jbenchmarker.core.Operation;
import jbenchmarker.core.SequenceMessage;

/**
 * Logoot that support operation delivered in non-causal order.
 * @author urso
 */
public class NonCausalLogoot<T> extends LogootDocument<T> {

    /**
     * Already seen objects.
     */
    VectorWithHoles seen;
    
    public NonCausalLogoot(int r, LogootStrategy strategy) {
        super(r, strategy);
        seen = new HashVectorWithHoles();
    }

    /**
     * Apply insert only if object is not already seen (i.e. previously deleted).
     */
    @Override
    public void apply(Operation op) {
        LogootOperation lg = (LogootOperation) op;
        ListIdentifier id = lg.getIdentifiant();
        int r = id.replica(), h = id.clock();
        if (lg.getType() == SequenceMessage.MessageType.del || !seen.contains(r, h)) {
            super.apply(op);
        }
        seen.add(r, h);
    }
    
    
}
