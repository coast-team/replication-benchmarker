/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker.logoot;

import crdt.tree.orderedtree.PositionIdentifier;
import java.io.Serializable;

/**
 * A logoot indentifier.
 * @author urso
 */
public interface ListIdentifier<T> extends PositionIdentifier, Comparable<T>, Cloneable, Serializable {
    /**
     * Length of this growable identifier.
     */
    int length();

    /**
     * Replica that generates this identifier.
     */
    int replica();
 
    /**
     * Clock used to generates this identifier.
     */
    int clock();   
    
    ListIdentifier clone();
}
