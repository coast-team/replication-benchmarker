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
public class FCIdentifier implements Serializable{
    int replicaNumber;
    int operationNumber;

    /**
     * 
     * @param replicaNumber
     * @param operationNumber
     */
    public FCIdentifier(int replicaNumber, int operationNumber) {
        this.replicaNumber = replicaNumber;
        this.operationNumber = operationNumber;
    }

    /**
     * 
     * @return
     */
    public int getOperationNumber() {
        return operationNumber;
    }

    /**
     * 
     * @return
     */
    public int getReplicaNumber() {
        return replicaNumber;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final FCIdentifier other = (FCIdentifier) obj;
        if (this.replicaNumber != other.replicaNumber) {
            return false;
        }
        if (this.operationNumber != other.operationNumber) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + this.replicaNumber;
        hash = 17 * hash + this.operationNumber;
        return hash;
    }

    /**
     * 
     * @param id
     * @return
     */
    public int compareTo(FCIdentifier id){
       if (this.equals(id))
           return 0;
       if (this.replicaNumber>id.replicaNumber 
               || (this.replicaNumber==id.replicaNumber 
                       && this.operationNumber>id.operationNumber))
           return 1;
       return -1;
   }

    @Override
    public String toString() {
        return "(" + replicaNumber + "," + operationNumber + ')';
    }
   
    
}
