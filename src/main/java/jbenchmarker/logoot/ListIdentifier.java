/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker.logoot;

import crdt.tree.orderedtree.PositionIdentifier;
import java.io.Serializable;

/**
 *
 * @author urso
 */
public interface ListIdentifier<T> extends PositionIdentifier, Comparable<T>, Cloneable, Serializable {
    int length();
 
    ListIdentifier clone();
}
