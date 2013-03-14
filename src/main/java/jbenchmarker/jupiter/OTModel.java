/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker.jupiter;

import java.util.List;
import jbenchmarker.core.LocalOperation;

/**
 * Operational transformation model. 
 * Includes data, operation generation and transformation functions.
 * @author urso
 */
public interface OTModel<T> {
    
    /**
     * Model view.
     */
    public T lookup();

    /**
     * Generates OT operations corresponding to a user local operation.
     * @param local user operation
     * @return corresponding ot operations
     */
    public List<OTOperation> generate(LocalOperation local);

    /**
     * Cross transforms a remote operation with a local operation.
     * Modifies the local operation. Return the transformed remote operation. 
     * @param msg remote operation
     * @param op local operation
     * @return remote transformed
     */
    public OTOperation transform(OTOperation msg, OTOperation op);

    /**
     * Applies a (remote) operation.
     * @param msg remote operation
     */
    public void apply(OTOperation msg);
}
