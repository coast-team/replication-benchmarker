package jbenchmarker.trace.git;

import jbenchmarker.trace.git.model.Patch;
import jbenchmarker.trace.git.model.FileEdition;
import jbenchmarker.trace.git.model.Edition;
import jbenchmarker.trace.git.model.Commit;
import collect.HashMapSet;
import java.io.IOException;
import java.util.*;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import static org.eclipse.jgit.diff.DiffEntry.Side.NEW;
import static org.eclipse.jgit.diff.DiffEntry.Side.OLD;
import org.eclipse.jgit.diff.*;
import org.eclipse.jgit.errors.*;
import static org.eclipse.jgit.lib.FileMode.GITLINK;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.patch.FileHeader;
import org.eclipse.jgit.patch.FileHeader.PatchType;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.pack.PackConfig;
import org.eclipse.jgit.treewalk.TreeWalk;
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

    public GitExtraction(Repository repo, CouchDbRepositorySupport<Commit> dbc,  
            CouchDbRepositorySupport<Patch> dbp, DiffAlgorithm diffAlgorithm) {
        this.repository = repo;
        this.reader = repo.newObjectReader();
   	this.source = ContentSource.create(reader);
	this.pairSource = new ContentSource.Pair(source, source);
        this.diffAlgorithm = diffAlgorithm;
        this.patchCrud = dbp;
        this.commitCrud = dbc;
        this.git = new Git(repo);
    }
    
    public GitExtraction(Repository repo, CouchDbConnector db ) {
        this(repo, new CommitCRUD(db), new PatchCRUD(db));
    }
    
    public GitExtraction(Repository repo, CouchDbRepositorySupport<Commit> dbc,  
            CouchDbRepositorySupport<Patch> dbp) {
        this(repo, dbc, dbp, DiffAlgorithm.getAlgorithm(DiffAlgorithm.SupportedAlgorithm.MYERS));
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
        
    public List<Edition> edits(final EditList edits, final RawText a, final RawText b)
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

    public FileEdition createDiffResult(DiffEntry ent) throws CorruptObjectException, MissingObjectException, IOException {

        final FileHeader.PatchType type;
        List<Edition> elist = null;
        if (ent.getOldMode() == GITLINK || ent.getNewMode() == GITLINK) {
            type = PatchType.UNIFIED;
        } else {
            byte[] aRaw = open(OLD, ent);
            byte[] bRaw = open(NEW, ent);

            if (aRaw == BINARY || bRaw == BINARY //
                    || RawText.isBinary(aRaw) || RawText.isBinary(bRaw)) {
                type = PatchType.BINARY;
            } else {
                type = PatchType.UNIFIED;
                elist = diff(aRaw, bRaw);
            }
        }
        return new FileEdition(ent, type, elist);
    }
    
// TODO : No parent
    public Commit parseRepository() throws IOException, GitAPIException {
        return parseRepository(null);
    } 
        
    public Commit parseRepository(String path) throws IOException, GitAPIException {
        HashMap<RevCommit, String> paths = new HashMap<RevCommit, String>();
        HashMapSet<RevCommit, Integer> identifiers = new HashMapSet<RevCommit, Integer>();
        HashMapSet<RevCommit, String> children = new HashMapSet<RevCommit, String>();
        int freeId = 2;
        Commit head = null;
        LogCommand log = git.log();
        if (path != null) {
            log.addPath(path);
        }
        Iterator<RevCommit> it = log.call().iterator();
        while (it.hasNext()) {
            RevCommit commit = it.next();
            Commit co = new Commit(commit, children.getAll(commit));
            if (head == null) {
                // Head commit
                co.setId("HEAD");
                head = co;
                if (path != null) {
                    paths.put(commit, path);
                }
                identifiers.put(commit, 1);
            }
            co.setReplica(Collections.min(identifiers.getAll(commit)));          
            if (commit.getParentCount() == 0) {
                // Final case : patch without parent
                List<FileEdition> edits = new LinkedList<FileEdition>(); 
                TreeWalk walk = walker(commit, paths.get(commit));
                while (walk.next()) { 
                    ObjectId id = walk.getObjectId(0);
                    edits.add(new FileEdition(walk.getPathString(), 
                            diff(new byte[0], open(walk.getPathString(), id))));
                }
                patchCrud.add(new Patch(co, edits));
            } else {
                if (commit.getParentCount() > 1) {
                    // Merge case -> store state
                    List<String> mpaths = new LinkedList<String>();
                    List<byte[]> mraws = new LinkedList<byte[]>();
                    TreeWalk twalk = walker(commit, paths.get(commit));
                    while (twalk.next()) {
                        ObjectId id = twalk.getObjectId(0);
                        mpaths.add(twalk.getPathString());
                        mraws.add(open(twalk.getPathString(), id));
                    }
                    patchCrud.add(new Patch(co, mpaths, mraws));
                }

                // Computes replica identifiers
                Iterator<Integer> itid = identifiers.getAll(commit).iterator();
       
                for (int p = 0; p < commit.getParentCount(); ++p) {
                    RevCommit parent = commit.getParent(p);
                    children.put(parent, co.getId());
                    List<FileEdition> edits = new LinkedList<FileEdition>();
                    TreeWalk walk = walker(commit, parent, paths.get(commit));
                    
                    // compute diff
                    for (DiffEntry entry : DiffEntry.scan(walk)) {
                        edits.add(createDiffResult(entry));
                        if (path != null) {
                            paths.put(parent, entry.getOldPath());
                        }
                    }
                    patchCrud.add(new Patch(co, parent, edits));
                    if (itid.hasNext()) {
                        identifiers.put(parent, itid.next());
                    } else if (!identifiers.containsKey(parent)) {
                        identifiers.put(parent, freeId);
                        ++freeId;
                    }
                }

                int i = 0;
                while (itid.hasNext()) {
                    identifiers.put(commit.getParent(i), itid.next());
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
