/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker.trace.git;

import collect.VectorClock;
import crdt.simulator.Trace;
import crdt.simulator.TraceOperation;
import java.util.*;
import jbenchmarker.trace.git.model.Commit;
import jbenchmarker.trace.git.model.Edition;
import jbenchmarker.trace.git.model.FileEdition;
import jbenchmarker.trace.git.model.Patch;
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
        private Iterator<Commit> startingCommit;
        private Iterator<String> currentChild;
        private String nextChild;
        private Iterator<FileEdition> currentFile;
        private Iterator<Edition> currentEdition;
        private FileEdition fileEdit;
        private Commit commit;
        private VectorClock currentVC;
        private HashMap<String, VectorClock> startVC = new HashMap<String, VectorClock>();
        private HashSet<String> allreadyGenerated = new HashSet<String>();
        private boolean init = true;
        
        public Walker() {
            startingCommit = initCommit.iterator();
            pendingCommit = new LinkedList<Commit>(initCommit);
        }

        @Override
        public boolean hasMoreElements() {
            return (currentEdition != null && currentEdition.hasNext()) 
                    || (currentFile != null && currentFile.hasNext()) 
                    || (nextChild != null) 
                    || (pendingCommit.size() > 0);
        }

        @Override
        public TraceOperation nextElement() {
            TraceOperation op = null;
            while (op == null) {
                if (currentEdition != null && currentEdition.hasNext()) {
                    Edition e = currentEdition.next();
                    currentVC.inc(commit.getReplica());
                    op = new GitOperation(fileEdit, e, commit.getReplica(), currentVC);
                } else if (currentFile != null && currentFile.hasNext()) {
                    fileEdit = currentFile.next();
                    currentEdition = fileEdit.getListDiff().iterator();
                } else if (nextChild != null) {
                    Patch p = patchCRUD.get(nextChild + commit.getId());
                    currentFile = p.getListEdit().iterator();
                    nextChild = nextNotGenerated();
                } else if (init) {
                    if (commit != null) {
                        startVC.put(commit.getId(), currentVC);
                    }
                    if (startingCommit.hasNext()) {
                        // Treat content of commit without parent
                        commit = startingCommit.next();
                        Patch p = patchCRUD.get(commit.patchId());
                        currentFile = p.getListEdit().iterator();
                        currentVC = new VectorClock(); 
                    } else {
                        init = false;
                        commit = null;
                    }
                } else {
                    if (commit != null) {
                        // Adds children to pending commits
                        for (int i = 0; i < commit.childrenCount(); ++i) {
                            Commit child = addIfNotPresent(commit.getChildren().get(i));
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
                            currentChild = commit.getChildren().iterator();
                            nextChild = nextNotGenerated();
                            currentVC = startVC.get(commit.getId());
                        }                   
                    } else throw new NoSuchElementException("No more operation");
                }
            }
System.out.println(commit);
            return op;
        }

        private Commit addIfNotPresent(String childId) {
            Commit child = null;
            for (Commit c : pendingCommit) {
                if (c.getId().equals(childId)) {
                    child = c;
                }
            }
            if (child == null) {
                child = commitCRUD.get(childId);
                pendingCommit.addLast(child);
            }
            return child;
        }

        private String nextNotGenerated() {
            String next = null;
            while (next == null && currentChild.hasNext()) {
                String child = currentChild.next();
                if (!allreadyGenerated.contains(child)) {
                    next = child;
                    allreadyGenerated.add(next);
                }
            }
            return next;
        }
    }
    
    @Override
    public Enumeration<TraceOperation> enumeration() {
        return new Walker();
    }
}
