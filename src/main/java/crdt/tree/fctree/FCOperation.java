/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
