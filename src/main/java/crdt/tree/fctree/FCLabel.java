/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.tree.fctree;

import java.io.Serializable;

/**
 * This is an label with Version and Identifier of last modification
 * T is type of element.
 * @param <T> 
 * @author Stephane Martin <stephane.martin@loria.fr>
 */
public class FCLabel<T> implements Serializable{
    private int version=0;
    private FCIdentifier id;
    private T label;

    /**
     * 
     * @return Identifier of current label
     */
    public FCIdentifier getId() {
        return id;
    }

    /**
     * set identifier 
     * @param id
     */
    public void setId(FCIdentifier id) {
        this.id = id;
    }

    /**
     * return current label
     * @return
     */
    public T getLabel() {
        return label;
    }

    /**
     * set current label
     * @param label
     */
    public void setLabel(T label) {
        this.label = label;
    }

    /**
     * Set new version of label 
     * @param label the new label
     * @param id id of current site.
     */
    /*public void setNewLabel(T label,FCIdentifier id){
        this.label=label;
        this.id=id;
        this.version++;
    }*/
    /**
     * 
     * @return
     */
    public int getVersion() {
        return version;
    }
    /**
     * This setter set only if the version is last or if this version is same  the site has more priority.
     * @param label new state of label
     * @param id Identifier of site
     * @param version version of label.
     */
    public void setLastLabel(T label,FCIdentifier id,int version){
        if (this.version<version || (this.version==version && this.id.compareTo(id)>0)){
            this.label=label;
            this.id=id;
            this.version=version;
        }
    }
    /**
     * set a version.
     * @param version
     */
    public void setVersion(int version) {
        this.version = version;
    }

    /**
     * Construct a label without identifier in version 0
     * @param label
     */
    public FCLabel(T label){
        this.id = null;
        this.label = label;
    }
    /**
     * Construct a label with identifier and version 0
     * @param id
     * @param label
     */
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
