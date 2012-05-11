/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker.sim;

import crdt.simulator.CausalSimulator;
import crdt.simulator.Trace;
import crdt.simulator.random.RandomTrace;
import crdt.simulator.random.StandardSeqOpProfile;
import jbenchmarker.rga.RGAFactory;

/**
 *
 * @author urso
 */
public class Profiling {
    public static void main(String args[]) throws Exception {
        for (int i = 0; i < 3; ++i) {
            Trace trace = new RandomTrace(4200, RandomTrace.FLAT, new StandardSeqOpProfile(0.8, 0.1, 40, 5.0), 0.1, 10, 3.0, 13);
            CausalSimulator cd = new CausalSimulator(new RGAFactory());

            cd.run(trace, false);
            System.out.println(i);
        }    
    }
}
