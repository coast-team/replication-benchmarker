/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker.woot;

import java.io.Serializable;

/**
 *
 * @author urso
 */
public abstract class WootNode<T> implements Serializable {

    protected final T content;
    protected final WootIdentifier id; // own identifier

    public WootNode(WootIdentifier id, T content) {
        this.content = content;
        this.id = id;
    }

    /**
     * Two invisible woot nodes are considered equal.
     *
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        WootNode other = (WootNode) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        if (this.content != other.content && (this.content == null || !this.content.equals(other.content))) {
            return false;
        }
        return true;
    }

    public T getContent() {
        return content;
    }

    public WootIdentifier getId() {
        return id;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 59 * hash + (this.content != null ? this.content.hashCode() : 0);
        return hash;
    }
    
    public abstract boolean isVisible();
}
