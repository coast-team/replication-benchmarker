/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker.woot;

import crdt.tree.orderedtree.PositionIdentifier;
import java.io.Serializable;

/**
 *
 * @author urso
 */
public class WootPosition implements Cloneable, PositionIdentifier, Serializable {
    final private WootIdentifier id;
    final private WootIdentifier ip;   // previous
    final private WootIdentifier in;   // next

    public WootPosition(WootIdentifier id, WootIdentifier ip, WootIdentifier in) {
        this.id = id;
        this.ip = ip;
        this.in = in;
    }

    public WootIdentifier getId() {
        return id;
    }

    public WootIdentifier getIn() {
        return in;
    }

    public WootIdentifier getIp() {
        return ip;
    }
    
    @Override
    public WootPosition clone() {
        return new WootPosition(id.clone(), ip.clone(), in.clone());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final WootPosition other = (WootPosition) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        if (this.ip != other.ip && (this.ip == null || !this.ip.equals(other.ip))) {
            return false;
        }
        if (this.in != other.in && (this.in == null || !this.in.equals(other.in))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 29 * hash + (this.ip != null ? this.ip.hashCode() : 0);
        hash = 29 * hash + (this.in != null ? this.in.hashCode() : 0);
        return hash;
    }
}
