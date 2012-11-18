/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker.factories;

import crdt.Factory;
import jbenchmarker.Experience;

/**
 *
 * @author score
 */
public abstract class ExperienceFactory implements Factory<Experience>{ 
     abstract public Experience create(String[] args) throws Exception;
}
