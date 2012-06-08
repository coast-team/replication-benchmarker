/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker.core;

/**
 *
 * @author urso
 */
public interface ReplicatedMessage extends Cloneable {
    public ReplicatedMessage concat(ReplicatedMessage msg);

    public ReplicatedMessage clone();
    
    public int size();
}
