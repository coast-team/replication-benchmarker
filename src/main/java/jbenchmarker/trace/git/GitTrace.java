/**
 * Replication Benchmarker
 * https://github.com/score-team/replication-benchmarker/ Copyright (C) 2013
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
import org.ektorp.impl.StdCouchDbConnector;

/**
 *
 * @author urso
 */
public class GitTrace implements Trace {
    /* Statistics */

    public int nbBlockMerge = 0;
    public int mergeSize = 0;
    public int nbMerge = 0;
    public int nbCommit = 0;
    public int nbInsBlock = 0;
    public int nbDelBlock = 0;
    public int nbUpdBlock = 0;
    public int nbReplace = 0;
    public int nbMove = 0;
    public int insertSize = 0;
    public int deleteSize = 0;
    public int advance = 0;
    
    private CommitCRUD commitCRUD;
    private PatchCRUD patchCRUD;
    private List<Commit> initCommit;
    private static final DiffAlgorithm diffAlgorithm = GitExtraction.defaultDiffAlgorithm;
    static final boolean DEBUG = false;
    static public int UpdBefore = 0, MoveBefore = 0, MergeBefore=0;
    private final boolean detectMoveAndUpdate;
    private final int updateThresold;
    private final int moveThresold;

    /**
     * Produces a git trace using a git directory a couch db URL and a file
     * path. Operations are insert, delete, and replace (block of lines).
     *
     * @param gitdir directory that contains ".git"
     * @param couchURL URL of couch BD
     * @param path a path in the gir repository
     * @param clean if true recreates db
     * @return a new git extractor
     * @throws IOException if git directory not accessible
     */
    public static GitTrace create(String gitdir, CouchConnector cc, String path, boolean cleanDB) throws IOException {
        return create(gitdir, cc, path, cleanDB, false, 0, 0);
    }

    /**
     * Produces a git trace with move and update operations using a git
     * directory a couch db URL and a file path. Operations are insert, delete,
     * update and move (block of lines).
     *
     * @param gitdir directory that contains ".git"
     * @param couchURL URL of couch BD
     * @param path a path in the gir repository
     * @param clean if true recreates db
     * @param updateThresold difference percentage thresold to detect an update
     * @param moveThresold difference percentage thresold to detect a move
     * @return a new git extractor
     * @throws IOException if git directory not accessible
     */
    public static GitTrace createWithMoves(String gitdir, CouchConnector cc, String path, boolean cleanDB,
            int updateThresold, int moveThresold) throws IOException {
        return create(gitdir, cc, path, cleanDB, true, updateThresold, moveThresold);
    }

    public static GitTrace create(String gitdir, CouchConnector cc, String path, boolean cleanDB, boolean detectMaU, int ut, int mt) throws IOException {
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        Repository repo = builder.setGitDir(new File(gitdir + "/.git")).readEnvironment()
                .findGitDir().build();
        CouchDbInstance dbInstance = cc.getDbInstance();

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
            GitExtraction ge = new GitExtraction(repo, commitCRUD, patchCRUD, GitExtraction.defaultDiffAlgorithm, path, detectMaU, ut, mt);
            ge.parseRepository();
            UpdBefore = ge.nbUpdBlockBefore;
            MoveBefore = ge.nbMoveBefore;
            MergeBefore= ge.nbrMergeBefore;
        } else {
            commitCRUD = new CommitCRUD(dbcc);
            patchCRUD = new PatchCRUD(dbcp);
        }

        return new GitTrace(commitCRUD, patchCRUD, detectMaU, ut, mt);
    }

    // TODO : Working view
    public GitTrace(CommitCRUD dbc, PatchCRUD dbp, boolean detectMovesAndUpdates, int updateThresold, int moveThresold) {
        this.detectMoveAndUpdate = detectMovesAndUpdates;
        this.updateThresold = updateThresold;
        this.moveThresold = moveThresold;
        commitCRUD = dbc;
        patchCRUD = dbp;
        initCommit = commitCRUD.getAll();
        for (Iterator<Commit> it = initCommit.iterator(); it.hasNext();) {
            Commit commit = it.next();
            if (commit.parentCount() > 0) {
                it.remove();
            }
        }
    }

    static List<Edition> diff(byte[] aRaw, byte[] bRaw) {
        final RawText a = new RawText(aRaw);
        final RawText b = new RawText(bRaw);
        final EditList editList = diffAlgorithm.diff(RawTextComparator.DEFAULT, a, b);
        return GitExtraction.edits(editList, a, b);
    }

    static List<Edition> diff(String a, String b) {
        return diff(a.getBytes(), b.getBytes());
    }

    private void stat(List<Edition> l, boolean merge) {

        if (merge) {
            nbBlockMerge += l.size();
        }
        for (Edition ed : l) {
            if (merge) {
                mergeSize += ed.sizeA() + ed.sizeB();
            }
            insertSize += ed.sizeA();
            deleteSize += ed.sizeB();
            switch (ed.getType()) {
                case update:
                    ++nbUpdBlock;
                    break;
                case move:
                    ++nbMove;
                    break;
                case replace:
                    ++nbReplace;
                    break;
                case insert:
                    ++nbInsBlock;
                    break;
                case delete:
                    ++nbDelBlock;
                    break;
            }
        }
    }

    /**
     * To check if state resulting from local operation correspond to git stored
     * state.
     */
    private static final class Check extends TraceOperation implements Serializable {

        transient Commit commit;
        transient Walker walker;
        transient String target;

        private Check(int replica, VectorClock VC, Commit commit, Walker walker, String target) {
            super(replica, new VectorClock(VC));
            getVectorClock().inc(replica);
            this.commit = commit;
            this.walker = walker;
            this.target = target;
        }

        @Override
        public LocalOperation getOperation() {
            return new LocalOperation() {
                @Override
                public LocalOperation adaptTo(CRDT replica) {
//add new line at the end of the document
                    //System.out.println("Lookup : "+replica.lookup().toString().length()+", target : "+target.length());
                    if (replica.lookup().equals(target)) {
                        walker.currentVC.inc(replica.getReplicaNumber());
                        return SequenceOperation.noop();
                    } else {
                        throw new RuntimeException("=== INCORRECT LOCAL OPERATION ---- FROM " + commit.getParents()
                                + "--- TO : " + commit.patchId() + "=== Lookup\n" + replica.lookup() + "\n\n=== Target\n" + target
                                + "=== DIFF\n" + diff(replica.lookup().toString(), target));
                    }
                }

                @Override
                public Operation clone() {
                    throw new UnsupportedOperationException("Not supported yet.");
                }
            };
        }
    }

    public static class MergeCorrection extends TraceOperation implements Serializable {

        transient Patch patch;
        transient Walker walker;
        transient GitTrace gitTrace;
        LocalOperation first;
        transient final String targetID;
        transient final String target;

        MergeCorrection(int replica, VectorClock VC, String targetID, Walker walker, GitTrace gitTrace) {
            super(replica, new VectorClock(VC));
            getVectorClock().inc(replica);
            this.targetID = targetID;
            this.target = gitTrace.patchCRUD.get(targetID).getContents().get(0);
            this.walker = walker;
            this.gitTrace = gitTrace;
        }

        /**
         * Introduce to the trace on-the-fly correction operations to obtain the
         * merge result.
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
//System.out.println("----- REPLICA -----\n" + replica.lookup());                                
//System.out.println("----- PATCH -----\n" + target); 
                        walker.merges.put(targetID, replica.lookup().toString());
                        List<Edition> l = diff(replica.lookup().toString(), target);
                        if (gitTrace.detectMoveAndUpdate) {
                            l = GitExtraction.detectMovesAndUpdates(l, gitTrace.updateThresold, gitTrace.moveThresold);
                        }
                        gitTrace.stat(l, true);
//for (Edition ed : l) { System.out.println("--- DIFF ---\n" + ed); }                                
                        if (l.isEmpty()) {
                            walker.currentVC.inc(replica.getReplicaNumber());
                            first = SequenceOperation.noop();
                        } else {
                            walker.editions.addAll(l);
                            first = walker.nextElement().getOperation().adaptTo(replica);
                        }
                    }
                    return first;
                }

                @Override
                public Operation clone() {
                    throw new UnsupportedOperationException("Not yet clone on trace");
                    //return first==null?(LocalOperation)super.clone():first;
                }
            };
        }

        @Override
        public String toString() {
            return "MergeCorrection{super=" + super.toString() + "first=" + first + '}';
        }
    }

    class Walker implements Enumeration<TraceOperation> {

        private LinkedList<Commit> pendingCommit;
        private LinkedList<Commit> startingCommit;
        private LinkedList<FileEdition> files;
        private LinkedList<Edition> editions;
        private FileEdition fileEdit;
        private Commit commit;
        private VectorClock currentVC;
        private HashMap<String, VectorClock> startVC = new HashMap<String, VectorClock>();
        private HashSet<String> treated = new HashSet<String>();
        private TraceOperation next = null;
        private boolean finish = false;
        private boolean check;
        private Map<String, String> merges = new HashMap<String, String>();

        public Walker() {
            startingCommit = new LinkedList<Commit>(initCommit);
            pendingCommit = new LinkedList<Commit>();
        }

        private TraceOperation next() {
            TraceOperation op = null;
            while (op == null && !finish) {
                if (editions != null && !editions.isEmpty()) {
                    Edition e = editions.pollFirst();
                    currentVC.inc(commit.getReplica());
                    op = new GitOperation(commit.getReplica(), currentVC, fileEdit, e);
                    check = true;
                } else if (files != null && !files.isEmpty()) {
                    fileEdit = files.pollFirst();
                    if (fileEdit.getType() == FileHeader.PatchType.UNIFIED) {
                        editions = new LinkedList<Edition>(fileEdit.getListDiff());
                    }
//System.out.println(commit.patchId() + "\n" + editions);
//                    advanceMerge(commit);
                    
                    stat(editions, false);
                } else { // Commit finished
                    if (commit != null) { // Not first iteration
                        if (DEBUG && check) { // Check commit state
                            check = false;
                            return new Check(commit.getReplica(), currentVC, commit, this, patchCRUD.get(commit.patchContent()).getContentOf(fileEdit.getPath()));
                        }
                        ++nbCommit;
                        treated.add(commit.getId());
                        for (String cid : commit.getChildren()) {
                            if (!found(pendingCommit, cid)) {
                                Commit child = commitCRUD.get(cid);
                                pendingCommit.add(child);
                            }
                            // Update starting VC of future commits
                            VectorClock vc = startVC.get(cid);
                            if (vc == null) {
                                startVC.put(cid, new VectorClock(currentVC));
                            } else {
                                vc.upTo(currentVC);
                            }
                        }
                    }
                    if (!startingCommit.isEmpty()) {
                        // Treat content of commit without parent
                        commit = startingCommit.pollFirst();
                        Patch p = patchCRUD.get(commit.patchId());
                        files = new LinkedList<FileEdition>(p.getEdits());
                        currentVC = new VectorClock();
                    } else if (pendingCommit.size() > 0) {
                        // Causality insurance 
                        Commit candidate = pendingCommit.removeFirst();
                        while (!treated.containsAll(candidate.getParents())) {
                            pendingCommit.addLast(candidate);
                            candidate = pendingCommit.removeFirst();
                        }
                        commit = candidate;
                        currentVC = startVC.get(commit.getId());
                        if (commit.parentCount() > 1) {
                            ++nbMerge;
                            // TODO : treat several files 
                            op = new MergeCorrection(commit.getReplica(), currentVC, commit.patchContent(), this, GitTrace.this);
                        } else {
                            Patch p = patchCRUD.get(commit.parentPatchId(0));
                            files = new LinkedList<FileEdition>(p.getEdits());
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

        private boolean found(LinkedList<Commit> pendingCommit, String cid) {
            for (Commit c : pendingCommit) {
                if (c.getId().equals(cid)) {
                    return true;
                }
            }
            return false;
        }

        // Check if computed merge was better than commited one 
        private void advanceMerge(Commit commit) {
            if (commit.parentCount() == 1) {
                Commit parent = commitCRUD.get(commit.getParents().get(0));
                if (parent.parentCount() > 1) {
                    Patch patch = patchCRUD.get(commit.patchContent());
                    if (!patch.getContents().isEmpty()) {
                        String son = patch.getContents().get(0),
                                commited = patchCRUD.get(parent.patchContent()).getContents().get(0),
                                merge = merges.get(parent.patchContent());
                        if (diff(commited, son).size() > diff(merge, son).size()) {
                            ++advance;
                        }
                    }
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
        path = path.toLowerCase().replaceAll("[^a-z0-9]", "\\$");
        return gitdir + "_" + path;
    }
}