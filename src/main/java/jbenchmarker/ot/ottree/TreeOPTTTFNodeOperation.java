/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker.ot.ottree;

import crdt.RemoteOperation;
import jbenchmarker.core.Operation;

/**
 *
 * @author Stephane Martin <stephane.martin@loria.fr>
 */
public class TreeOPTTTFNodeOperation<T> implements RemoteOperation{

    static public enum OpType{ins,del,chT,child};
    
    OpType type;
    T contain;
    int position;
    int siteId;
    

    public TreeOPTTTFNodeOperation(OpType type, int position) {
        this.type = type;
        this.position = position;
    }

    public TreeOPTTTFNodeOperation(OpType type, int position, int siteId) {
        this.type = type;
        this.position = position;
        this.siteId = siteId;
    }

    
    public TreeOPTTTFNodeOperation(OpType type, T contain, int position, int siteId) {
        this.type = type;
        this.contain = contain;
        this.position = position;
        this.siteId = siteId;
    }

    public int getSiteId() {
        return siteId;
    }

    public void setSiteId(int siteId) {
        this.siteId = siteId;
    }
    
    
    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
    
    public T getContain() {
        return contain;
    }

    public void setContain(T contain) {
        this.contain = contain;
    }

    public OpType getType() {
        return type;
    }

    public void setType(OpType type) {
        this.type = type;
    }

 
    
    @Override
    public Operation clone() {
       return new TreeOPTTTFNodeOperation(type, contain, position, siteId);
    }
    
}
