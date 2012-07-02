/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker.trace.git;

import jbenchmarker.trace.git.model.Commit;
import jbenchmarker.trace.git.model.Patch;
import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.impl.StdCouchDbConnector;
import org.ektorp.support.CouchDbRepositorySupport;

/**
 *
 * @author urso
 */
public class PatchCRUD extends CouchDbRepositorySupport<Patch> {

    public PatchCRUD(CouchDbConnector db) {
        super(Patch.class, db);
    }
    
    public PatchCRUD(String prefix, CouchDbInstance db) {
        super(Patch.class, new StdCouchDbConnector(prefix + "_patch", db), true);
    }
}
