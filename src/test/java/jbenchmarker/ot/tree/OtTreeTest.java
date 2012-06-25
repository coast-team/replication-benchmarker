/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker.ot.tree;

import crdt.Factory;
import crdt.tree.CRDTTree;
import crdt.tree.CrdtTreeGeneric;
import jbenchmarker.ot.ottree.OTTree;
import jbenchmarker.ot.ottree.OTTreeTranformation;
import jbenchmarker.ot.soct2.SOCT2;
import jbenchmarker.ot.soct2.SOCT2Log;
import org.junit.Test;

/**
 *
 * @author Stephane Martin <stephane.martin@loria.fr>
 */
public class OtTreeTest {

    @Test
    public void otTreeTestBasic() throws Exception {
        OTTree tree1 = new OTTree(new SOCT2(0, new SOCT2Log(new OTTreeTranformation()),null));
        OTTree tree2 = new OTTree(new SOCT2(1, new SOCT2Log(new OTTreeTranformation()),null));
        
    }
}
