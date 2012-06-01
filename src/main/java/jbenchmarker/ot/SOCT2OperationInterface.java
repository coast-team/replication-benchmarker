/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker.ot;

import collect.VectorClock;

/**
 *
 * @author Stephane Martin
 * Ceci est le nécessaire pour soct2 mais on va quand même ajouter des ops
 */
public interface SOCT2OperationInterface {
    public VectorClock getClock();
    public int getSiteId();
}
