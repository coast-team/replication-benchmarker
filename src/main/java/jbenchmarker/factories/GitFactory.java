/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker.factories;

import jbenchmarker.Experience;
import jbenchmarker.GitMain;

/**
 *
 * @author score
 */
public class GitFactory extends ExperienceFactory{

    @Override
    public Experience create(String[] args) throws Exception {
        return new GitMain(args);
    }

    @Override
    public Experience create() {
        throw new UnsupportedOperationException("Not supported yet.");
    }


}
