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

import collect.HashMapSet;
import java.io.File;
import java.io.IOException;
import java.util.*;
import jbenchmarker.trace.git.model.Commit;
import jbenchmarker.trace.git.model.Edition;
import jbenchmarker.trace.git.model.FileEdition;
import jbenchmarker.trace.git.model.Patch;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import static org.eclipse.jgit.diff.DiffEntry.Side.NEW;
import static org.eclipse.jgit.diff.DiffEntry.Side.OLD;
import org.eclipse.jgit.diff.*;
import org.eclipse.jgit.errors.AmbiguousObjectException;
import org.eclipse.jgit.errors.CorruptObjectException;
import org.eclipse.jgit.errors.LargeObjectException;
import org.eclipse.jgit.errors.MissingObjectException;
import static org.eclipse.jgit.lib.FileMode.GITLINK;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.patch.FileHeader;
import org.eclipse.jgit.patch.FileHeader.PatchType;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevSort;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.storage.pack.PackConfig;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.AndTreeFilter;
import org.eclipse.jgit.treewalk.filter.PathFilter;
import org.eclipse.jgit.treewalk.filter.TreeFilter;
import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.DbPath;
import org.ektorp.http.HttpClient;
import org.ektorp.http.StdHttpClient;
import org.ektorp.impl.StdCouchDbConnector;
import org.ektorp.impl.StdCouchDbInstance;
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
    
    public GitExtraction(Repository repo, CouchDbRepositorySupport<Commit> dbc,  
            CouchDbRepositorySupport<Patch> dbp, DiffAlgorithm diffAlgorithm, String path) {
        this.repository = repo;
        this.reader = repo.newObjectReader();
   	this.source = ContentSource.create(reader);
	this.pairSource = new ContentSource.Pair(source, source);
        this.diffAlgorithm = diffAlgorithm;
        this.patchCrud = dbp;
        this.commitCrud = dbc;
        this.git = new Git(repo);
        this.path = path;
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
        List<Edition> editions = new LinkedList<Edition>();
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
                TreeWalk walk = walker(commit, paths.get(co.getId()));
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
                    TreeWalk twalk = walker(commit, paths.get(co.getId()));
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
                    TreeWalk walk = walker(commit, parent, paths.get(co.getId()));
                    
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
