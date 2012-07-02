package jbenchmarker.trace.git;
import crdt.PreconditionException;
import crdt.simulator.CausalSimulator;
import crdt.simulator.IncorrectTraceException;
import crdt.simulator.TraceOperation;
import jbenchmarker.trace.git.CouchTrace;
import jbenchmarker.trace.git.GitExtraction;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import jbenchmarker.factories.LogootFactory;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.http.HttpClient;
import org.ektorp.http.StdHttpClient;
import org.ektorp.impl.StdCouchDbConnector;
import org.ektorp.impl.StdCouchDbInstance;
import org.ektorp.support.CouchDbRepositorySupport;


/**
 * Hello world!
 *
 */
public class App {

    public static void main(String[] args) throws IOException, GitAPIException, IncorrectTraceException, PreconditionException {
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        Repository repository = builder.setGitDir(new File("/Users/urso/Rech/github/git" + "/.git")).readEnvironment() // scan environment GIT_* variables
                .findGitDir() // scan up the file system tree
                .build();

        HttpClient httpClient = new StdHttpClient.Builder()
                                .url("http://localhost:5984")
                                .build();

        CouchDbInstance dbInstance = new StdCouchDbInstance(httpClient);
        
        final String prefix = "git_makefile";
        CommitCRUD commitCRUD = new CommitCRUD(prefix, dbInstance);
        PatchCRUD patchCRUD = new PatchCRUD(prefix, dbInstance);
        //        CouchDbConnector db = new StdCouchDbConnector("git_makefile", dbInstance);
        //        db.createDatabaseIfNotExists();
        //        httpClient.post("/git_makefile/xxx",
        //                " { \"_id\": \"xxx\","
        //                + "\"_rev\": \"12345\","
        //                + "\"language\": \"javascript\","
        //                + "\"views\":"
        //                + "{ \"all\": { \"map\": \"function(doc) { if (doc.message)  emit(null, doc) }\"}}}");

//        GitExtraction ge = new GitExtraction(repository, commitCRUD, patchCRUD);
//        ge.parseRepository("Makefile");

        CouchTrace couchTrace = new CouchTrace(commitCRUD, patchCRUD);
        CausalSimulator cd = new CausalSimulator(new LogootFactory<String>());
        cd.run(couchTrace, false);
    }
}
// git : 967 authors, 30000 commits: Total time: 5:46:52.821s 