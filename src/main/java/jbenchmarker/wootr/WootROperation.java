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

import crdt.Operation;
import jbenchmarker.core.SequenceOperation;
import jbenchmarker.core.SequenceOperation.OpType;

/**
 *
 * @author urso
 */
public class WootROperation implements Operation {
    final private WootRNode node; 
    final private OpType type;
    
    /**
     * Constructor for insert operation
     * @param o a trace insert
     * @param id identifier to insert
     * @param ip identifier of previous element
     * @param in identifier of next element
     * @param content content of element
     */
    public WootROperation(OpType type, WootRNode p, WootRNode n, char content) {
        this(type, new WootRNode(content, p, n));
    }

    /**
     * Constructore for delete operation
     * @param o a trace delete
     * @param id identifier to delete
     */
    public WootROperation(OpType type, WootRNode e) {
        this.type = type;
        this.node = e;
    }


    public OpType getType() {
        return type;
    }

    public WootRNode getNode() {
        return node;
    }

    
    @Override
    public Operation clone() {
        return new WootROperation(null, (WootRNode) node.clone());
    }
}
