/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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

    public Del(FCIdentifier id) {
        super(id);
    }

    @Override
    public Operation clone() {
        return new Del(this.getId());
    }

    @Override
    public void apply(FCTree tree) {
        FCNode node = tree.getNodeById(this.getId());
        if (node != null) {
            applyOnNode(node, tree);
        }
    }

    public void applyOnNode(FCNode<T> nodeToKill, FCTree tree) {
        tree.getMap().remove(nodeToKill.getId());

        FCNode father = nodeToKill.getFather();
        if (father != null) {
            father.delChildren(nodeToKill);
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
