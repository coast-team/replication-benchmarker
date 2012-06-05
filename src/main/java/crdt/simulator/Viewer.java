/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.simulator;

import collect.VectorClock;
import crdt.CRDTMessage;

/**
 *
 * @author Stephane Martin
 */
public interface Viewer {
    public void addMessage(VectorClock emitor,VectorClock receptor,int e,int r,CRDTMessage m);
    public void clear();
    public void affiche();
}
