/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker.ot;

import crdt.CRDTMessage;
import jbenchmarker.ot.otset.AddWinTransformation;
import jbenchmarker.ot.otset.DelWinTransformation;
import jbenchmarker.ot.otset.OTSet;
import jbenchmarker.ot.soct2.SOCT2TranformationInterface;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Stephane Martin <stephane.martin@loria.fr>
 */
public class OtSetTest {
    
    /*
     * 4 sites :
     * s1 : add 1(m1), del 1(m3) ; s2 add 1(m2)
     * s3 recieve (m1,m2,m3)
     * s4 recieve (m1,m3,m2)
     * s1 recieve m2
     * s2 recieve m1,m3
     */

    @Test
    public void OTSetTestAddWin() {
        SOCT2TranformationInterface ot = new AddWinTransformation();
        OTSet set1 = new OTSet(ot, 0);
        OTSet set2 = new OTSet(ot, 1);
        OTSet set3 = new OTSet(ot, 3);
        OTSet set4 = new OTSet(ot, 4);
        try {
            CRDTMessage m1 = set1.add(1);
            assertEquals(set1.contains(1), true);
            CRDTMessage m2 = set2.add(1);
            assertEquals(set2.contains(1), true);
            CRDTMessage m3 = set1.remove(1);
            assertEquals(set1.contains(1), false);

            /*
             * Scenario 
             */
            set3.applyRemote(m1.clone());
            assertEquals(set3.contains(1), true);
            set3.applyRemote(m2.clone());
            assertEquals(set3.contains(1), true);
            set3.applyRemote(m3.clone());
            assertEquals(set3.contains(1), true);

            /*
             * Scenario 2
             */
            set4.applyRemote(m1.clone());
            assertEquals(set4.contains(1), true);
            set4.applyRemote(m3.clone());
            assertEquals(set4.contains(1), false);
            set4.applyRemote(m2.clone());
            assertEquals(set4.contains(1), true);

            /*
             * injection dans les autres
             */
            set1.applyRemote(m2.clone());
            assertEquals(set1.contains(1), true);

            set2.applyRemote(m1.clone());
            assertEquals(set2.contains(1), true);
            set2.applyRemote(m3.clone());
            assertEquals(set2.contains(1), true);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    @Test
    public void OTSetTestDelWin() {
        SOCT2TranformationInterface ot = new DelWinTransformation();
        OTSet set1 = new OTSet(ot, 0);
        OTSet set2 = new OTSet(ot, 1);
        OTSet set3 = new OTSet(ot, 3);
        OTSet set4 = new OTSet(ot, 4);
        try {
            CRDTMessage m1 = set1.add(1);
            assertEquals(set1.contains(1), true);
            CRDTMessage m2 = set2.add(1);
            assertEquals(set2.contains(1), true);
            CRDTMessage m3 = set1.remove(1);
            assertEquals(set1.contains(1), false);

            /*
             * Scenario 1
             */
            set3.applyRemote(m1.clone());
            assertEquals(set3.contains(1), true);
            set3.applyRemote(m2.clone());
            assertEquals(set3.contains(1), true);
            set3.applyRemote(m3.clone());
            assertEquals(set3.contains(1), false);

            /*
             * Scenario 2
             */
            set4.applyRemote(m1.clone());
            assertEquals(set4.contains(1), true);
            set4.applyRemote(m3.clone());
            assertEquals(set4.contains(1), false);
            set4.applyRemote(m2.clone());
            assertEquals(set4.contains(1), false);

            /*
             * injection dans les autres
             */
            set1.applyRemote(m2.clone());
            assertEquals(set1.contains(1), false);

            set2.applyRemote(m1.clone());
            assertEquals(set2.contains(1), true);
            set2.applyRemote(m3.clone());
            assertEquals(set2.contains(1), false);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
}
