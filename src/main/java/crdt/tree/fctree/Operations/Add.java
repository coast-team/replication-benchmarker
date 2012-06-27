/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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

    public Add(T label, FCPosition position, FCIdentifier father,FCIdentifier id) {
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
        FCNode node =tree.getNodeById(this.father);
        if (node!=null)
            applyOnNode(node,tree);
    }
    public void applyOnNode(FCNode <T> father,FCTree tree){
        FCNode newnode=new FCNode(father,label,position,this.getId());
        father.addChildren(newnode);        
        tree.getMap().put(this.getId(),newnode);
    }

    @Override
    public FCIdentifier[] DependOf() {
        return new FCIdentifier []{father};
    }

    @Override
    public String toString() {
        return "Add{id="+this.getId() + " label=" + label + ", position=" + position + ", father=" + father + '}';
    }
    
}
