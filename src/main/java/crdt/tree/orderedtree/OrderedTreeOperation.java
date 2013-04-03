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
package crdt.tree.orderedtree;

import collect.OrderedNode;
import crdt.CRDT;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import jbenchmarker.core.LocalOperation;
import jbenchmarker.core.Operation;

/**
 *
 * @author score
 */
public class OrderedTreeOperation<T> implements LocalOperation {

    @Override
    public Operation clone() {
        try {
            return (Operation) super.clone();
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(OrderedTreeOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    OrderedNode adaptPath(List<Integer> path, OrderedNode root, List<Integer> dst) {
        //List<Integer> nPath = path;
        int i = 0;
        for (Integer pNext : this.path) {

            if (root.getChildrenNumber() <= pNext) {
                //return this.path.subList(0, i);
                return root;
            }
            dst.add(pNext);
            root = root.getChild(pNext);
            i++;
        }
        return root;
    }
   
    @Override
    public LocalOperation adaptTo(CRDT replica) {
        //CRDTOrderedTree tree=(CRDTOrderedTree)replica;
        OrderedNode root = (OrderedNode) replica.lookup();
        int i = 0;
        List<Integer> nPath = new LinkedList();
        OrderedNode node = adaptPath(this.path, root, nPath);
        int nPos = this.position;

        switch (this.type) {
            case add:
                if (nPos > node.getChildrenNumber()) {
                    nPos = node.getChildrenNumber();
                }
                return new OrderedTreeOperation(nPath, nPos, this.content);
            case del:
                return new OrderedTreeOperation(nPath);
            case move:
                List<Integer> nPathDst = new LinkedList();
                OrderedNode nodedst = adaptPath(this.dstPath, root, nPathDst);
                if (nPos > nodedst.getChildrenNumber()) {
                    nPos = nodedst.getChildrenNumber();
                }
                return new OrderedTreeOperation(type, nPath, nPathDst, nPos, null);
            case chContent:
                return new OrderedTreeOperation(type, nPath, null, 0, this.content);
            default:
                throw new UnsupportedOperationException("Not compatible");
        }
    }

    public enum OpType {

        add, del, move, chContent
    };
    final private OpType type;
    final private List<Integer> path;
    final private List<Integer> dstPath;
    final private int position;
    final private T content;

    /**
     * Generic constructor
     *
     * @param type type of operation
     * @param path path for add and del et source for move operation
     * @param dstPath destination for move operation
     * @param position position of insertion
     * @param content new element for chContent
     */
    public OrderedTreeOperation(OpType type, List<Integer> path, List<Integer> dstPath, int position, T content) {
        this.type = type;
        this.path = path;
        this.dstPath = dstPath;
        this.position = position;
        this.content = content;
    }

    // Add operation
    public OrderedTreeOperation(List<Integer> path, int p, T elem) {
        this(OpType.add, path, null, p, elem);
    }

    // Del operation
    public OrderedTreeOperation(List<Integer> path) {
        this(OpType.del, path, null, 0, null);
    }

    public OpType getType() {
        return type;
    }

    public T getContent() {
        return content;
    }

    public int getPosition() {
        return position;
    }

    public List<Integer> getPath() {
        return path;
    }

    public List<Integer> getDstPath() {
        return dstPath;
    }

    @Override
    public String toString() {
        return "OrderedTreeOperation{" + "type=" + type + ", path=" + path + ", position=" + position + ", content=" + content + '}';
    }
}
