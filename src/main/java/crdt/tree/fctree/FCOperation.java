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
package crdt.tree.fctree;

import crdt.RemoteOperation;
import jbenchmarker.core.Operation;

/**
 *
 * @param <T> 
 * @author Stephane Martin <stephane.martin@loria.fr>
 */
public abstract class FCOperation<T> implements RemoteOperation {
    //public enum OpType{add,del,chlabel,chorder,move};
    private FCIdentifier id;

    /**
     * Make new operation with identifier of this
     * @param id
     */
    public FCOperation(FCIdentifier id) {
        this.id = id;
    }
    

    /**
     * get the identifier of the operation
     * @return
     */
    public FCIdentifier getId() {
        return id;
    }

    /**
     * set an identifier for this operation
     * @param id
     */
    public void setId(FCIdentifier id) {
        this.id = id;
    }
    
    @Override
    public abstract Operation clone() ;
    /**
     * Apply this operation of a tree
     * @param tree
     */
    public abstract void apply(FCTree tree);
    /**
     * return identifier of this operation depend
     * @return array of FCidentifier
     */
    public abstract FCIdentifier[] DependOf();
}
