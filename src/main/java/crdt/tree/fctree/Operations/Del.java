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
package crdt.tree.fctree.Operations;

import crdt.tree.fctree.FCIdentifier;
import crdt.tree.fctree.FCNode;
import crdt.tree.fctree.FCOperation;
import crdt.tree.fctree.FCTree;
import jbenchmarker.core.Operation;

/**
 *
 * @author Stephane Martin <stephane.martin@loria.fr>
 */
public class Del<T> extends FCOperation {

    FCIdentifier idToKill;

    public Del(FCIdentifier id, FCIdentifier idToKill) {
        super(id);
        this.idToKill = idToKill;
    }

    @Override
    public Operation clone() {
        return new Del(this.getId(), idToKill);
    }

    @Override
    public void apply(FCTree tree) {
        FCNode node = tree.getNodeById(idToKill);
        if (node != null) {
            apply(node, tree);
        }
    }

    public void apply(FCNode<T> nodeToKill, FCTree tree) {
        tree.getMap().remove(nodeToKill.getId());

        FCNode father = nodeToKill.getFather();
        if (father != null) {
            father.delChildren(nodeToKill);
        }
        if (tree.getPostAction() != null) {
            tree.getPostAction().postDel(this, nodeToKill);
        }
    }

    @Override
    public FCIdentifier[] DependOf() {
        return new FCIdentifier[]{this.getId()};
    }

    @Override
    public String toString() {
        return "Del{" + this.getId() + '}';
    }
}
