/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package testMain;
import jbenchmarker.Main;
import org.junit.Ignore;
import org.junit.Test;
/**
 *
 * @author score
 */
public class mainSimulatorTest {        
   
    @Ignore
    @Test
    public void mainTest() throws Exception {
        String logoot = "jbenchmarker.logoot.LogootFactory";
        //String rga = "jbenchmarker.rga.RGAFactory";
        //String wootH = "jbenchmarker.woot.WootFactories$WootHFactory";
        String trace = "../../traces/xml/SerieDoc2.xml";
        String[] args = new String[]{logoot, trace, "5", "2", "10"};
        Main mn = new Main();
        mn.main(args);
    }
    
    @Test
    public void playAndReplay() {
        
    }
}
