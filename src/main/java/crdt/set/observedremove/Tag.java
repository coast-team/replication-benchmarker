/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.set.observedremove;

/**
 *
 * @author score
 */
public class Tag {

    private int numReplica;
    private int numOp;

    public Tag() {
        numReplica = 0;
        numOp = 0;
    }

    public Tag(int r, int o) {
        numReplica = r;
        numOp = o;
    }
    
    public int getNumOp() {
        return numOp;
    }
    
    public int getNumReplica() {
        return numReplica;
    }
    
    public void setNumOp(int o) {
        numOp = o;
    }
    
    public void setNumReplica(int n) {
        numReplica = n;
    }
    
    public Tag clone() {
        return new Tag(this.numReplica,this.numOp);
    }

    @Override
    public String toString() {
        return "(" + numReplica + ", " + numOp + ')';
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Tag other = (Tag) obj;
        if (this.numReplica != other.numReplica) {
            return false;
        }
        if (this.numOp != other.numOp) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 73 * hash + this.numReplica;
        hash = 73 * hash + this.numOp;
        return hash;
    }
}
