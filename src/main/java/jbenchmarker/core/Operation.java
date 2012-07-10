/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker.core;

import java.io.Serializable;

/**
 *
 * @author urso
 */
public interface Operation<T> extends Cloneable,Serializable {
    public Operation<T> clone();
}
