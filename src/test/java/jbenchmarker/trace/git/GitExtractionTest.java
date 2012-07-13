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

import java.net.MalformedURLException;
import jbenchmarker.trace.git.model.Commit;
import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.DbPath;
import org.ektorp.http.HttpClient;
import org.ektorp.http.StdHttpClient;
import org.ektorp.impl.StdCouchDbConnector;
import org.ektorp.impl.StdCouchDbInstance;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;

/**
 *
 * @author urso
 */
public class GitExtractionTest {

    @Test
    public void storeCommitTest() throws MalformedURLException {
        HttpClient httpClient = new StdHttpClient.Builder().url("http://localhost:5984").build();

        CouchDbInstance dbInstance = new StdCouchDbInstance(httpClient);
        if (dbInstance.checkIfDbExists(new DbPath("test"))) {
            dbInstance.deleteDatabase("test");
        }
        CouchDbConnector db = new StdCouchDbConnector("test", dbInstance);
        db.createDatabaseIfNotExists();

        CommitCRUD commitCRUD = new CommitCRUD(db);
        
        Commit commit = new Commit();
        commit.setId("myid");
        commit.setMessage("coucou");
        commit.setReplica(42);
        commitCRUD.add(commit);
        
        Commit result = commitCRUD.get("myid");
        assertEquals("myid", result.getId());
        assertEquals("coucou", result.getMessage());
        assertEquals(42, result.getReplica());
        
        Commit result2 = commitCRUD.getAll().get(0);
        assertEquals("myid", result2.getId());
        assertEquals("coucou", result2.getMessage());
        assertEquals(42, result2.getReplica());
    }
    
    @Ignore
    @Test
    public void storeAnrRetrieve() throws MalformedURLException {
        HttpClient httpClient = new StdHttpClient.Builder().url("http://localhost:5984").build();

        CouchDbInstance dbInstance = new StdCouchDbInstance(httpClient);
        if (dbInstance.checkIfDbExists(new DbPath("test_commit"))) {
            dbInstance.deleteDatabase("test_commit");
        }
        CouchDbConnector db = new StdCouchDbConnector("test", dbInstance);
        db.createDatabaseIfNotExists();
        
        fail("Not implemented yet");
    }
}
