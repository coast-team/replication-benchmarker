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
package jbenchmarker.ot.soct2;

import crdt.Operation;


/**
 *
 * @author urso
 */
public class SOCT2LogOptimizedPlace<Op extends Operation> extends SOCT2Log<Op> {

    
    public SOCT2LogOptimizedPlace(SOCT2TranformationInterface t) {
        super(t);
    }

    @Override
    protected Op placeOperation(OTMessage<Op> message, int separationIndex) {
        Op opt = message.getOperation(), oo = (Op) opt.clone();
        operations.add(separationIndex, message);
        for (int i = separationIndex + 1; i < this.operations.size(); ++i) {
            Op opi = (Op) operations.get(i).getOperation().clone();
            transforme.transpose(operations.get(i).getOperation(), oo);
            oo = transforme.transpose(oo, opi);
        }
        return oo;
    }

    @Override
    public SOCT2Log<Op> create() {
        return new SOCT2LogOptimizedPlace<Op>(transforme);
    }
}
