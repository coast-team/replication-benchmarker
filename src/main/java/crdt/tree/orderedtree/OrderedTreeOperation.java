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
package crdt.tree.orderedtree;

import collect.OrderedNode;
import crdt.CRDT;
import jbenchmarker.core.Operation;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import jbenchmarker.core.LocalOperation;

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

    @Override
    public LocalOperation adaptTo(CRDT replica) {
        //CRDTOrderedTree tree=(CRDTOrderedTree)replica;
        OrderedNode node = (OrderedNode) replica.lookup();
        int i = 0;
        List<Integer> nPath = path;
        int nPos=this.position;
        for (Integer pNext : this.path) {
            if (node.childrenNumber() <= pNext) {
                nPath = this.path.subList(0, i);
                break;
            }
            node = node.getChild(pNext);
            i++;
        }
        if (this.type == OpType.add) {
            if (nPos>node.childrenNumber()){
                nPos=node.childrenNumber();
            }
            return new OrderedTreeOperation(nPath,nPos,this.content);
            
        }else{
            return new OrderedTreeOperation(nPath);
        }
    }

    public enum OpType {

        add, del
    };
    final private OpType type;
    final private List<Integer> path;
    final private int position;
    final private T content;

    // Add operation
    public OrderedTreeOperation(List<Integer> path, int p, T elem) {
        this.type = OpType.add;
        this.path = path;
        this.position = p;
        this.content = elem;
    }

    // Del operation
    public OrderedTreeOperation(List<Integer> path) {
        this.type = OpType.del;
        this.path = path;
        this.position = 0;
        this.content = null;
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

    @Override
    public String toString() {
        return "OrderedTreeOperation{" + "type=" + type + ", path=" + path + ", position=" + position + ", content=" + content + '}';
    }
}
