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
public class OPTreeRemoteOperation<T> implements RemoteOperation{

    List<Integer> path;
    T contain;
    static public enum OpType{ins,del,chT};
    int position;
    int siteId;
    @Override
    public Operation clone() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    
}
