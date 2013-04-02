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

import org.eclipse.jgit.diff.Edit.Type;
import collect.HashMapSet;
import java.io.IOException;
import java.util.*;
import jbenchmarker.core.SequenceOperation;
import jbenchmarker.core.SequenceOperation.OpType;
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

    private boolean MIN_TWO_LINES_MOVE = true;
    private static final int DEFAULT_LINE_UPDATE_THRESHOLD = 50;
    private static final int DEFAULT_UPDATE_THRESHOLD = 20;
    private static final int DEFAULT_MOVE_THRESHOLD = 10;


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
    private final boolean detectMoveAndUpdate;
    private int lineUpdateThresold = 50;
    private int updateThresold = 20;
    private int moveThresold = 10;
    
    
    /**
     * Test constructor. Do not use outside test.
     **/
    GitExtraction(int lineUpdateThresold, int updateThresold, int moveThresold) {
        this.repository = null;
        this.reader = null;
        this.source = null;
        this.pairSource = null;
        this.diffAlgorithm = defaultDiffAlgorithm;
        this.patchCrud = null;
        this.commitCrud = null;
        this.git = null;
        this.path = null;
        this.detectMoveAndUpdate = true;
        this.lineUpdateThresold = lineUpdateThresold;
        this.updateThresold = updateThresold;
        this.moveThresold = moveThresold;       
    }
    
    public GitExtraction(Repository repo, CouchDbRepositorySupport<Commit> dbc,
            CouchDbRepositorySupport<Patch> dbp, DiffAlgorithm diffAlgorithm, String path) {
        this(repo, dbc, dbp, diffAlgorithm, path, false, 0, 0, 0);
    }

    public GitExtraction(Repository repo, CouchDbRepositorySupport<Commit> dbc,
            CouchDbRepositorySupport<Patch> dbp, DiffAlgorithm diffAlgorithm, String path, 
            boolean detectMovesAndUpdates, int lineUpdateThresold, int updateThresold, int moveThresold) {
        this.repository = repo;
        this.reader = repo.newObjectReader();
        this.source = ContentSource.create(reader);
        this.pairSource = new ContentSource.Pair(source, source);
        this.diffAlgorithm = diffAlgorithm;
        this.patchCrud = dbp;
        this.commitCrud = dbc;
        this.git = new Git(repo);
        this.path = path;
        this.detectMoveAndUpdate = detectMovesAndUpdates;
        this.lineUpdateThresold = lineUpdateThresold;
        this.updateThresold = updateThresold;
        this.moveThresold = moveThresold;
    }

    // TODO : resolve problem of diff docuement in same DB
    private GitExtraction(Repository repo, CouchDbConnector db) {
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

    /**
     * Detects moves and updates. Replace couples delete/insert by update or
     * moves.
     *
     * @param input the edit list to parse
     */
    List<Edition> detectMovesAndUpdates(List<Edition> input) {
        Map<OpType, LinkedList<Edition>> edits = new EnumMap<OpType, LinkedList<Edition>>(OpType.class);
        edits.put(OpType.delete, new LinkedList<Edition>());
        edits.put(OpType.insert, new LinkedList<Edition>());
        edits.put(OpType.update, new LinkedList<Edition>());
        edits.put(OpType.move, new LinkedList<Edition>());
        ListIterator<Edition> edit = input.listIterator();

        // Replaces "replace" by updates, insert and delete
        while (edit.hasNext()) {
            Edition e = edit.next();
            if (e.getType() == OpType.replace) {
                LinkedList<Edition> lines = lineUpdate(e);
                if (lines != null) {
                    for (Edition ed : lines) {
                        edits.get(ed.getType()).add(ed);
                    }
                } else {
                    edits.get(OpType.insert).add(new Edition(OpType.insert, e.getBeginA(), e.getBeginA(),
                            e.getBeginB(), e.getEndB(), null, e.getCb()));
                    edits.get(OpType.delete).add(new Edition(OpType.delete, e.getBeginA(), e.getEndA(),
                            e.getBeginB(), e.getBeginB(), e.getCa(), null));
                }
            } else {
                edits.get(e.getType()).add(e);
            }
        }

        // Identify moves between delete and insert
        ListIterator<Edition> delit = edits.get(OpType.delete).listIterator();
        while (delit.hasNext()) {
            Edition e = delit.next();
            if (e.getCa().size() > 1) {
                ListIterator<Edition> insit = edits.get(OpType.insert).listIterator();
                List<Edition> move = null;
                int pos = 0;
                while (insit.hasNext()) {
                    Edition f = insit.next();
                    if (f.getCb().size() > 1) {
                        List<Edition> lines = lineUpdate(e.getCa(), e.getBeginA(),
                                f.getCb(), f.getBeginB(), true, f.getBeginA());
                        if (lines != null && (move == null || lines.size() < move.size())) {
                            move = lines;
                            pos = insit.previousIndex();
                        }
                    }
                }
                if (move != null) {
                    int back = 0;
                    insit = edits.get(OpType.insert).listIterator(pos);
                    insit.next();
                    insit.remove();
                    delit.remove();
                    for (Edition ed : move) {
                        switch (ed.getType()) {
                            case update:
                                ed.setType(OpType.move);
                                edits.get(OpType.move).add(ed);
                                break;
                            case insert:
                                insit.add(ed);
                                break;
                            case delete:
                                delit.add(ed);
                                back++;
                                break;
                        }
                    }
                    for (; back > 0; --back) {
                        delit.previous();
                    }
                }
            }
        }


        // Fusion of edit operations. Insert has lower priority. 
        List<Edition> result = new ArrayList<Edition>();
        while (!allEmpty(edits)) {
            Edition edm = null;
            for (LinkedList<Edition> l : edits.values()) {
                if (!l.isEmpty() && (edm == null || l.peekFirst().getBeginA() > edm.getBeginA()
                        || (edm.getType() == OpType.insert && l.peekFirst().getBeginA() == edm.getBeginA()))) {
                    edm = l.peekFirst();
                }
            }
            result.add(edm);
            edits.get(edm.getType()).removeFirst();
        }

        // Apply position shift due to moves
        for (int i = 0; i < result.size(); ++i) {
            Edition e = result.get(i);
            if (e.getType() == OpType.move) {
                if (e.getBeginB() < e.getBeginA()) {
                    int shift = e.getCb().size();
                    for (int j = i + 1; j < result.size() && result.get(j).getBeginA() >= e.getBeginB(); ++j) {
                        Edition f = result.get(j);
                        f.setBeginA(f.getBeginA() + shift);
                        f.setEndA(f.getEndA() + shift);
                    }
                } else {
                    int shift = 0;
                    for (int j = i - 1; j >= 0 && result.get(j).getBeginA() < e.getBeginB(); --j) {
                        Edition f = result.get(j);
                        if (f.getType() == OpType.insert) {
                            shift += f.getCb().size();
                        } else if (f.getType() == OpType.delete) {
                            shift -= f.getCa().size();
                        }
                    }
                    e.setBeginB(e.getBeginB() + shift);
                    e.setEndB(e.getEndB() + shift);
                }
            }
        }
        System.out.println("========= PATCH ===========");
        for (Edition ed : result) {
            System.out.println(">>>>>>>>> " + ed.getType() + "\n" + ed);
        }
        return result;
    }

    private LinkedList<Edition> lineUpdate(Edition edit) {
        return lineUpdate(edit.getCa(), edit.getBeginA(), edit.getCb(), edit.getBeginB(), false, 0);
    }

    /**
     * Identify partial updates. I.E. updates combined with insertion and
     * deletions. Dynamic programming algorithm for diffing.
     */
    private LinkedList<Edition> lineUpdate(List<String> listA, int beginA,
            List<String> listB, int beginB, boolean move, int beginMove) {
        boolean match = false;
        int[][] mat = new int[listA.size() + 1][listB.size() + 1];
        int[][] md = new int[listA.size()][listB.size()];
        for (int i = 0; i < listA.size(); ++i) {
            for (int j = 0; j < listB.size(); ++j) {
                md[i][j] = dist(listA.get(i), listB.get(j));
            }
        }

        for (int j = 0; j <= listB.size(); ++j) {
            mat[0][j] = j * 100;
        }
        for (int i = 1; i <= listA.size(); ++i) {
            mat[i][0] = i * 100;
            for (int j = 1; j <= listB.size(); ++j) {
                mat[i][j] = Math.min(mat[i][j - 1], mat[i - 1][j]) + 100;
                int d = md[i - 1][j - 1];
                if (d < lineUpdateThresold) {
                    if (d < (move ? moveThresold : updateThresold)) {
                        match = true;
                    }
                    mat[i][j] = Math.min(mat[i - 1][j - 1] + d, mat[i][j]);
                }
            }
        }
        if (match) { // compute edit operations
            if (move && MIN_TWO_LINES_MOVE) { // check for at least 2 lines
                match = false;
            }
            LinkedList<Edition> editList = new LinkedList<Edition>();
            int i = listA.size(), j = listB.size();
            while (i > 0 && j > 0) {
                Edition last = editList.isEmpty() ? null : editList.getLast();
                if (mat[i][j] == mat[i - 1][j - 1] + md[i - 1][j - 1]) {
                    --i;
                    --j;
                    if (last != null && last.getType() == OpType.update) {
                        last.setBeginA(last.getBeginA() - 1);
                        last.setBeginB(last.getBeginB() - 1);
                        last.getCa().add(0, listA.get(i));
                        last.getCb().add(0, listB.get(j));
                        match = true; // 2 consecutive lines
                    } else {
                        editList.add(new Edition(OpType.update, beginA + i,
                                (move ? beginMove : beginB) + j,
                                listA.get(i), listB.get(j)));
                    }
                } else if (mat[i][j] == mat[i - 1][j] + 100) {
                    --i;
                    if (last != null && last.getType() == OpType.delete) {
                        last.setBeginA(last.getBeginA() - 1);
                        last.getCa().add(0, listA.get(i));
                    } else {
                        editList.add(new Edition(OpType.delete,
                                beginA + i, beginB + j, listA.get(i), null));
                    }

                } else if (mat[i][j] == mat[i][j - 1] + 100) {
                    --j;
                    if (last != null && last.getType() == OpType.insert) {
                        last.setBeginB(last.getBeginB() - 1);
                        last.getCb().add(0, listB.get(j));
                    } else {
                        editList.add(new Edition(OpType.insert,
                                beginA + i, beginB + j, null, listB.get(j)));
                    }
                }
            }
            if (i > 0) {
                editList.add(new Edition(OpType.delete, beginA, beginA + i,
                        beginB, beginB, listA.subList(0, i), null));
            } else if (j > 0) {
                editList.add(new Edition(OpType.insert, beginA, beginA,
                        beginB, beginB + j, null, listB.subList(0, j)));
            }
            if (match) {
                return editList;
            }
        }
        return null;
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
        // Detect move and update
        if (detectMoveAndUpdate) {
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

    static private boolean allEmpty(Map<OpType, LinkedList<Edition>> results) {
        for (LinkedList<Edition> l : results.values()) {
            if (!l.isEmpty()) {
                return false;
            }
        }
        return true;
    }
}
