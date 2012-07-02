/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker.trace.git;

import collect.VectorClock;
import crdt.CRDT;
import crdt.simulator.Trace;
import crdt.simulator.TraceOperation;
import java.util.*;
import jbenchmarker.core.Operation;
import jbenchmarker.core.SequenceOperation;
import jbenchmarker.trace.git.model.Commit;
import jbenchmarker.trace.git.model.Edition;
import jbenchmarker.trace.git.model.FileEdition;
import jbenchmarker.trace.git.model.Patch;
import org.eclipse.jgit.diff.RawText;
import org.ektorp.CouchDbConnector;

/**
 *
 * @author urso
 */
public class CouchTrace implements Trace {
    private CommitCRUD commitCRUD;
    private PatchCRUD patchCRUD;  
    private List<Commit> initCommit;
    
    // TODO : Working view
    public CouchTrace(CommitCRUD dbc, PatchCRUD dbp) {
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

    
    public CouchTrace(CouchDbConnector db) {
        this(new CommitCRUD(db), new PatchCRUD(db));
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
        
        public Walker() {
            startingCommit = new LinkedList<Commit>(initCommit);
            pendingCommit = new LinkedList<Commit>(initCommit);
        }

        @Override
        public boolean hasMoreElements() {
            return (editions != null && !editions.isEmpty()) 
                    || (files != null && !files.isEmpty()) 
                    || (children != null && !children.isEmpty()) 
                    || (!pendingCommit.isEmpty());
        }

        @Override
        public TraceOperation nextElement() {
            TraceOperation op = null;
            while (op == null) {
                if (editions != null && !editions.isEmpty()) {
                    Edition e = editions.pollFirst();
                    currentVC.inc(commit.getReplica());
                    op = new GitOperation(fileEdit, e, commit.getReplica(), currentVC);
                } else if (files != null && !files.isEmpty()) {
                    fileEdit = files.pollFirst();
                    editions = new LinkedList<Edition>(fileEdit.getListDiff());
                } else if (children != null && !children.isEmpty()) {
                    Patch p = patchCRUD.get(children.pollFirst() + commit.getId());
                    files = new LinkedList<FileEdition>(p.getListEdit());
                } else if (init) {
                    if (commit != null) {
                        startVC.put(commit.getId(), currentVC);
                    }
                    if (!startingCommit.isEmpty()) {
                        // Treat content of commit without parent
                        commit = startingCommit.pollFirst();
                        Patch p = patchCRUD.get(commit.patchId());
                        files = new LinkedList<FileEdition>(p.getListEdit());
                        currentVC = new VectorClock(); 
                    } else {
                        init = false;
                        commit = null;
                    }
                } else {
                    if (commit != null) {
                        // Adds children to pending commits
                        for (int i = 0; i < commit.childrenCount(); ++i) {
                            Commit child = foundPending(commit.getChildren().get(i));
                            VectorClock vc = startVC.get(child.getId());

                            child.getParents().remove(commit.getId());
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
                        }                   
                    } else throw new NoSuchElementException("No more operation");
                }
            }
System.out.println(commit);
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
         * Add to children only id which are not merge.
         * Add unknown child to pending. Identify merge commit.
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
}
