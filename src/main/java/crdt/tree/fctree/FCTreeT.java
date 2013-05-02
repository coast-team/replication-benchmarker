/*
 *  Replication Benchmarker
 *  https://github.com/score-team/replication-benchmarker/
 *  Copyright (C) 2013 LORIA / Inria / SCORE Team
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package crdt.tree.fctree;

import crdt.tree.fctree.policy.PostAction;
import crdt.tree.orderedtree.CRDTOrderedTree;
import java.io.Serializable;

/**
 *
 * @author Stephane Martin <stephane@stephanemartin.fr>
 */
public class FCTreeT extends FCTree implements Serializable{
    
    /**
     * Constructor for an tree with a root identified by site : -1 nbop : 0
     * 
     */
    public FCTreeT() {
        this(false);
    }
    /**
     * 
     * @param action Action trigger after add/del/move operation
     * @param removeEntireTree Remove entire subtree on local remove
     */
    public FCTreeT(PostAction action,boolean removeEntireTree) {
        super(action, removeEntireTree);
    }

    /**
     * 
     * @param removeEntireTree Remove entire subtree on local remove
     */
    public FCTreeT(boolean removeEntireTree) {
        super(removeEntireTree);
        FCIdentifier idroot = new FCIdentifier(-1, 0);
        root = new FCNodeT(root, null, null, idroot);
        map.put(idroot, root);
    }
    
    public FCTreeT(PostAction action) {
        this(action,false);
    }
    @Override
    public CRDTOrderedTree create() {
       return new FCTreeT(this.postAction);
    }
    
}
