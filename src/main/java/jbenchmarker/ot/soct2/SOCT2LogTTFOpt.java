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
package jbenchmarker.ot.soct2;

import jbenchmarker.core.Operation;

/**
 *  This object is a log of Soct2. It splits the history the process to transform new operation
 * @param <Op> 
 * @author oster
 */
public class SOCT2LogTTFOpt<Op extends Operation> extends SOCT2Log<Op>{

    public SOCT2LogTTFOpt(SOCT2TranformationInterface t) {
        super(t);
    }

    
    @Override
    protected Op placeOperation(OTMessage<Op> message, int separationIndex) {  
        Op opt = message.getOperation();
        for (int i = separationIndex; i < this.operations.size(); i++) {
            opt = transforme.transpose(opt, operations.get(i).getOperation());
        }
        if (!(transforme instanceof SOCT2TranformationInterfaceOpt) ||
                ((SOCT2TranformationInterfaceOpt)transforme).isLogInterest(opt) ){
        //if (!message.operation instanceof TTFOperation || ((TTFOperation)message.operation ).getType()==TTFOperation)
        operations.add(message);
        }
        return opt;
    }

    @Override
    public SOCT2Log<Op> create() {
        return new SOCT2LogTTFOpt<Op>(transforme);
    }

  
}
