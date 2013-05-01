/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker.trace.git;

import Tools.ExecTools;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Stephane Martin <stephane@stephanemartin.fr>
 */
public class ExecToolsTest {

    public ExecToolsTest() {
    }

    @Test(expected = TimeoutException.class)
    public void testLaunchAndWaitTimeOut() throws Exception {
        
        ExecTools.launchAndWait("sleep 20", "/", 500);
    }

    @Test
    public void testLaunchAndWait() throws Exception {
        ExecTools.launchAndWait("sleep 1", "/", 1500);
    }
    /*@Test todo find a test for all OS
    public void testLaunchOutput() throws Exception {
        ExecTools.launchAndWait("dmesg", "/", 1000);
    }*/
    
}
