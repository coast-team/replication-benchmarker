/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.tree.fctree;

/**
 *
 * @author Stephane Martin <stephane.martin@loria.fr>
 */
public class FCLabel<T> {
    private int version=0;
    private FCIdentifier id;
    private T label;

    public FCIdentifier getId() {
        return id;
    }

    public void setId(FCIdentifier id) {
        this.id = id;
    }

    public T getLabel() {
        return label;
    }

    public void setLabel(T label) {
        this.label = label;
    }

    public int getVersion() {
        return version;
    }
    public void setLabelLast(T label,FCIdentifier id,int version){
        if (this.version<version || (this.version==version && this.id.compareTo(id)>0)){
            this.label=label;
            this.id=id;
            this.version=version;
        }
    }
    public void setVersion(int version) {
        this.version = version;
    }

    public FCLabel(T label){
        this.id = null;
        this.label = label;
    }
    public FCLabel(FCIdentifier id, T label) {
        this.id = id;
        this.label = label;
    }

    @Override
    public String toString() {
        return  label==null?"null":label.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final FCLabel<T> other = (FCLabel<T>) obj;
        if (this.version != other.version) {
            return false;
        }
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        if (this.label != other.label && (this.label == null || !this.label.equals(other.label))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + this.version;
        hash = 97 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 97 * hash + (this.label != null ? this.label.hashCode() : 0);
        return hash;
    }
    
    
}
