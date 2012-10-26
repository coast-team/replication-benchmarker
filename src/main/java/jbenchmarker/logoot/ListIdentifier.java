/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker.logoot;

import crdt.tree.orderedtree.PositionIdentifier;
import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author urso
 */
public interface ListIdentifier<T> extends PositionIdentifier, Comparable<T>, Cloneable, Serializable {
    long getDigitAt(int index);

    Object getComponentAt(int index);

    int length();

    ArrayList<ListIdentifier> generateN(int n, ListIdentifier Q, int index, long interval, LogootDocument replica);

    ListIdentifier clone();
}
