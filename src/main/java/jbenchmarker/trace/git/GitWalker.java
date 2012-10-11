/*
 *  Replication Benchmarker
 *  https://github.com/score-team/replication-benchmarker/
 *  Copyright (C) 2012 LORIA / Inria / SCORE Team
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.

 */
package jbenchmarker.trace.git;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.FileHandler;
import org.eclipse.jgit.diff.ContentSource;
import org.eclipse.jgit.diff.DiffAlgorithm;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.diff.EditList;
import org.eclipse.jgit.diff.RawText;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.diff.Sequence;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.LargeObjectException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.AnyObjectId;
import org.eclipse.jgit.merge.MergeFormatter;
import org.eclipse.jgit.merge.MergeResult;
import org.eclipse.jgit.merge.ResolveMerger;
import org.eclipse.jgit.merge.StrategyResolve;
import org.eclipse.jgit.merge.ThreeWayMerger;
import org.eclipse.jgit.patch.FileHeader;
import org.eclipse.jgit.storage.pack.PackConfig;
import org.eclipse.jgit.treewalk.FileTreeIterator;
import org.eclipse.jgit.treewalk.filter.TreeFilter;

/**
 *
 * @author Stephane Martin <stephane.martin@loria.fr>
 */
public class GitWalker {

    private final ContentSource source;
    // private final ContentSource.Pair pairSource;
    private final ObjectReader reader;
    FileRepositoryBuilder builder;
    Repository repo;
    Git git;
    DiffAlgorithm diffAlgorithm;
    String gitDir;
    AnyObjectId fromCommit;

    public static void main(String... arg) throws Exception {

        if (arg.length < 1) {
            System.out.println("GitTalker <git Repository> [lastcommit] [outputfile]");
        }
        String fromCommit;
        if (arg.length < 1) {
            fromCommit = "master";
        } else {
            fromCommit = arg[1];
        }
        PrintStream p;
        if (arg.length < 2) {
            p = System.out;
        } else {
            File fout = new File(arg[2]);
            p = new PrintStream(fout);
        }
        GitWalker gw = new GitWalker(arg[0], fromCommit);

        HashMap<String, BlockLine> res = gw.measure();
        res = gw.filter(res, gw.getFromCommit());
        System.out.println("Number of Files :" + res.size());

        for (Entry<String, BlockLine> line : res.entrySet()) {
            p.println(line.getKey() + ";" + line.getValue().getBlock() + ";" + line.getValue().getLine());
        }
    }

    public GitWalker(String gitDir, String fromCommit) throws IOException {
        builder = new FileRepositoryBuilder();
        repo = builder.setGitDir(new File(gitDir + "/.git")).readEnvironment()
                .findGitDir().build();
        this.reader = repo.newObjectReader();
        this.gitDir = gitDir;
        this.git = new Git(repo);
        this.source = ContentSource.create(reader);
        this.diffAlgorithm = DiffAlgorithm.getAlgorithm(DiffAlgorithm.SupportedAlgorithm.MYERS);
        this.fromCommit = repo.resolve(fromCommit);
        // this.pairSource = new ContentSource.Pair(source, source);
    }

    HashMap<String, BlockLine> filter(HashMap<String, BlockLine> result, AnyObjectId obj) throws IOException {
        HashMap<String, BlockLine> resultFiltred = new HashMap();
        RevWalk revWalk = new RevWalk(repo);
        RevCommit revCom = revWalk.parseCommit(obj);
        TreeWalk tw = new TreeWalk(repo);
        tw.addTree(revCom.getTree());
        tw.setFilter(TreeFilter.ALL);
        tw.setRecursive(true);
        while (tw.next()) {
            BlockLine blockLine = result.get(tw.getPathString());
            if (blockLine != null) {
                resultFiltred.put(tw.getPathString(), blockLine);
            }
        }
        return resultFiltred;
    }

    RevWalk initRevWalker() throws MissingObjectException, IncorrectObjectTypeException, IOException {
        RevWalk revWalk = new RevWalk(repo);
        revWalk.markStart(revWalk.parseCommit(fromCommit));
        return revWalk;
    }

    void ls() throws Exception {

        RevWalk revWalk = initRevWalker();
        for (RevCommit revCom : revWalk) {
            TreeWalk tw = new TreeWalk(repo);
            tw.addTree(revCom.getTree());
            tw.setFilter(TreeFilter.ANY_DIFF);
            tw.setRecursive(true);

            while (tw.next()) {
                System.out.println("name:" + tw.getPathString());
                //tw.setFilter(AndTreeFilter.create(PathFilter.create(path), TreeFilter.ANY_DIFF));
                System.out.println("dept:" + tw.getDepth());
                if (!tw.isSubtree()) {
                    tw.getObjectId(0);
                    ObjectReader objr = tw.getObjectReader();

                    for (int i = 1; i < tw.getTreeCount(); i++) {
                        System.out.println("other :");
                        ObjectLoader ldr = source.open(tw.getPathString(), tw.getObjectId(i));
                        System.out.println(">" + new String(ldr.getBytes()) + "<");

                    }

                    ObjectLoader ldr = source.open(tw.getPathString(), tw.getObjectId(0));
                    //Merger merger=new Merger(repo);


                    System.out.println(">" + new String(ldr.getBytes()) + "<");
                }
                tw.getObjectReader();
            }

            tw.release();
            /* while (walk.next()) { 
             ObjectId id = walk.getObjectId(0);*/
            /*edits.add(new FileEdition(walk.getPathString(), 
             diff(new byte[0], open(walk.getPathString(), id))));*/
            /*     System.out.println(id);
             }*/

            /* for (RevCommit revComP : revCom.getParents()) {
             System.out.println("from:" + revComP.getFullMessage());
             //   RevTree rt = revComP.getTree();

             }*/
            System.out.println("------");

            //RevWalk revWalkMerger = new RevWalk(repo);
            //  mr.getNewHead();
            //RevCommit revComMerge = null;//revWalk.lookupCommit(mr.getNewHead());
            //tw.addTree(revComMerge.getTree());
            //  revWalkMerger.markStart(revWalk.parseCommit(mr.getNewHead()));

        }
    }

    void printStream(InputStream s, PrintStream p) throws IOException {
        int c;
        while ((c = s.read()) > -1) {
            p.print((char) c);
        }
    }

    void lauchAndWait(String arg) throws IOException, InterruptedException {
        // System.err.println(arg);
        Process p = Runtime.getRuntime().exec(arg, new String[0], new File(gitDir));
        p.waitFor();
        if (p.exitValue() != 0) {
            System.err.println("Error : " + arg);
            printStream(p.getErrorStream(), System.err);
            printStream(p.getInputStream(), System.err);
            System.err.flush();
        }

    }

    public HashMap<String, BlockLine> measure() throws Exception {
        int nbCommit;
        int current = 0;
        boolean file;

        List<String> names = Arrays.asList("a", "b", "c", "d", "b", "c", "d", "b", "c", "d", "b", "c", "d", "b", "c", "d", "b", "c", "d", "b", "c", "d");
        HashMap<String, RawText> map = new HashMap();
        HashMap<String, BlockLine> result = new HashMap();

        RevWalk revWalk = initRevWalker();

        for (RevCommit revCom : revWalk) {
            System.out.println("[" + (++current) + "]");
            if (revCom.getParentCount() < 2) {
                continue;
            }

            file = false;
            StrategyResolve sr = new StrategyResolve();
            ThreeWayMerger twm = sr.newMerger(repo, true);
            if (twm instanceof ResolveMerger) {
                //System.out.println("hahaha");
                try {
                    if (true) {
                        throw new Exception("test");
                    }
                    ResolveMerger rm = (ResolveMerger) twm;
                    rm.setWorkingTreeIterator(new FileTreeIterator(repo));
                    rm.merge(revCom.getParents());

                    for (Entry<String, MergeResult<? extends Sequence>> line : rm.getMergeResults().entrySet()) {
                        //System.out.println("+++++++++++++");
                        MergeResult<? extends Sequence> mergeRes = line.getValue();
                        MergeFormatter mf = new MergeFormatter();
                        ByteArrayOutputStream buff = new ByteArrayOutputStream();
                        mf.formatMerge(buff, (MergeResult<RawText>) mergeRes, names, revCom.getEncoding().displayName());

                        map.put(line.getKey(), new RawText(buff.toByteArray()));
                        System.out.println("add: " + line.getKey());
                    }

                    //continue;
                } catch (Exception ex) {
                    file = true;

                    RevCommit[] parents = revCom.getParents();
                    //lauchAndWait("git clean -f ");
                    //lauchAndWait("git checkout " + parents[0].getName());
                    lauchAndWait("git reset --hard " + parents[0].getName());
                    StringBuilder ids = new StringBuilder();
                    for (int i = 1; parents.length > i; i++) {
                        ids.append(parents[i].getName());
                        ids.append(" ");
                    }
                    /* for (RevCommit revComP : revCom.getParents()) {
                     System.out.println("Oops:" + revComP.getId().name());
                     }*/
                    lauchAndWait("git merge --no-commit " + ids.toString());

                    System.out.println("--------------------------");

                }
                //return;
            } else {
                System.out.println("fuck");
                System.exit(0);
            }

            TreeWalk tw = new TreeWalk(repo);
            tw.addTree(revCom.getTree());
            tw.setFilter(TreeFilter.ANY_DIFF);
            tw.setRecursive(true);
            while (tw.next()) {
                if (!tw.isSubtree()) {
                    tw.getPathString();
                    ObjectLoader ldr = source.open(tw.getPathString(), tw.getObjectId(0));
                    ldr.getType();
                    RawText d1 = null;
                    try {
                        d1 = new RawText(ldr.getBytes(PackConfig.DEFAULT_BIG_FILE_THRESHOLD));
                    } catch (LargeObjectException.ExceedsLimit overLimit) {
                        continue;
                    } catch (LargeObjectException.ExceedsByteArrayLimit overLimit) {
                        continue;
                    }
                    RawText d2;
                    if (file) {
                        File f = new File(gitDir + "/" + tw.getPathString());
                        if (f.exists()) {
                            d2 = new RawText(f);
                        } else {
                            d2 = null;
                            System.err.println("Warning file " + f + " doesn't exist");
                        }

                    } else {
                        d2 = map.get(tw.getPathString());
                        if (d2==null){
                            continue;
                        }
                        
                    }
                    //System.out.println("get: " + tw.getPathString());
                    if (d2 == null) {
                        d2 = new RawText(new byte[0]);
                    }

                    EditList editList = diffAlgorithm.diff(RawTextComparator.DEFAULT, d1, d2);

                    BlockLine editCount = result.get(tw.getPathString());
                    if (editCount == null) {
                        editCount = new BlockLine();
                        result.put(tw.getPathString(), editCount);
                    }


                    for (Edit ed : editList) {
                        editCount.addLine(ed.getEndA() - ed.getBeginA() + ed.getEndB() - ed.getBeginB());
                    }
                    editCount.addBlock(editList.size());
                   /* if (tw.getPathString().equals("alloc.c")) {
                        showDiff(d1, d2);
                        System.out.println(tw.getPathString() + editCount);
                    }*/
                    // System.out.println("get: " + tw.getPathString());


                }
            }
            map.clear();
            tw.release();
        }
        return result;
    }

    public AnyObjectId getFromCommit() {
        return fromCommit;
    }

    public static void printRawText(RawText rt) {
        for (int i = 0; i < rt.size(); i++) {
            System.out.println(rt.getString(i));
        }

    }

    public static void showDiff(RawText rt1, RawText rt2) {
        if (rt1.size() == 0 || rt2.size() == 0) {
            System.out.println("Achtung !");
            System.out.println("rt1:" + rt1.size());
            System.out.println("rt2:" + rt2.size());
        }
        for (int i = 0; i < Math.min(rt2.size(), rt1.size()); i++) {
            System.out.println("-----------");
            System.out.println(rt1.getString(i));
            System.out.println("VS");
            System.out.println(rt2.getString(i));
            System.out.println("+++++++++++");
        }
    }

    static class BlockLine {

        int block = 0;
        int line = 0;
        boolean binary = false;

        public BlockLine() {
        }

        public int getBlock() {
            return block;
        }

        public void setBlock(int block) {
            this.block = block;
        }

        public int getLine() {
            return line;
        }

        public void setLine(int line) {
            this.line = line;
        }

        public void addLine(int line) {
            this.line += line;
        }

        public void addBlock(int block) {
            this.block += block;
        }

        @Override
        public String toString() {
            return "{" + "blocks:" + block + ", lines:" + line + '}';
        }

        public void setBinary(boolean binary) {
            this.binary = binary;
        }
    }
}
