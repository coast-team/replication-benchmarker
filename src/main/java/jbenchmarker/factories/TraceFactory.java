/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker.factories;

import jbenchmarker.Experience;
import jbenchmarker.TraceMain;

/**
 *
 * @author score
 */
public class TraceFactory extends ExperienceFactory {

    @Override
    public Experience create(String[] args) throws Exception {
        return new TraceMain(args);
    }

    @Override
    public Experience create() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
