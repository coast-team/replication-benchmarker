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