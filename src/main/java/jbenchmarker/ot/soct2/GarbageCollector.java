/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker.ot.soct2;

import crdt.Factory;

/**
 *
 * @author urso
 */
public interface GarbageCollector extends Factory<GarbageCollector> {

    /**
     * register the soct2message to get anothers vector clock and count before run
     * @param mess Soct2messages
     */
    void collect(OTAlgorithm otalgo, OTMessage mess);
    
}
