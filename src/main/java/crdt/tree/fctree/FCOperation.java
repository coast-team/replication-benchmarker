/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.tree.fctree;

import crdt.RemoteOperation;
import jbenchmarker.core.Operation;

/**
 *
 * @author Stephane Martin <stephane.martin@loria.fr>
 */
public abstract class FCOperation<T> implements RemoteOperation {
    //public enum OpType{add,del,chlabel,chorder,move};
    private FCIdentifier id;

    public FCOperation(FCIdentifier id) {
        this.id = id;
    }
    

    public FCIdentifier getId() {
        return id;
    }

    public void setId(FCIdentifier id) {
        this.id = id;
    }
    
    @Override
    public abstract Operation clone() ;
    public abstract void apply(FCTree tree);
    public abstract FCIdentifier[] DependOf();
}
