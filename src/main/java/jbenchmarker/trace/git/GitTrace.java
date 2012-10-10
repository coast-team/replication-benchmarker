/**
 * Replication Benchmarker
 * https://github.com/score-team/replication-benchmarker/ Copyright (C) 2012
 * LORIA / Inria / SCORE Team
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package jbenchmarker.trace.git;

import collect.VectorClock;
import crdt.CRDT;
import crdt.simulator.Trace;
import crdt.simulator.TraceOperation;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import jbenchmarker.core.LocalOperation;
import jbenchmarker.core.Operation;
import jbenchmarker.core.SequenceOperation;
import jbenchmarker.trace.git.model.Commit;
import jbenchmarker.trace.git.model.Edition;
import jbenchmarker.trace.git.model.FileEdition;
import jbenchmarker.trace.git.model.Patch;
import org.eclipse.jgit.diff.DiffAlgorithm;
import org.eclipse.jgit.diff.EditList;
import org.eclipse.jgit.diff.RawText;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.patch.FileHeader;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.DbPath;
import org.ektorp.http.HttpClient;
import org.ektorp.http.StdHttpClient;
import org.ektorp.impl.StdCouchDbConnector;
import org.ektorp.impl.StdCouchDbInstance;

/**
 *
 * @author urso
 */
public class GitTrace implements Trace{
    static int editNb = 0;
    static int editSize = 0;
    
    
    private CommitCRUD commitCRUD;
    private PatchCRUD patchCRUD;
    private List<Commit> initCommit;
    private final DiffAlgorithm diffAlgorithm;

    /**
     * Creates a git extractor using a git directory a couch db URL and a file
     * path
     *
     * @param gitdir directory that contains ".git"
     * @param couchURL URL of couch BD
     * @param path a path in the gir repository
     * @param clean if true recreates db
     * @return a new git extractor
     * @throws IOException if git directory not accessible
     */
    public static GitTrace create(String gitdir, String couchURL, String path, boolean cleanDB) throws IOException {
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        Repository repo = builder.setGitDir(new File(gitdir + "/.git")).readEnvironment()
                .findGitDir().build();

        HttpClient httpClient = new StdHttpClient.Builder().url(couchURL).build();
        CouchDbInstance dbInstance = new StdCouchDbInstance(httpClient);
        String prefix = clearName(gitdir, path),
                co = prefix + "_commit", pa = prefix + "_patch";
        CouchDbConnector dbcc = new StdCouchDbConnector(co, dbInstance);
        CouchDbConnector dbcp = new StdCouchDbConnector(pa, dbInstance);
        CommitCRUD commitCRUD;
        PatchCRUD patchCRUD;

        if (cleanDB || !dbInstance.checkIfDbExists(new DbPath(co))
                || !dbInstance.checkIfDbExists(new DbPath(pa))) {
            clearDB(dbInstance, co);
            clearDB(dbInstance, pa);
            commitCRUD = new CommitCRUD(dbcc);
            patchCRUD = new PatchCRUD(dbcp);
            GitExtraction ge = new GitExtraction(repo, commitCRUD, patchCRUD, GitExtraction.defaultDiffAlgorithm, path);
            ge.parseRepository();
        } else {
            commitCRUD = new CommitCRUD(dbcc);
            patchCRUD = new PatchCRUD(dbcp);
        }
        return new GitTrace(commitCRUD, patchCRUD);
    }

    // TODO : Working view
    public GitTrace(CommitCRUD dbc, PatchCRUD dbp) {
        commitCRUD = dbc;
        patchCRUD = dbp;
        initCommit = commitCRUD.getAll();
        for (Iterator<Commit> it = initCommit.iterator(); it.hasNext();) {
            Commit commit = it.next();
            if (commit.parentCount() > 0) {
                it.remove();
            }
        }
        diffAlgorithm = GitExtraction.defaultDiffAlgorithm;
    }

    public GitTrace(CouchDbConnector db) {
        this(new CommitCRUD(db), new PatchCRUD(db));
    }

    List<Edition> diff(byte[] aRaw, byte[] bRaw) throws IOException {
        final RawText a = new RawText(aRaw);
        final RawText b = new RawText(bRaw);
        final EditList editList = diffAlgorithm.diff(RawTextComparator.DEFAULT, a, b);
        return GitExtraction.edits(editList, a, b);
    }

    

    static class MergeCorrection extends TraceOperation implements Serializable {

            transient Patch patch;
            transient Walker walker;
            transient GitTrace gitTrace;
            LocalOperation first;

            MergeCorrection(int replica, VectorClock VC, Patch merge, Walker walker,GitTrace gitTrace) {
                super(replica, new VectorClock(VC));
                getVectorClock().inc(replica);
                this.patch = merge;
                this.walker=walker;
                this.gitTrace=gitTrace;
            }

            /**
             * Introduce to the trace on-the-fly correction operations to obtain
             * the merge result.
             *
             * @param replica the replica that will originate the correction
             * @return the first edit of the correction operation
             */
            @Override
            public LocalOperation getOperation(/*CRDT replica*/) {
                return new LocalOperation() {
                    @Override
                    public LocalOperation adaptTo(CRDT replica) {

                        if (first == null) {
                            try {
                                List<Edition> l = gitTrace.diff(((String) replica.lookup()).getBytes(), patch.getRaws().get(0));
                                editNb += l.size();
                                for (Edition ed : l) {
                                    editSize += ed.getEndA() - ed.getBeginA() + ed.getEndB() - ed.getBeginB();
                                }                                
                                if (l.isEmpty()) {
                                    walker.currentVC.inc(replica.getReplicaNumber());
                                    first = SequenceOperation.noop(/*replica.getReplicaNumber(), getVectorClock()*/);
                                } else {
                                    walker.editions.addAll(l);
                                    first = walker.nextElement().getOperation().adaptTo(replica);
                                }
                            } catch (IOException ex) {
                                Logger.getLogger(GitTrace.class.getName()).log(Level.SEVERE, "During merge correction computation", ex);
                            }
                        }
                        return first;
                    }

                    @Override
                    public Operation clone() {
                        throw new UnsupportedOperationException("Not Yet fucked clone on trace");
                        //return first==null?(LocalOperation)super.clone():first;
                    }
                };
            }

            @Override
            public String toString() {
                return "MergeCorrection{super="+super.toString() + "first=" + first + '}';
            }

        }
     class Walker implements Enumeration<TraceOperation> {
        private LinkedList<Commit> pendingCommit;
        private LinkedList<Commit> startingCommit;
        private LinkedList<String> children;
        private LinkedList<FileEdition> files;
        private LinkedList<Edition> editions;
        private FileEdition fileEdit;
        private Commit commit;
        private VectorClock currentVC;
        private HashMap<String, VectorClock> startVC = new HashMap<String, VectorClock>();
        private HashSet<String> mergeCommit = new HashSet<String>();
        private boolean init = true;
        private TraceOperation next = null;
        private boolean finish = false;
        
        public Walker() {
            startingCommit = new LinkedList<Commit>(initCommit);
            pendingCommit = new LinkedList<Commit>(initCommit);
        }

        private TraceOperation next() {
            TraceOperation op = null;
            while (op == null && !finish) {
                if (editions != null && !editions.isEmpty()) {
                    Edition e = editions.pollFirst();
                    currentVC.inc(commit.getReplica());
                    op = new GitOperation(commit.getReplica(), currentVC, fileEdit, e);
                } else if (files != null && !files.isEmpty()) {
                    fileEdit = files.pollFirst();
                    if (fileEdit.getType() == FileHeader.PatchType.UNIFIED) {
                        editions = new LinkedList<Edition>(fileEdit.getListDiff());
                    }
                } else if (children != null && !children.isEmpty()) {
                    Patch p = patchCRUD.get(children.pollFirst() + commit.getId());
                    files = new LinkedList<FileEdition>(p.getEdits());
                } else if (init) {
                    if (commit != null) {
                        startVC.put(commit.getId(), currentVC);
                    }
                    if (!startingCommit.isEmpty()) {
                        // Treat content of commit without parent
                        commit = startingCommit.pollFirst();
                        Patch p = patchCRUD.get(commit.patchId());
                        files = new LinkedList<FileEdition>(p.getEdits());
                        currentVC = new VectorClock();
                    } else {
                        init = false;
                        commit = null;
                    }
                } else {
                    if (commit != null) {
                        // Remove itself from children (topological sort).
//System.out.println(commit.getId());
                        for (int i = 0; i < commit.childrenCount(); ++i) {
                            Commit child = foundPending(commit.getChildren().get(i));
                            child.getParents().remove(commit.getId());

                            // Update starting VC of future commits
                            VectorClock vc = startVC.get(child.getId());
                            if (vc == null) {
                                startVC.put(child.getId(), currentVC);
                            } else {
                                vc.upTo(currentVC);
                            }
                        }
                        commit = null;
                    }
                    if (pendingCommit.size() > 0) {
                        // Causality insurance 
                        Commit candidate = pendingCommit.removeFirst();
                        if (candidate.parentCount() > 0) {
                            pendingCommit.addLast(candidate);
                        } else {
                            commit = candidate;
                            pureChildren(commit.getChildren());
                            currentVC = startVC.get(commit.getId());
                            if (mergeCommit.contains(commit.getId())) {
                                op = new MergeCorrection(commit.getReplica(), currentVC, patchCRUD.get(commit.patchId()),this,GitTrace.this);
                            }
                        }
                    } else {
                        finish = true;
                    }
                }
            }
//System.out.println(commit);
            return op; 
        }
        
        @Override
        public boolean hasMoreElements() {
            if (next == null) {
                next = next();
            } 
            return !finish;
//            return (editions != null && !editions.isEmpty())
//                    || (files != null && !files.isEmpty())
//                    || (children != null && !children.isEmpty())
//                    || (!pendingCommit.isEmpty());
        }

        @Override
        public TraceOperation nextElement() {
            TraceOperation op;
            if (next == null) {
                op = next();
            } else {
                op = next;                
            }
            if (finish) {
                throw new NoSuchElementException("No more operation");
            }
            next = null;
            return op;
        }

        private Commit foundPending(String childId) {
            for (Commit c : pendingCommit) {
                if (c.getId().equals(childId)) {
                    return c;
                }
            }
            return null;
        }

        /**
         * Add to children only id which are not merge. Add unknown child to
         * pending. Identify merge commit.
         */
        private void pureChildren(List<String> childrenId) {
            children = new LinkedList<String>(childrenId);
            Iterator<String> it = children.iterator();
            while (it.hasNext()) {
                String cid = it.next();
                Commit child = foundPending(cid);
                if (child == null) {
                    child = commitCRUD.get(cid);
                    if (child.parentCount() > 1) {
                        mergeCommit.add(cid);
                    }
                    pendingCommit.addLast(child);
                }
                if (mergeCommit.contains(cid)) {
                    it.remove();
                }
            }
        }
    }

    @Override
    public Enumeration<TraceOperation> enumeration() {
        return new Walker();
    }

    public static void clearDB(CouchDbInstance dbInstance, String path) {
        if (dbInstance.checkIfDbExists(new DbPath(path))) {
            dbInstance.deleteDatabase(path);
        }
    }

    public static String clearName(String gitdir, String path) {
        String[] d = gitdir.split("/");
        gitdir = d[d.length - 1];
        path = path.replaceAll("[^a-zA-Z0-9]", "");
        return gitdir + "_" + path;
    }
}
