/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.tree.fctree;

/**
 *
 * @author Stephane Martin <stephane.martin@loria.fr>
 */
public class IdFactory {
    private int replica;
    private int nbOp;
    
    FCIdentifier createId(){
        return new FCIdentifier(replica, nbOp++);
    }

    void setReplicaNumber(int replica) {
        this.replica=replica;
    }

    public int getNbOp() {
        return nbOp;
    }

    public int getReplica() {
        return replica;
    }
    
}
