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
package crdt.tree;

import collect.Node;
import crdt.CRDT;
import crdt.tree.orderedtree.OrderedTreeOperation;
import java.util.logging.Level;
import java.util.logging.Logger;
import jbenchmarker.core.LocalOperation;
import jbenchmarker.core.Operation;

/**
 *
 * @author score
 */
public class TreeOperation<T> implements LocalOperation {

    @Override
    public LocalOperation adaptTo(CRDT replica) {
        //TODO : make correct adaptation function For TreeOperation
        return this;
    }
    
    public enum OpType {add, del, move}; 
    private final OpType type;
    private final Node<T> node, dest;
    private final T content;

    public TreeOperation(OpType type, Node<T> node, T content) {
        this.type = type;
        this.node = node;
        this.dest = null;
        this.content = content;
    }

    // Add operation
    public TreeOperation(Node<T> obj, T elem) {
        this(OpType.add, obj, elem);
    }
    
    // Del operation
    public TreeOperation(Node<T> obj) {
        this(OpType.del, obj, null);
    }

    public TreeOperation(Node<T> node, Node<T> dest, T content) {
        this.type = OpType.move;
        this.node = node;
        this.dest = dest;
        this.content = content;
    }

    public OpType getType() {
        return type;
    }

    public T getContent() {
        return content;
    }

    public Node<T> getNode() {
        return node;
    }

    public Node<T> getDest() {
        return dest;
    }
    
    @Override
    public Operation clone() {
        try {
            return (Operation) super.clone();
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(OrderedTreeOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
