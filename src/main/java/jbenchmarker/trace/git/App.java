/**
 * Replication Benchmarker
 * https://github.com/score-team/replication-benchmarker/
 * Copyright (C) 2012 LORIA / Inria / SCORE Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package jbenchmarker.trace.git;

import crdt.PreconditionException;
import crdt.simulator.CausalSimulator;
import crdt.simulator.IncorrectTraceException;
import java.io.IOException;
import jbenchmarker.factories.LogootFactory;
import jbenchmarker.factories.RGAFactory;
import jbenchmarker.factories.TreedocFactory;
import jbenchmarker.factories.WootFactories;
import org.eclipse.jgit.api.errors.GitAPIException;


/**
 * Hello world!
 *
 */
public class App {

    public static void main(String[] args) throws IOException, GitAPIException, IncorrectTraceException, PreconditionException {
        
//        GitExtraction.parseRepository(("/Users/urso/Rech/github/linux", "http://localhost:5984", "kernel/sched.c", true);
//

        GitTrace trace = GitTrace.create("/Users/urso/Rech/github/git",
                "http://localhost:5984", "Makefile", false);
        CausalSimulator cd = new CausalSimulator(new RGAFactory());
        cd.run(trace, false);
        
        
        
        
//        GitTrace couchTrace = GitTrace.create("/Users/urso/Rech/github/linux", "http://localhost:5984", "MAINTAINERS", false);
//        GitTrace couchTrace = GitTrace.create("/Users/urso/Rech/github/linux", "http://localhost:5984", "kernel/sched.c", false);
        

        System.out.println(cd.replicas.get(1).lookup());
    }
}
// git : 967 authors, 30000 commits: Total time: 5:46:52.821s 