/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker.core;

import crdt.CRDT;

/**
 *
 * @author Stephane Martin <stephane.martin@loria.fr>
 */
public interface LocalOperation extends Operation{
    public LocalOperation adaptTo(CRDT replica);
}
