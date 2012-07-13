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

import collect.Tree;
import collect.UnorderedNode;
import crdt.CRDT;
import crdt.CRDTMessage;
import crdt.PreconditionException;
import java.util.List;
import jbenchmarker.core.LocalOperation;

/**
 *
 * @author score
 */
public abstract class CRDTTree<T> extends CRDT<Tree<T>>  {
    
   abstract public CRDTMessage add(UnorderedNode<T> father, T element) throws PreconditionException;
    
   abstract public CRDTMessage remove(UnorderedNode<T> subtree) throws PreconditionException;
    
   @Override
   final public CRDTMessage applyLocal(LocalOperation op) throws PreconditionException {
       TreeOperation<T> top = (TreeOperation<T>) op;
       if (top.getType() == TreeOperation.OpType.add)
           return add((UnorderedNode<T>) top.getNode(), top.getContent());
       else
           return remove((UnorderedNode<T>) top.getNode());
   }
        
   abstract public UnorderedNode<T> getRoot();
   
   public UnorderedNode<T> getNode(T ... path) {
       UnorderedNode<T> n = getRoot();
       for (T t : path) {
           n = n.getChild(t);
       }
       return n;
   }
   
   public UnorderedNode<T> getNode(List<T> path) {
       UnorderedNode<T> n = getRoot();
       for (T t : path) {
           n = n.getChild(t);
       }
       return n;
   }
}
