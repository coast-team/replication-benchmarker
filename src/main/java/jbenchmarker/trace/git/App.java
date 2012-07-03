package jbenchmarker.trace.git;

import crdt.PreconditionException;
import crdt.simulator.CausalSimulator;
import crdt.simulator.IncorrectTraceException;
import java.io.IOException;
import jbenchmarker.factories.LogootFactory;
import org.eclipse.jgit.api.errors.GitAPIException;


/**
 * Hello world!
 *
 */
public class App {

    public static void main(String[] args) throws IOException, GitAPIException, IncorrectTraceException, PreconditionException {
        
//        GitExtraction.parseRepository(("/Users/urso/Rech/github/linux", "http://localhost:5984", "kernel/sched.c", true);
//

        GitTrace couchTrace = GitTrace.create("/Users/urso/Rech/github/git", "http://localhost:5984", "Makefile", false);
//        GitTrace couchTrace = GitTrace.create("/Users/urso/Rech/github/linux", "http://localhost:5984", "MAINTAINERS", false);
//        GitTrace couchTrace = GitTrace.create("/Users/urso/Rech/github/linux", "http://localhost:5984", "kernel/sched.c", false);
        
        CausalSimulator cd = new CausalSimulator(new LogootFactory<String>());
        cd.run(couchTrace, false);
        System.out.println(cd.replicas.get(1).lookup());
    }
}
// git : 967 authors, 30000 commits: Total time: 5:46:52.821s 