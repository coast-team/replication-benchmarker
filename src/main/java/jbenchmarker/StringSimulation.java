/*
 *  Replication Benchmarker
 *  https://github.com/score-team/replication-benchmarker/
 *  Copyright (C) 2013 LORIA / Inria / SCORE Team
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package jbenchmarker;

import crdt.CRDT;
import crdt.Factory;
import crdt.PreconditionException;
import crdt.simulator.random.OperationProfile;
import crdt.simulator.random.SequenceOperationStupid;
import crdt.simulator.random.StandardSeqOpProfile;
import java.io.IOException;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.Option;

/**
 *
 * @author Stephane Martin <stephane@stephanemartin.fr>
 */
public class StringSimulation extends SimulationBase {
    String factoryName;
    Factory<CRDT> factory;
    @Option(name="-f", usage = "Factory name")
    private void factory(String arg) throws CmdLineException{
        this.factoryName=arg;
        try {
            factory= (Factory<CRDT>) Class.forName(arg).newInstance();
        } catch (ClassNotFoundException ex) {
           throw new CmdLineException(this.parser,"Factory not found " + ex);
        } catch (InstantiationException ex) {
           throw new CmdLineException(this.parser,"Factory not valid " + ex);
        } catch (IllegalAccessException ex) {
           throw new CmdLineException(this.parser,"Factory access is invalid " + ex);
        }
    }
    
     @Option(name = "-A", usage = "Generate Add/del Trace -A perIns,perBlock,avgBlockSize,sdvBlockSize,duration (without spaces)",metaVar = "perIns,perBlock,avgBlockSize,sdvBlockSize,duration")
    private void genAdddel(String param) throws CmdLineException {
        try {
            param = param.replace(")", "");
            param = param.replace("(", "");
            String[] params = param.split(",");
            double perIns = Double.parseDouble(params[0]);
            double perBlock = Double.parseDouble(params[1]);
            int avgBlockSize = Integer.parseInt(params[2]);
            double sdvBlockSize = Double.parseDouble(params[3]);
            int duration = Integer.parseInt(params[4]);

            totalDuration += duration;
            //System.out.println("Generation Add/Del Trace \n"+duration+" ops with:\n"+perIns+"prob insert and \n"+perChild+"prob use child");
            OperationProfile opprof = new StandardSeqOpProfile(perIns, perBlock, avgBlockSize, sdvBlockSize);
            randomTrace.add(traceP.makeRandomTrace(duration, opprof));
        } catch (Exception ex) {
            throw new CmdLineException(this.parser,"-A parameter is invalid " + ex);
        }
    }
   
     
     @Option(name = "-X", usage = "Generate Add/del Trace -A perIns,perBlock,avgBlockSize,sdvBlockSize,duration (without spaces)",metaVar = "perIns,perBlock,avgBlockSize,sdvBlockSize,duration")
    private void genAdddelS(String param) throws CmdLineException {
        try {
            param = param.replace(")", "");
            param = param.replace("(", "");
            String[] params = param.split(",");
            double perIns = Double.parseDouble(params[0]);
            double perBlock = Double.parseDouble(params[1]);
            int avgBlockSize = Integer.parseInt(params[2]);
            double sdvBlockSize = Double.parseDouble(params[3]);
            int duration = Integer.parseInt(params[4]);

            totalDuration += duration;
            //System.out.println("Generation Add/Del Trace \n"+duration+" ops with:\n"+perIns+"prob insert and \n"+perChild+"prob use child");
            OperationProfile opprof = new SequenceOperationStupid(perIns, perBlock, avgBlockSize, sdvBlockSize);
            randomTrace.add(traceP.makeRandomTrace(duration, opprof));
        } catch (Exception ex) {
            throw new CmdLineException(this.parser,"-A parameter is invalid " + ex);
        }
    }
     
    public StringSimulation(String... arg) {
        super(arg);
    }

    public static void main(String... arg) throws IOException, PreconditionException {
        StringSimulation sim = new StringSimulation(arg);
        sim.run();
        sim.writeFiles();
    }

    @Override
    Factory<CRDT> getFactory() {
       return factory;
    }

   

    @Override
    String getDefaultPrefix() {
        return factoryName;
    }
}
