/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker.ot.otset;

import collect.VectorClock;
import jbenchmarker.ot.SOCT2OperationInterface;

/**
 *
 * @author stephane martin
 */
public class Operations implements SOCT2OperationInterface{

    @Override
    public VectorClock getClock() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getSiteId() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
