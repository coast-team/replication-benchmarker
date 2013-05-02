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
import crdt.tree.fctree.FCPosition;
import crdt.tree.fctree.FCTree;
import jbenchmarker.core.Operation;

/**
 *
 * @author Stephane Martin <stephane.martin@loria.fr>
 */
public class Add<T> extends FCOperation {

    T label;
    FCPosition position;
    FCIdentifier father;

    public Add(T label, FCPosition position, FCIdentifier father, FCIdentifier id) {
        super(id);
        this.label = label;
        this.position = position;
        this.father = father;
    }

    @Override
    public Operation clone() {
        return new Add(label, position, father, this.getId());
    }

    @Override
    public void apply(FCTree tree) {
        FCNode node = tree.getNodeById(this.father);
        // if (node != null) {
        apply(node, tree);
        //}
    }

    public void apply(FCNode<T> father, FCTree tree) {
        FCNode newnode = tree.getRoot().createNode(father, label, position, this.getId());
        if (father != null) {
            father.addChildren(newnode);
        }
        tree.getMap().put(this.getId(), newnode);
         if (tree.getPostAction() != null) {
            tree.getPostAction().postAdd(this, newnode);
        }

    }

    @Override
    public FCIdentifier[] DependOf() {
        return new FCIdentifier[]{father};
    }

    @Override
    public String toString() {
        return "Add{id=" + this.getId() + " label=" + label + ", position=" + position + ", father=" + father + '}';
    }
}
