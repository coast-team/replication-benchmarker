/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.simulator;

import java.util.Enumeration;

/**
 *
 * @author urso
 */
public interface Trace {
    Enumeration<TraceOperation> enumeration();
}
