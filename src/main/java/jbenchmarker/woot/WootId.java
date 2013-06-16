/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker.woot;

import java.io.Serializable;

/**
 * For clone compilation purpose. 
 * @author urso
 */
public interface WootId extends Serializable, Cloneable {
    WootId clone();
}
