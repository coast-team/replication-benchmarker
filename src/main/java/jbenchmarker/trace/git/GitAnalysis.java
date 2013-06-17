/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker.trace.git;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import jbenchmarker.Experience;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.RevertCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.AmbiguousObjectException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevSort;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.filter.AndTreeFilter;
import org.eclipse.jgit.treewalk.filter.PathFilter;
import org.eclipse.jgit.treewalk.filter.TreeFilter;

/**
 * Computes some git analysis.
 * @author urso
 **/
public class GitAnalysis {

    private final Repository repository;
    private int reverts = 0;
    private int failed = 0;
        
        
    public GitAnalysis(Repository repository) {
        this.repository = repository;
    }

    /** 
     * Counts the number of commit with a revert message that cannot be reverted automatically.
     **/    
    void countFailedRevert(String path) throws IOException, GitAPIException {
        RevWalk revwalk = new RevWalk(repository);
        revwalk.sort(RevSort.TOPO);

        if (path != null) {
            revwalk.setTreeFilter(AndTreeFilter.create(PathFilter.create(path), TreeFilter.ANY_DIFF));
        }
        revwalk.markStart(revwalk.parseCommit(repository.resolve("HEAD")));
        Iterator<RevCommit> it = revwalk.iterator();
        while (it.hasNext()) {
            RevCommit commit = it.next();
            ObjectId r = sayRevert(commit);
            if (r != null) {
                reverts++;
                if (commit.getParentCount() == 1) {
                    Git git = new Git(repository);
                    ResetCommand reset = git.reset();
                    reset.addPath(path);
                    reset.setRef(commit.getParent(0).getName());
                    reset.call();
                    RevertCommand revert = git.revert();
                    revert.include(r);
                    try {
                        revert.call();
                    } catch (GitAPIException ex) {
                        failed++;
                    }
                }
            }

        }
    }

    /**
     * If the message of the commit says "This reverts ..." returns the corresponding reverted commit id.
     */
    private ObjectId sayRevert(RevCommit commit) throws AmbiguousObjectException, IOException {
        String msg = commit.getFullMessage();
        if (msg.contains("This reverts")) {
            Pattern pattern = Pattern.compile("\\b[a-f0-9]{40}\\b");
            Matcher matcher = pattern.matcher(msg);
            if (matcher.find()) {
                return repository.resolve(matcher.group());
            }
        }
        return null;
    }

    public static void main(String... args) throws Exception {
        if (args.length < 1 && args.length > 2) {
            System.err.println("Arguments : repository [file]");
            System.exit(1);
        }
        
        String gitdir = args[0];
        List<String> paths = new LinkedList<String>();
        if (args.length > 1) {
            paths.add(args[1]);
        } else {
            Experience.extractFiles(new File(gitdir), gitdir, paths);
        }
      
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        Repository repo = builder.setGitDir(new File(args[0] + "/.git")).readEnvironment().findGitDir().build();
        GitAnalysis ga = new GitAnalysis(repo);      
        for (String p : paths) {
            ga.countFailedRevert(p);
            System.out.println(p + ":" + ga.failed + "/" + ga.reverts);
        }
    }
    
}
