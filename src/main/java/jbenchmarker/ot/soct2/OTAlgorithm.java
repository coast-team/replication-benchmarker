/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker.ot.soct2;

import collect.VectorClock;
import crdt.Factory;
import jbenchmarker.core.Operation;

/**
 *
 * @author urso
 */
public interface OTAlgorithm<O extends Operation> extends Factory<OTAlgorithm<O>> {

    /**
     * This method put on operation the vector clock, increse the vector clock
     * the operation is not applyed
     * @param op The operation
     * @return Soct2Message with vector clock.
     */
    OTMessage estampileMessage(O op);

    /**
     * @return return log object
     */
    SOCT2Log getLog();

    /**
     *
     * @return the vector clock of the instance
     */
    VectorClock getSiteVC();

    /**
     * Integre operation sent by another site or replicats.
     * The operation is returned to apply on document
     * @param soct2message Is a message which contains the operation and vector clock
     * @return operation to performe on document
     */
    Operation integrateRemote(OTMessage soct2message);

    /**
     * Check if the operation is ready by its vector clock
     * @param siteId Site of operation
     * @param vcOp Vector clock of this operation.
     * @return true if its ready false else.
     */
    boolean readyFor(int siteId, VectorClock vcOp);

    void setReplicaNumber(int siteId);

    public int getReplicaNumber();
}
