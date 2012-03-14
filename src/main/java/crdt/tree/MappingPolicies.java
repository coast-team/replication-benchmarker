/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.tree;

import crdt.tree.graphtree.Gtree;
import collect.Tree;

/**
 *
 * @author score
 */
public interface MappingPolicies {
    
    public Tree getTreeFromMapping(Gtree gt);
    
}
