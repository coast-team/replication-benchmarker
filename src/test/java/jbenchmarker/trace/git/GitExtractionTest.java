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
import jbenchmarker.core.SequenceOperation;
import jbenchmarker.trace.git.model.Commit;
import jbenchmarker.trace.git.model.Edition;
import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.DbPath;
import org.ektorp.http.HttpClient;
import org.ektorp.http.StdHttpClient;
import org.ektorp.impl.StdCouchDbConnector;
import org.ektorp.impl.StdCouchDbInstance;
import org.junit.Test;
import static org.junit.Assert.*;
import static collect.Utils.*;
import jbenchmarker.core.SequenceOperation.OpType;
import java.util.List;
import org.junit.Ignore;

/**
 *
 * @author urso
 */
public class GitExtractionTest {

    public static boolean sorted(List<Edition> l) {
        int m = Integer.MAX_VALUE;
        for (Edition e : l) {
            if (e.getBeginA() > m || (e.getBeginA() == m && e.getType() != OpType.insert)) {
                return false;
            }
            m = e.getBeginA();
        }
        return true;
    }
    
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
    
    String A = "AAAAAAAAAAAAAAAAA", 
            B= "BBBBBBBBBBBBBBBBB",
            C= "CCCCCCCCCCCCCCCCC",
            X= "XXXXXXXXXXXXXXXXX",
            Y= "YYYYYYYYYYYYYYYYY",
            Z= "ZZZZZZZZZZZZZZZZZ",
            Aa = "AAAAAAAAAAAAAAAAAx", 
            Ba = "BBBBBBBBBBBBBBBBBx";
    
    @Test 
    public void detectNone() {
        GitExtraction ge = new GitExtraction(50, 20, 10);
        Edition e = new Edition(OpType.replace, 42, 44, 33, 36, toList(A, B), toList(X, Y, Z)),
                r1 = new Edition(OpType.delete, 42, 44, 33, 33, toList(A, B), null),
                r2 = new Edition(OpType.insert, 42, 42, 33, 36, null, toList(X, Y, Z));
        List<Edition> result = ge.detectMovesAndUpdates(toList(e));
        
        assertEquals(toList(r1, r2), result);
    }
    
    @Test 
    public void detectUpdate() {
        GitExtraction ge = new GitExtraction(50, 20, 10);
        Edition e = new Edition(OpType.replace, 42, 44, 33, 35, toList(A, B), toList(Aa, Ba)),
                r = new Edition(OpType.update, 42, 44, 33, 35, toList(A, B), toList(Aa, Ba));
        List<Edition> result = ge.detectMovesAndUpdates(toList(e));
        
        assertEquals(toList(r), result);
    }
    
    @Test 
    public void detectPartialUpdate() {
        GitExtraction ge = new GitExtraction(50, 20, 10);
        Edition e = new Edition(OpType.delete, 55, 56, 43, 43, toList(X), null),
                f = new Edition(OpType.replace, 42, 44, 33, 36, toList(A, B), toList(Y, Aa, Z)),
                
                r1 = new Edition(OpType.delete, 55, 56, 43, 43, toList(X), null),
                r2 = new Edition(OpType.delete, 43, 44, 36, 36, toList(B), null),
                r3 = new Edition(OpType.insert, 43, 43, 35, 36, null, toList(Z)),
                r4 = new Edition(OpType.update, 42, 43, 34, 35, toList(A), toList(Aa)),
                r5 = new Edition(OpType.insert, 42, 42, 33, 34, null, toList(Y));
        List<Edition> result = ge.detectMovesAndUpdates(toList(e, f));

        assertTrue(sorted(result));
        assertEquals(toList(r1, r2, r3, r4, r5), result);
    }

    @Test 
    public void detectPureMove() {
        GitExtraction ge = new GitExtraction(50, 20, 10);
        Edition e = new Edition(OpType.delete, 55, 57, 43, 43, toList(A, B), null),
                f = new Edition(OpType.insert, 42, 42, 33, 35, null, toList(A, B)),
                
                r = new Edition(OpType.move, 55, 57, 42, 44, toList(A, B), toList(A, B));
        List<Edition> result = ge.detectMovesAndUpdates(toList(e, f));

        assertEquals(toList(r), result);
    }
    
    @Test 
    public void detectMove() {
        GitExtraction ge = new GitExtraction(50, 20, 10);
        Edition e = new Edition(OpType.delete, 55, 57, 43, 43, toList(A, Ba), null),
                f = new Edition(OpType.insert, 42, 42, 33, 35, null, toList(Aa, B)),
                
                r = new Edition(OpType.move, 55, 57, 42, 44, toList(A, Ba), toList(Aa, B));
        List<Edition> result = ge.detectMovesAndUpdates(toList(e, f));

        assertEquals(toList(r), result);
    }
    
    @Test 
    public void detectPartialMoveDown() {
        GitExtraction ge = new GitExtraction(50, 20, 10);
        Edition e = new Edition(OpType.replace, 55, 59, 43, 44, toList(X, A, Ba, Y), toList(Z)),
                f = new Edition(OpType.insert, 42, 42, 33, 35, null, toList(A, B)),
                
                r1 = new Edition(OpType.delete, 58, 59, 45, 45, toList(Y), null), // 45 instead of 44 
                r2 = new Edition(OpType.move, 56, 58, 42, 44, toList(A, Ba), toList(A, B)),
                r3 = new Edition(OpType.delete, 57, 58, 43, 43, toList(X), null),
                r4 = new Edition(OpType.insert, 57, 57, 43, 44, null, toList(Z));
        List<Edition> result = ge.detectMovesAndUpdates(toList(e, f));

        assertEquals(toList(r1, r2, r3, r4), result);
    }

    @Test 
    public void detectPartialMoveUp() {
        GitExtraction ge = new GitExtraction(50, 20, 10);
        Edition e = new Edition(OpType.replace, 55, 59, 73, 74, toList(X, A, Ba, Y), toList(Z)),
                f = new Edition(OpType.insert, 62, 62, 83, 87, null, toList(A, B, C, Z)),
                
                r0 = new Edition(OpType.insert, 62, 62, 85, 87, null, toList(C, Z)),
                r1 = new Edition(OpType.delete, 58, 59, 77, 77, toList(Y), null),
                r2 = new Edition(OpType.move, 56, 58, 59, 61, toList(A, Ba), toList(A, B)),
                r3 = new Edition(OpType.delete, 55, 56, 73, 73, toList(X), null),
                r4 = new Edition(OpType.insert, 55, 55, 73, 74, null, toList(Z));
        List<Edition> result = ge.detectMovesAndUpdates(toList(f, e));

        assertEquals(toList(r0, r1, r2, r3, r4), result);
    }
}
