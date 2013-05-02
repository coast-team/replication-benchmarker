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
 
package crdt.tree.fctree.policy;

import crdt.tree.fctree.FCNode;
import crdt.tree.fctree.FCTree;
import crdt.tree.fctree.Operations.Add;
import crdt.tree.fctree.Operations.ChX;
import crdt.tree.fctree.Operations.Del;

/**
 *
 * @author Stephane Martin <stephane.martin@loria.fr>
 */
public interface PostAction extends Cloneable {
   
    public void postMove(ChX operation, FCNode node);
    public void postDel(Del operation, FCNode node);
    public void postAdd(Add operation, FCNode node );
    public void setTree(FCTree tree);
    //public void update(Observable o, FCNodeGf arg);

    public PostAction clone();
    
}
