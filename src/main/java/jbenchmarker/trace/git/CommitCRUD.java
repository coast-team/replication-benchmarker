/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker.trace.git;

import java.util.List;
import jbenchmarker.trace.git.model.Commit;
import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.CouchDbRepositorySupport;
import org.ektorp.support.View;

/**
 *
 * @author urso
 */
public class CommitCRUD extends CouchDbRepositorySupport<Commit> {

    public CommitCRUD(CouchDbConnector db) {
        super(Commit.class, db);
    }
    
    @View(name = "init", map = "function(doc) { if (empty doc.parents) { emit(null, doc) } }")
    public List<Commit> getInit() {
        ViewQuery q = createQuery("init").descending(true);
        return db.queryView(q, Commit.class);
    }
}
