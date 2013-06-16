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
package crdt.set;

import crdt.CRDT;
import crdt.tree.orderedtree.OrderedTreeOperation;
import java.util.logging.Level;
import java.util.logging.Logger;
import crdt.Operation;
import jbenchmarker.core.LocalOperation;
/**
 *
 * @author score
 */
public class SetOperation<T> implements LocalOperation {

    @Override
    public LocalOperation adaptTo(CRDT replica) {
        //TODO: Correct Adaption 
        return this;
        /*if (this.type==OpType.del && !((CRDTSet)replica).contains(this.content)){
            
        }*/
    }
    
    public enum OpType {add, del}; 
    private OpType type;
    private T content;
    
    public SetOperation(OpType type, T obj) {
        this.type = type;
        this.content = obj;
    }

    public OpType getType() {
        return type;
    }
    
    public T getContent() {
        return content;
    }

    @Override
    public String toString() {
        return "SetOperation{" + "type=" + type + ", content=" + content + '}';
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
