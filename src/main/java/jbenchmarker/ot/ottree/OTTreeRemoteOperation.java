/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker.ot.ottree;

import crdt.RemoteOperation;
import java.util.List;
import jbenchmarker.core.Operation;

/**
 *
 * @author Stephane Martin <stephane.martin@loria.fr>
 */
public class OTTreeRemoteOperation<T> implements RemoteOperation{

    private List<Integer> path;
    private T contain;
    static public enum OpType{ins,del,chT};
    //int position;
    private int siteId;
    private OpType type;

    public OTTreeRemoteOperation(List<Integer> path, T contain, int siteId, OpType type) {
        this.path = path;
        this.contain = contain;
        this.siteId = siteId;
        this.type = type;
    }

    public OTTreeRemoteOperation(List<Integer> path,  int siteId, OpType type) {
        this.path = path;
        this.siteId = siteId;
        this.type = type;
    }
    public OpType getType() {
        return type;
    }

    public void setType(OpType type) {
        this.type = type;
    }
    @Override
    public Operation clone() {
        return new OTTreeRemoteOperation(path, contain, siteId, type);
    }

    public T getContain() {
        return contain;
    }

    public void setContain(T contain) {
        this.contain = contain;
    }

    public List<Integer> getPath() {
        return path;
    }

    public void setPath(List<Integer> path) {
        this.path = path;
    }

   /* public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }*/

    public int getSiteId() {
        return siteId;
    }

    public void setSiteId(int siteId) {
        this.siteId = siteId;
    }
    
    
}
