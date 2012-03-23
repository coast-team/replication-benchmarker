/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package testMain;
import crdt.CRDT;
import crdt.Factory;
import crdt.simulator.CausalSimulator;
import crdt.simulator.Trace;
import crdt.simulator.random.RandomTrace;
import crdt.simulator.random.StandardSeqOpProfile;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.List;
import jbenchmarker.MainSimulation;
import org.junit.Test;
/**
 *
 * @author score
 */
public class mainSimulatorTest {        
        @Test
        public void mainSimulatorTest() throws Exception
        {
            //String logoot = "jbenchmarker.logoot.LogootFactory";
            //String rga = "jbenchmarker.rga.RGAFactory";
            String wootH = "jbenchmarker.woot.WootFactories$WootHFactory";
            
            String[] args = new String[] { wootH, "1", "200", "0.88", "2.39", "30", "64.0", "0.1", "50", "10","4","2","100" };
            MainSimulation mn = new MainSimulation();
            mn.main(args);
            
        }
}