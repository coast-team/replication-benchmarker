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
package jbenchmarker.wootr;

import jbenchmarker.core.SequenceMessage;
import jbenchmarker.core.SequenceOperation;
import jbenchmarker.core.SequenceOperation.OpType;

/**
 *
 * @author urso
 */
public class WootROperation extends SequenceMessage {
    final private WootRNode node; 
        
    /**
     * Constructor for insert operation
     * @param o a trace insert
     * @param id identifier to insert
     * @param ip identifier of previous element
     * @param in identifier of next element
     * @param content content of element
     */
    public WootROperation(SequenceOperation o, WootRNode p, WootRNode n, char content) {
        super(o);
        this.node = new WootRNode(content, p, n);
    }

    /**
     * Constructore for delete operation
     * @param o a trace delete
     * @param id identifier to delete
     */
    public WootROperation(SequenceOperation o, WootRNode e) {
        super(o);
        this.node = e;
    }


    public OpType getType() {
        return this.getOriginalOp().getType();
    }

    public WootRNode getNode() {
        return node;
    }

    
    @Override
    public SequenceMessage clone() {
        return new WootROperation(this.getOriginalOp(), (WootRNode) node.clone());
    }
}
