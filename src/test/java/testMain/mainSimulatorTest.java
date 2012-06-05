/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package testMain;
import crdt.CRDT;
import crdt.simulator.CausalSimulator;
import crdt.simulator.Trace;
import jbenchmarker.Main;
import jbenchmarker.MainSimulation;
import jbenchmarker.TraceSimul2XML;
import jbenchmarker.trace.TraceGenerator;
import jbenchmarker.woot.WootFactories.WootHFactory;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;
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
}
