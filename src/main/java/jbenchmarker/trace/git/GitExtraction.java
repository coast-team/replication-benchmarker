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

import jbenchmarker.core.SequenceOperation.OpType;
import org.eclipse.jgit.diff.Edit.Type;
import collect.HashMapSet;
import java.io.IOException;
import java.util.*;
import jbenchmarker.trace.git.model.Commit;
import jbenchmarker.trace.git.model.Edition;
import jbenchmarker.trace.git.model.FileEdition;
import jbenchmarker.trace.git.model.Patch;
import name.fraser.neil.plaintext.DiffMatchPatch;
import name.fraser.neil.plaintext.DiffMatchPatch.Diff;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.diff.*;
import static org.eclipse.jgit.diff.DiffEntry.Side.NEW;
import static org.eclipse.jgit.diff.DiffEntry.Side.OLD;
import org.eclipse.jgit.errors.AmbiguousObjectException;
import org.eclipse.jgit.errors.CorruptObjectException;
import org.eclipse.jgit.errors.LargeObjectException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.*;
import static org.eclipse.jgit.lib.FileMode.GITLINK;
import org.eclipse.jgit.patch.FileHeader;
import org.eclipse.jgit.patch.FileHeader.PatchType;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevSort;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.pack.PackConfig;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.AndTreeFilter;
import org.eclipse.jgit.treewalk.filter.PathFilter;
import org.eclipse.jgit.treewalk.filter.TreeFilter;
import org.ektorp.CouchDbConnector;
import org.ektorp.support.CouchDbRepositorySupport;
import org.ektorp.support.GenericRepository;


/**
 * Extract FileEdition objects from a git repository.
 */
public class GitExtraction {

  
    private static final int binaryFileThreshold = PackConfig.DEFAULT_BIG_FILE_THRESHOLD;
    /**
     * Magic return content indicating it is empty or no content present.
     */
    public static final byte[] EMPTY = new byte[]{};
    /**
     * Magic return indicating the content is binary.
     */
    public static final byte[] BINARY = new byte[]{};
    
    private final Repository repository;
    private final ObjectReader reader;
    private final ContentSource source;
    private final ContentSource.Pair pairSource;
    private final DiffAlgorithm diffAlgorithm;
    private final GenericRepository<Patch> patchCrud;
    private final GenericRepository<Commit> commitCrud;
    private final Git git;   
    private final String path;
    public static final DiffAlgorithm defaultDiffAlgorithm = DiffAlgorithm.getAlgorithm(DiffAlgorithm.SupportedAlgorithm.MYERS);
    private static final DiffMatchPatch neil = new DiffMatchPatch();
    private boolean detectMaU;
                
    public GitExtraction(Repository repo, CouchDbRepositorySupport<Commit> dbc,  
            CouchDbRepositorySupport<Patch> dbp, DiffAlgorithm diffAlgorithm, String path) {
        this(repo, dbc, dbp, diffAlgorithm, path, false);
    }
    
    public GitExtraction(Repository repo, CouchDbRepositorySupport<Commit> dbc,  
            CouchDbRepositorySupport<Patch> dbp, DiffAlgorithm diffAlgorithm, String path, boolean detectMaU) {
        this.repository = repo;
        this.reader = repo.newObjectReader();
   	this.source = ContentSource.create(reader);
	this.pairSource = new ContentSource.Pair(source, source);
        this.diffAlgorithm = diffAlgorithm;
        this.patchCrud = dbp;
        this.commitCrud = dbc;
        this.git = new Git(repo);
        this.path = path;
        this.detectMaU = detectMaU;
    }
    
    // TODO : resolve problem of diff docuement in same DB
    private GitExtraction(Repository repo, CouchDbConnector db ) {
        this(repo, new CommitCRUD(db), new PatchCRUD(db), defaultDiffAlgorithm, "");
    }
    
    private byte[] getBytes(ObjectLoader ldr, ObjectId id) throws IOException {
        try {
            return ldr.getBytes(binaryFileThreshold);
        } catch (LargeObjectException.ExceedsLimit overLimit) {
            return BINARY;
        } catch (LargeObjectException.ExceedsByteArrayLimit overLimit) {
            return BINARY;
        } catch (LargeObjectException.OutOfMemory tooBig) {
            return BINARY;
        } catch (LargeObjectException tooBig) {
            tooBig.setObjectId(id);
            throw tooBig;
        }    
    }    
    
    private byte[] open(String path, ObjectId id) throws IOException {
        ObjectLoader ldr = source.open(path, id);
        return getBytes(ldr, id);
    }

    private byte[] open(DiffEntry.Side side, DiffEntry entry) throws IOException {
        if (entry.getMode(side) == FileMode.MISSING) {
            return EMPTY;
        }

        if (entry.getMode(side).getObjectType() != Constants.OBJ_BLOB) {
            return EMPTY;
        }

        AbbreviatedObjectId id = entry.getId(side);
        if (!id.isComplete()) {
            Collection<ObjectId> ids = reader.resolve(id);
            if (ids.size() == 1) {
//                id = AbbreviatedObjectId.fromObjectId(ids.iterator().next());
//                switch (side) {
//                    case OLD:
//                        entry.oldId = id;
//                        break;
//                    case NEW:
//                        entry.newId = id;
//                        break;
//                }
            } else if (ids.isEmpty()) {
                throw new MissingObjectException(id, Constants.OBJ_BLOB);
            } else {
                throw new AmbiguousObjectException(id, ids);
            }
        }
        ObjectLoader ldr = pairSource.open(side, entry);
        return getBytes(ldr, id.toObjectId());
    }
        
    static public List<Edition> edits(final EditList edits, final RawText a, final RawText b)
            throws IOException {
        List<Edition> editions = new ArrayList<Edition>();
        for (int curIdx = 0; curIdx < edits.size(); ++curIdx) {
            editions.add(0, new Edition(edits.get(curIdx), a, b));
        }
        return editions;
    }
  
    List<Edition> diff(byte[] aRaw, byte[] bRaw) throws IOException {
        final RawText a = new RawText(aRaw);
        final RawText b = new RawText(bRaw);
        final EditList editList = diffAlgorithm.diff(RawTextComparator.DEFAULT, a, b);
        return edits(editList, a, b);
    }
    
    private static final int UPDATE_THRESHOLD = 15;
    private static final int LINE_UPDATE_THRESHOLD = 50;
    private static final int MOVE_UPDATE_THRESHOLD = 10; 
    
    /**
     * Detects moves and updates. Replace couples delete/insert by update or moves.
     * @param edits the edit list to parse
     */
    private void detectMovesAndUpdates(List<Edition> edits) {
        List<String> sa = new ArrayList<String>(), sb = new ArrayList<String>();
        for (Edition e : edits) {
            sa.add(stringer(e.getCa()));
            sb.add(stringer(e.getCb()));
        }
        for (int i = 0; i < edits.size(); ++i) {
            Edition e = edits.get(i);
            String ea = sa.get(i), eb = sb.get(i);
            boolean match = false;
            if (e.getType() == OpType.replace) {
                if (e.getEndA() - e.getBeginA() == e.getEndB() - e.getBeginB() 
                        && dist(ea, eb) < UPDATE_THRESHOLD) {
                    System.out.println(">>>>> UPDATE (" + dist(ea, eb) + ")\n" + e);
                    match = true;
                } else {
                    match = lineUpdate(edits.get(i));
                }
            } 
            if ((e.getType() == OpType.del || e.getType() == OpType.replace)) {
                for (int j = 0; !match && j < edits.size(); ++j) {
                    Edition f = edits.get(j); 
                    if (i != j && (f.getType() == OpType.ins || f.getType() == OpType.replace)) {
                        String fb = sb.get(j); 
                        int d = dist(ea, fb);
                        if (d < MOVE_UPDATE_THRESHOLD) {
                            System.out.println(">>>>> MOVE (" + d + ")\n" + e + "\n============================\n" + f);
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Identify partial updates. I.E. updates combined with insertion and deletions.
     * Dynamic programming algorithm for diffing. 
     */
    private boolean lineUpdate(Edition edit) {
        List<String> la = edit.getCa(), lb = edit.getCb();
        boolean match = false;
        int [][]mat = new int[la.size() + 1][lb.size() + 1];
        int [][]md = new int[la.size()][lb.size()];
        for (int i = 0; i < la.size(); ++i) {
            for (int j = 0; j < lb.size(); ++j) { 
                md[i][j] = dist(la.get(i), lb.get(j));
            }        
        }

        for (int j = 0; j <= lb.size(); ++j) {
            mat[0][j] = j * 100;
        }
        for (int i = 1; i <= la.size(); ++i) {
            mat[i][0] = i * 100;
            for (int j = 1; j <= lb.size(); ++j) { 
                mat[i][j] = Math.min(mat[i][j-1], mat[i-1][j]) + 100;
                int d = md[i - 1][j - 1];
                if (d < LINE_UPDATE_THRESHOLD) {
                    if (d < MOVE_UPDATE_THRESHOLD) { 
                        match = true;
                    }
                    mat[i][j] = Math.min(mat[i-1][j-1] + d, mat[i][j]); 
                }
            }
        }
        if (match) {
            LinkedList<Edition> editList = new LinkedList<Edition>();
            int i = la.size(), j = lb.size();
            while (i > 0 && j > 0) {
                Edition first = editList.isEmpty() ? null : editList.getFirst();
                if (mat[i][j] == mat[i-1][j-1] + md[i-1][j-1]) {
                    if (first != null && first.getType() == OpType.update) {
                        first.setBeginA(first.getBeginA()-1);
                        first.setBeginB(first.getBeginB()-1);
                        first.getCa().add(0, la.get(i-1));
                        first.getCb().add(0, lb.get(j-1));
                    } else {
                        editList.addFirst(new Edition(OpType.update, edit.getBeginA() + i - 1, edit.getBeginB() + j - 1, la.get(i-1), lb.get(j-1)));
                    }
                    --i; --j;
                } else if (mat[i][j] == mat[i-1][j] + 100) {
                    if (first != null && first.getType() == OpType.del) {
                        first.setBeginA(first.getBeginA()-1);
                        first.getCa().add(0, la.get(i-1));
                    } else {
                        editList.addFirst(new Edition(OpType.del, edit.getBeginA() + i - 1, edit.getBeginB() + j - 1, la.get(i-1), null));
                    }
                    --i;
                } else if (mat[i][j] == mat[i][j-1] + 100) {
                    if (first != null && first.getType() == OpType.ins) {
                        first.setBeginB(first.getBeginB()-1);
                        first.getCb().add(0, lb.get(j-1));
                    } else {
                        editList.addFirst(new Edition(OpType.ins, edit.getBeginA() + i - 1, edit.getBeginB() + j - 1, null, lb.get(j-1)));
                    }
                    --j;
                }
            }
            Edition first = editList.getFirst();
            if (i > 0) {
                if (first.getType() == OpType.del) {
                    first.setBeginA(edit.getBeginA());
                    first.getCa().addAll(la.subList(0, i));
                } else {
                    editList.addFirst(new Edition(OpType.del, edit.getBeginA(), edit.getBeginA() + i,
                            edit.getBeginB(), edit.getBeginB(), la.subList(0, i), null));
                }
            } else if (j > 0) {
                if (first.getType() == OpType.ins) {
                    first.setBeginB(edit.getBeginB());
                    first.getCb().addAll(lb.subList(0, j));
                } else {
                    editList.addFirst(new Edition(OpType.ins, edit.getBeginA(), edit.getBeginA(),
                            edit.getBeginB(), edit.getBeginB() + j, null, lb.subList(0, j)));
                }
            }
            System.out.println(">>>>> PARTIAL UPDATE (" + mat[la.size()][lb.size()] + ")\n" + edit + "=======================");
            for (Edition e : editList) {
                System.out.println(e);
            }
            
        }
        return match;
    }
    
    /**
     * Relative edit distance.
     */
    private int dist(String a, String b) {
        return neil.diff_levenshtein(neil.diff_main(a, b)) * 100 / a.length();
    } 
    
    /*
     * A list of string to a string.
     */
    private String stringer(List<String> list) {
        StringBuilder b = new StringBuilder();
        for (String s : list) {
            b.append(s);
        }
        return b.toString();
    }
    
    /*
     * Creates a file edition corresponding to a diff entry (without content if merge is true)
     */
    public FileEdition createDiffResult(DiffEntry ent, boolean merge) throws CorruptObjectException, MissingObjectException, IOException {
        FileHeader.PatchType type = PatchType.UNIFIED;
        List<Edition> elist = null;
        if (ent.getOldMode() != GITLINK && ent.getNewMode() != GITLINK && !merge) {
            byte[] aRaw = open(OLD, ent);
            byte[] bRaw = open(NEW, ent);
            if (aRaw == BINARY || bRaw == BINARY //
                    || RawText.isBinary(aRaw) || RawText.isBinary(bRaw)) {
                type = PatchType.BINARY;
            } else {
                elist = diff(aRaw, bRaw);
            }
        }
        // Detect move
        if (detectMaU) {
            detectMovesAndUpdates(elist);
        }
        
        return new FileEdition(ent, type, elist);
    }
    
// TODO : No parent
    public Commit parseRepository() throws IOException {
        return parseRepository(path);
    } 
        
    Commit parseRepository(String path) throws IOException {
        HashMap<String, String> paths = new HashMap<String, String>();
        HashMapSet<String, Integer> identifiers = new HashMapSet<String, Integer>();
        HashMapSet<String, String> children = new HashMapSet<String, String>();
        int freeId = 2;
        Commit head = null;       

        RevWalk revwalk = new RevWalk(repository);
        revwalk.sort(RevSort.TOPO);
        if (path != null) {
            revwalk.setTreeFilter(AndTreeFilter.create(PathFilter.create(path), TreeFilter.ANY_DIFF));
        }  
        revwalk.markStart(revwalk.parseCommit(repository.resolve("HEAD")));      
        Iterator<RevCommit> it = revwalk.iterator();
        
        while (it.hasNext()) {
            RevCommit commit = it.next();
            Commit co = new Commit(commit, children.getAll(ObjectId.toString(commit)));
            if (head == null) {
                // Head commit
                co.setId("HEAD");
                head = co;
                if (path != null) {
                    paths.put("HEAD", path);
                }
                identifiers.put("HEAD", 1);
            }
            co.setReplica(Collections.min(identifiers.getAll(co.getId())));          
            if (commit.getParentCount() == 0) {
                // Final case : patch without parent
                List<FileEdition> edits = new LinkedList<FileEdition>(); 
                TreeWalk walk = walker(commit, path); // paths.get(co.getId()));
                while (walk.next()) { 
                    ObjectId id = walk.getObjectId(0);
                    edits.add(new FileEdition(walk.getPathString(), 
                            diff(new byte[0], open(walk.getPathString(), id))));
                }
                patchCrud.add(new Patch(co, edits));
            } else {
                boolean merge = false;
                if (commit.getParentCount() > 1) {
                    // Merge case -> store state
                    List<String> mpaths = new LinkedList<String>();
                    List<byte[]> mraws = new LinkedList<byte[]>();
                    TreeWalk twalk = walker(commit, path); // paths.get(co.getId()));
                    while (twalk.next()) {
                        ObjectId id = twalk.getObjectId(0);
                        mpaths.add(twalk.getPathString());
                        mraws.add(open(twalk.getPathString(), id));
                    }
                    patchCrud.add(new Patch(co, mpaths, mraws));
                }

                // Computes replica identifiers
                Iterator<Integer> itid = identifiers.getAll(co.getId()).iterator();
       
                for (int p = 0; p < commit.getParentCount(); ++p) {
                    RevCommit parent = commit.getParent(p);
                    String parentId = ObjectId.toString(parent);
                    children.put(parentId, co.getId());
                    List<FileEdition> edits = new LinkedList<FileEdition>();
                    TreeWalk walk = walker(commit, parent, path); // paths.get(co.getId()));
                    
                    // compute diff
                    for (DiffEntry entry : DiffEntry.scan(walk)) {
                        edits.add(createDiffResult(entry, merge));
                        if (path != null) {
                            paths.put(parentId, entry.getOldPath());
                        }
                    }
                    
                    patchCrud.add(new Patch(co, parent, edits));
                    if (itid.hasNext()) {
                        identifiers.put(parentId, itid.next());
                    } else if (!identifiers.containsKey(ObjectId.toString(parent))) {
                        identifiers.put(parentId, freeId);
                        ++freeId;
                    }
                }

                int i = 0;
                while (itid.hasNext()) {
                    identifiers.put(ObjectId.toString(commit.getParent(i)), itid.next());
                    i = (i + 1) % commit.getParentCount();
                }
            }
            commitCrud.add(co);
        }
        return head;
    } 
    
    private TreeWalk walker(RevCommit commit, RevCommit parent, String path) throws IOException {
        TreeWalk walk = new TreeWalk(repository);
        walk.addTree(parent.getTree());
        walk.addTree(commit.getTree());
        walk.setRecursive(true);
        if (path != null) {
            walk.setFilter(PathFilter.create(path));
        }
        return walk;
    }
    
    private TreeWalk walker(RevCommit commit, String path) throws IOException {
        TreeWalk walk = new TreeWalk(repository);
        walk.addTree(commit.getTree());
        walk.setRecursive(true);
        if (path != null) {
            walk.setFilter(PathFilter.create(path));
        }
        return walk;
    }




}
