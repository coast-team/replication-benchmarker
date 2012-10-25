/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker.factories;

import jbenchmarker.core.ReplicaFactory;
import jbenchmarker.logootOneId.*;
/**
 *
 * @author score
 */
public class LogootOneIdFactory<T>  extends ReplicaFactory {
    
     @Override
    public LogootOneIdMerge<T> create(int r) {
        return new LogootOneIdMerge<T>(new LogootOneIdDocument(r, new BoundaryStrategy(1000000000)), r);
    }
    
}
