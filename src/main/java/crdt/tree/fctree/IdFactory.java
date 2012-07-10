/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.tree.fctree;

import java.io.Serializable;

/**
 *
 * @author Stephane Martin <stephane.martin@loria.fr>
 */
public class IdFactory implements Serializable{
    private int replica;
    private int nbOp;
    
    /**
     * Create new id with incremented nbOp
     * @return return new id.
     */
    FCIdentifier createId(){
        return new FCIdentifier(replica, nbOp++);
    }

    void setReplicaNumber(int replica) {
        this.replica=replica;
    }

    /**
     * 
     * @return number of generated operation
     */
    public int getNbOp() {
        return nbOp;
    }

    /**
     * 
     * @return get replica number
     */
    public int getReplica() {
        return replica;
    }
    
}
