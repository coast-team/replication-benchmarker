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

import com.sun.istack.internal.logging.Logger;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;
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
    boolean countCreatedFile = false;
    boolean useOnlyGitCommand = true;
    static Logger logger = Logger.getLogger(GitWalker.class);

    public static void main(String... arg) throws Exception {
        int idx;
        if (arg.length < 2) {
            System.out.println("GitTalker <git Repository> [-l lastcommit] [-o outputfile]");
            System.exit(1);
        }
        String fromCommit;
        List<String> argList = Arrays.asList(arg);
        if ((idx = argList.indexOf("-o")) > -1 && idx + 1 < arg.length) {
            fromCommit = arg[idx + 1];
        } else {
            fromCommit = "origin/master";

        }
        PrintStream p;
        if ((idx = argList.indexOf("-o")) > -1 && idx + 1 < arg.length) {
            File fout = new File(arg[idx + 1]);
            p = new PrintStream(fout);
        } else {
            p = System.out;

        }
        GitWalker gw = new GitWalker(arg[0], fromCommit);
        logger.setLevel(Level.SEVERE);
        HashMap<String, BlockLine> res = gw.measure();
        res = gw.filter(res, gw.getFromCommit());
        System.out.println("Number of Files :" + res.size());
        p.println("File name;number of merge;number of block;number of line");
        for (Entry<String, BlockLine> line : res.entrySet()) {
            p.println(line.getKey() + ";" + line.getValue().getCount() + ";" + line.getValue().getBlock() + ";" + line.getValue().getLine());
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

    // initialize the walker from the commit id of constructor.
    RevWalk initRevWalker() throws MissingObjectException, IncorrectObjectTypeException, IOException {
        RevWalk revWalk = new RevWalk(repo);
        revWalk.markStart(revWalk.parseCommit(fromCommit));
        return revWalk;
    }

    //Print all element in stream in p
    public static void printStream(InputStream s, PrintStream p) throws IOException {
        int c;
        while ((c = s.read()) > -1) {
            p.print((char) c);
        }
    }

    //Convert all element in stream in String
    public static String stream2Str(InputStream s) throws IOException {
        StringBuilder str = new StringBuilder();
        int c;
        while ((c = s.read()) > -1) {
            str.append((char) c);
        }
        return str.toString();

    }

    //Launch a command and wait the terminaison. 
    // If the return number of command is not 0 then display all stream in warning logger
    public static void launchAndWait(String command, String currentDirectory) throws IOException, InterruptedException {
        logger.info("command line : " + command);
        Process p = Runtime.getRuntime().exec(command, new String[0], new File(currentDirectory));
        p.waitFor();
        if (p.exitValue() != 0) {
            logger.warning("command existed with error code : " + p.exitValue());
            logger.warning("error : " + stream2Str(p.getErrorStream()));
            logger.warning("output : " + stream2Str(p.getInputStream()));
        }

    }

    public void launchAndWait(String command) throws IOException, InterruptedException {
        launchAndWait(command, gitDir);
    }

    public HashMap<String, BlockLine> measure() throws Exception {
        int nbCommit;
        int current = 0;
        boolean file;

        List<String> names = Arrays.asList("a", "b", "c", "d", "b", "c", "d", "b", "c", "d", "b", "c", "d", "b", "c", "d", "b", "c", "d", "b", "c", "d");
        HashMap<String, RawText> map = new HashMap();
        HashMap<String, BlockLine> result = new HashMap();


        RevWalk revWalk = initRevWalker();

        //For all commit
        for (RevCommit revCom : revWalk) {
            logger.info("Commit passed " + (++current));

            // if recom is not issue on merge go to the next
            if (revCom.getParentCount() < 2) {
                continue;
            }
            logger.info("working on :" + revCom.getId().name());
            file = false;
            StrategyResolve sr = new StrategyResolve();
            ThreeWayMerger twm = sr.newMerger(repo, true);
            if (twm instanceof ResolveMerger) {
                try {
                    if (useOnlyGitCommand) {
                        throw new Exception("Use Only git command");
                    }
                    ResolveMerger rm = (ResolveMerger) twm;
                    rm.setWorkingTreeIterator(new FileTreeIterator(repo));
                    rm.merge(revCom.getParents());

                    for (Entry<String, MergeResult<? extends Sequence>> line : rm.getMergeResults().entrySet()) {
                        MergeResult<? extends Sequence> mergeRes = line.getValue();
                        MergeFormatter mf = new MergeFormatter();
                        ByteArrayOutputStream buff = new ByteArrayOutputStream();
                        mf.formatMerge(buff, (MergeResult<RawText>) mergeRes, names, revCom.getEncoding().displayName());

                        map.put(line.getKey(), new RawText(buff.toByteArray()));
                        this.logger.info("add: " + line.getKey());
                    }

                    //continue;
                } catch (Exception ex) {
                    file = true;

                    RevCommit[] parents = revCom.getParents();
                    launchAndWait("git reset --hard " + parents[0].getName());
                    StringBuilder ids = new StringBuilder();
                    for (int i = 1; parents.length > i; i++) {
                        ids.append(parents[i].getName());
                        ids.append(" ");
                    }
                    launchAndWait("git merge --no-commit " + ids.toString());
                }
            } else {
                logger.severe("The merger is not resolveMerger");
                System.exit(-21);
            }

            TreeWalk tw = new TreeWalk(repo);
            //add rev com in tree
            tw.addTree(revCom.getTree());
            //add fathers rev in tree.
            for (RevCommit revComP : revCom.getParents()) {
                tw.addTree(revComP.getTree());
            }

            //filter only different files
            tw.setFilter(TreeFilter.ANY_DIFF);

            tw.setRecursive(true);
            while (tw.next()) {
                //if the object is not an folder.
                if (!tw.isSubtree()) {

                    RawText stateAfterMerge = null;//State after merger
                    if (file) { // if git used
                        File f = new File(gitDir + "/" + tw.getPathString());
                        if (f.exists()) { // if file is existing after merge
                            stateAfterMerge = new RawText(f);
                        } else { //else the file is created by commit
                            stateAfterMerge = null;
                            logger.warning("" + f + " doesn't exist");

                            // doesn't count new files
                            if (!countCreatedFile) {
                                continue;
                            }
                        }

                    } else {
                        stateAfterMerge = map.get(tw.getPathString());
                        if (stateAfterMerge == null && !countCreatedFile) {
                            continue;
                        }

                    }
                    if (stateAfterMerge == null) {
                        stateAfterMerge = new RawText(new byte[0]);
                    }



                    RawText stateAfterCommit = null;
                    //Path of commit
                    try {
                        ObjectLoader ldr = source.open(tw.getPathString(), tw.getObjectId(0));
                        ldr.getType();

                        stateAfterCommit = new RawText(ldr.getBytes(PackConfig.DEFAULT_BIG_FILE_THRESHOLD));
                    } catch (LargeObjectException.ExceedsLimit overLimit) {// File is overlimits => binary
                        continue;
                    } catch (LargeObjectException.ExceedsByteArrayLimit overLimit) {// File is overlimits => binary
                        continue;
                    } catch (Exception ex) { // if another exception like inexitant considere 0 file.
                        stateAfterCommit = new RawText(new byte[0]);
                    }


                    EditList editList = diffAlgorithm.diff(RawTextComparator.DEFAULT, stateAfterMerge, stateAfterCommit);


                    BlockLine editCount = result.get(tw.getPathString());
                    if (editCount == null) {// if it's first time for this file We create an entry.
                        editCount = new BlockLine();
                        result.put(tw.getPathString(), editCount);
                    }

                    editCount.increment();

                    for (Edit ed : editList) {// Count line replace is two time counted
                        editCount.addLine(ed.getEndA() - ed.getBeginA() + ed.getEndB() - ed.getBeginB());
                    }
                    //Count the block size
                    editCount.addBlock(editList.size());
//                    FOR debug
//                    if (tw.getPathString().equals("git-gui/lib/branch.tcl")) {
//                        logger.info("Parent count :" + revCom.getParentCount());
//                        logger.info("" + (stateAfterCommit == null ? "d1null" : "") + (stateAfterMerge == null ? "d2null" : ""));
//                        logger.info(tw.getPathString() + editCount);
//                        logger.info("");
//
//                    }
//                    // System.out.println("get: " + tw.getPathString());


                }
            }
            //cleaning for the next pass.
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

        private int block = 0;
        private int line = 0;
        private int count = 0;
        boolean binary = false;

        public BlockLine() {
        }

        public void increment() {
            count++;
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
            return "BlockLine{" + "block=" + block + ", line=" + line + ", count=" + count + ", binary=" + binary + '}';
        }

        public int getCount() {
            return count;
        }

        public void setBinary(boolean binary) {
            this.binary = binary;
        }
    }
}
