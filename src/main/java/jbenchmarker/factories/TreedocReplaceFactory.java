/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker.factories;

import jbenchmarker.core.MergeAlgorithm;
import jbenchmarker.core.ReplicaFactory;
import jbenchmarker.treedocReplace.TreedocRepMerge;

/**
 *
 * @author score
 */
public class TreedocReplaceFactory extends ReplicaFactory{
    @Override
	public MergeAlgorithm create(int r) {
		return new TreedocRepMerge(r);
	}
}
