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

import collect.Node;
import collect.OrderedNode;
import collect.Tree;
import collect.UnorderedNode;
import crdt.CRDT;
import crdt.CRDTMessage;
import crdt.Factory;
import crdt.PreconditionException;
import crdt.tree.CRDTUnorderedTree;
import crdt.tree.TreeOperation;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 *
 * @author urso
 */
public class PositionIdentifierTree<T> extends CRDTOrderedTree<T> implements Observer {

    CRDTUnorderedTree<Positioned<T>> tree;
    PositionnedNode<T> root;

    public PositionIdentifierTree(PositionnedNode<T> pic, Factory<CRDT<Tree<Positioned<T>>>> tree) {
        this.tree = (CRDTUnorderedTree<Positioned<T>>) tree.create();
        this.root = pic;
        this.tree.addObserver(this);
    }

    @Override
    public CRDTMessage add(List<Integer> path, int p, T element) throws PreconditionException {
        OrderedNode<T> f = root;
        UnorderedNode<Positioned<T>> uf = tree.getRoot();
        for (int i : path) {
            uf = uf.getChild(((PositionnedNode<T>) f).getPositioned(i));
            f = f.getChild(i);
        }
        PositionIdentifier pi = ((PositionnedNode<T>) f).getNewPosition(p, element);
        CRDTMessage msg = tree.add(uf, new Positioned(pi, element));
        return msg;
    }

    @Override
    public CRDTMessage remove(List<Integer> path) throws PreconditionException {
        OrderedNode<T> f = root;
        UnorderedNode<Positioned<T>> uf = tree.getRoot();
        for (int i : path) {
            uf = uf.getChild(((PositionnedNode<T>) f).getPositioned(i));
            f = f.getChild(i);
        }
        CRDTMessage msg = tree.remove(uf);
        return msg;
    }

    @Override
    public void applyOneRemote(CRDTMessage msg) {
        tree.applyRemote(msg);
    }

    @Override
    public OrderedNode<T> lookup() {
        return root;
    }

    @Override
    public PositionIdentifierTree<T> create() {
        return new PositionIdentifierTree<T>((PositionnedNode)root.createNode(null), tree);
    }

    @Override
    public void update(Observable o, Object arg) {
        TreeOperation<Positioned<T>> op = (TreeOperation<Positioned<T>>) arg;
        PositionIdentifier pi = op.getContent().getPi();
        T t = op.getContent().getElem();
        PositionnedNode<T> father = (PositionnedNode<T>) link(op.getNode());
        if (op.getType() == TreeOperation.OpType.add) {
            father.add(pi, t);
        } else if (op.getType() == TreeOperation.OpType.del) {
            father.remove(pi, t);
        } else { // move seems to bug

            PositionnedNode<T> dest =(PositionnedNode<T>) link(op.getDest());
            father.remove(pi, t);
            dest.add(pi, t);
        }
    }

    private OrderedNode link(Node<Positioned<T>> node) {
        List<Positioned<T>> path = node.getPath();
        PositionnedNode<T> father = root;
        for (Positioned<T> p : path) {
            father = (PositionnedNode)father.getChild(p);
        }
        return father;
    }

    @Override
    public void setReplicaNumber(int replicaNumber) {
        super.setReplicaNumber(replicaNumber);
        tree.setReplicaNumber(replicaNumber);
        root.setReplicaNumber(replicaNumber);
    }

    @Override
    public CRDTMessage rename(List<Integer> path, T newValue) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public CRDTMessage move(List<Integer> from, List<Integer> to, int p) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
