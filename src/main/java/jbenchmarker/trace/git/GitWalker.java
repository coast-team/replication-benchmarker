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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.diff.ContentSource;
import org.eclipse.jgit.diff.DiffAlgorithm;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.diff.EditList;
import org.eclipse.jgit.diff.RawText;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.diff.Sequence;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.LargeObjectException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.AnyObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.merge.MergeFormatter;
import org.eclipse.jgit.merge.MergeResult;
import org.eclipse.jgit.merge.ResolveMerger;
import org.eclipse.jgit.merge.StrategyResolve;
import org.eclipse.jgit.merge.ThreeWayMerger;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.storage.pack.PackConfig;
import org.eclipse.jgit.treewalk.FileTreeIterator;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;
import org.eclipse.jgit.treewalk.filter.TreeFilter;

/**
 *
 * @author Stephane Martin <stephane.martin@loria.fr>
 */
public class GitWalker {

    private final ContentSource source;
    private final ObjectReader reader;
    FileRepositoryBuilder builder;
    Repository repo;
    Git git;
    DiffAlgorithm diffAlgorithm;
    String gitDir;
    AnyObjectId fromCommit;
    boolean countCreatedFile = false;
    boolean useOnlyGitCommand = true;
    static boolean progressBar = false;
    String fileFilter = null;
    static final Logger logger = Logger.getLogger(GitWalker.class.getCanonicalName());

    /**
     *
     * @param arg
     * @throws Exception
     */
    public static void main(String... arg) throws Exception {
        int idx;
        if (arg.length < 2) {
            System.out.println("GitTalker <git Repository> [-l lastcommit] [-o outputfile] [-f fileName] [-p]");
            System.out.println("-l the last commit");
            System.out.println("-o output csv file");
            System.out.println("-p display a progress bar");
            System.out.println("-f interest only on file");

            System.exit(1);
        }
        String fromCommit;
        List<String> argList = Arrays.asList(arg);
        if ((idx = argList.indexOf("-l")) > -1 && idx + 1 < arg.length) {
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
        String fileFilter = null;
        if ((idx = argList.indexOf("-f")) > -1 && idx + 1 < arg.length) {
            fileFilter = arg[idx + 1];
        }


        if (argList.contains("-p")) {
            progressBar = true;
        }
        GitWalker gw = new GitWalker(arg[0], fromCommit);
        gw.setFileFilter(fileFilter);
        logger.setLevel(Level.SEVERE);
        HashMap<String, BlockLine> res = gw.measure();
        //Filtre to obtain only file in last commit 
        res = gw.filter(res, gw.getFromCommit());
        System.out.println("\n\nNumber of Files :" + res.size());
        //Write the result
        p.println("File name;number of merge;number of block;number of line");
        for (Entry<String, BlockLine> line : res.entrySet()) {
            p.println(line.getKey() + ";" + line.getValue().getCount() + ";" + line.getValue().getBlock() + ";" + line.getValue().getLine());
        }
    }

    /**
     *
     * @param gitDir
     * @param fromCommit
     * @throws IOException
     */
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

    public void setFileFilter(String fileFilter) {
        this.fileFilter = fileFilter;
    }

    public String getFileFilter() {
        return fileFilter;
    }

    // Filter the restult to eliminate files which not in commit
    HashMap<String, BlockLine> filter(HashMap<String, BlockLine> result, AnyObjectId commit) throws IOException {
        HashMap<String, BlockLine> resultFiltred = new HashMap();
        RevWalk revWalk = new RevWalk(repo);
        RevCommit revCom = revWalk.parseCommit(commit);
        TreeWalk tw = new TreeWalk(repo);
        tw.addTree(revCom.getTree());
        tw.setFilter(TreeFilter.ALL);
        tw.setRecursive(true);
        while (tw.next()) {
            BlockLine blockLine = result.get(tw.getPathString());
            if (blockLine != null) {
                resultFiltred.put(tw.getPathString(), blockLine);
            }else {
                resultFiltred.put(tw.getPathString(), new BlockLine());
            }
        }
        return resultFiltred;
    }

    // initialize the walker from the commit id of constructor.
    RevWalk initRevWalker() throws MissingObjectException, IncorrectObjectTypeException, IOException {
        RevWalk revWalk = new RevWalk(repo);
        revWalk.markStart(revWalk.parseCommit(fromCommit));
        if (fileFilter != null) {
            revWalk.setTreeFilter(PathFilter.create(fileFilter));
        }
        return revWalk;
    }

    /**
     * Print all element in stream in p
     *
     * @param s Stream
     * @param p Printer
     * @throws IOException
     */
    public static void printStream(InputStream s, PrintStream p) throws IOException {
        int c;
        while ((c = s.read()) > -1) {
            p.print((char) c);
        }
    }

    /**
     * Convert all element in stream in String
     *
     * @param s Stream
     * @return the result String
     * @throws IOException
     */
    public static String stream2Str(InputStream s) throws IOException {
        StringBuilder str = new StringBuilder();
        int c;
        while ((c = s.read()) > -1) {
            str.append((char) c);
        }
        return str.toString();

    }

    /**
     * Launch a command and wait the terminaison. If the return number of
     * command is not 0 then display all stream in warning logger.
     *
     * @param command the command to be launch
     * @param currentDirectory Current working directory for the command
     * @throws IOException
     * @throws InterruptedException
     */
    public static void launchAndWait(String command, String currentDirectory) throws IOException, InterruptedException {
        logger.log(Level.INFO, "command line : {0}", command);
        Process p = Runtime.getRuntime().exec(command, new String[0], new File(currentDirectory));
        p.waitFor();
        if (p.exitValue() != 0) {
            logger.log(Level.WARNING, "command existed with error code : {0}", p.exitValue());
            logger.log(Level.WARNING, "error : {0}", stream2Str(p.getErrorStream()));
            logger.log(Level.WARNING, "output : {0}", stream2Str(p.getInputStream()));
        }

    }

    /**
     * Launch a command and wait the terminaison. If the return number of
     * command is not 0 then display all stream in warning logger. The current
     * directory is git dir.
     *
     * @param command
     * @throws IOException
     * @throws InterruptedException
     */
    public void launchAndWait(String command) throws IOException, InterruptedException {
        launchAndWait(command, gitDir);
    }

    /**
     * Mesure the patch between a merge of concurent states and next commit
     *
     * @return map associate filename and stat
     * @throws Exception
     */
    public HashMap<String, BlockLine> measure() throws Exception {
        int nbCommit = -1;
        double nextPas = 0;
        int current = 0;

        boolean file;

        List<String> names = Arrays.asList("a", "b", "c", "d", "b", "c", "d", "b", "c", "d", "b", "c", "d", "b", "c", "d", "b", "c", "d", "b", "c", "d");
        HashMap<String, RawText> map = new HashMap();
        HashMap<String, BlockLine> result = new HashMap();

        if (progressBar) {
            nbCommit = 0;
            RevWalk revWalk = initRevWalker();
            for (RevCommit revCom : revWalk) {
                nbCommit++;
            }
        }

        RevWalk revWalk = initRevWalker();
        //revWalk.setTreeFilter(TreeFilter.ANY_DIFF);



        //For all commit
        for (RevCommit revCom : revWalk) {
            logger.log(Level.INFO, "Commit passed {0}", (++current));

            if (nbCommit > -1) {
                if (current >= nextPas) {
                    nextPas += (nbCommit) / 100.0;
                    System.out.print(".");
                    System.out.flush();
                }
            }
            // if revCom is not issue on merge go to the next commit
            if (revCom.getParentCount() < 2) {
                continue;
            }
            logger.log(Level.INFO, "working on :{0}", revCom.getId().name());
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
                        logger.log(Level.INFO, "add: {0}", line.getKey());
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
            //add father rev in tree .
            tw.addTree(revCom.getParent(0).getTree());

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
                            logger.log(Level.WARNING, "{0} doesn''t exist", f);

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



                    RawText stateAfterCommit;
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
//                    if (tw.getPathString().equals(".gitattributes") && editList.size() > 0) {
//                        System.out.println("" + editCount);
//                        System.out.println("rev:" + revCom.name());
//                        for (RevCommit pa : revCom.getParents()) {
//                            System.out.println("parent:" + pa.name());
//                        }
//
//                        System.out.println("");
//
//                    }
                }
            }
            //cleaning for the next pass.
            map.clear();
            tw.release();
        }
        return result;
    }

    /**
     *
     * @return from commit parameter
     */
    public AnyObjectId getFromCommit() {
        return fromCommit;
    }

    /**
     * display a row text
     *
     * @param rt
     */
    public static void printRawText(RawText rt) {
        for (int i = 0; i < rt.size(); i++) {
            System.out.println(rt.getString(i));
        }

    }

    /**
     * show diff between two row text
     *
     * @param rt1
     * @param rt2
     */
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

    /**
     * Statistic Element
     */
    public static class BlockLine {

        private int block = 0;
        private int line = 0;
        private int count = 0;

        /**
         * empty constructor
         */
        public BlockLine() {
        }

        /**
         * increment count initialized at 0 by constructor
         */
        public void increment() {
            count++;
        }

        /**
         * get modified block count
         *
         * @return block count
         */
        public int getBlock() {
            return block;
        }

        /**
         * get modified line count
         *
         * @return modified line count
         */
        public int getLine() {
            return line;
        }

        /**
         * Increment modified line number
         *
         * @param line
         */
        public void addLine(int line) {
            this.line += line;
        }

        /**
         * Increment modified block number
         *
         * @param block
         */
        public void addBlock(int block) {
            this.block += block;
        }

        @Override
        public String toString() {
            return "BlockLine{" + "block=" + block + ", line=" + line + ", count=" + count + '}';
        }

        /**
         * get Count of object
         *
         * @return
         */
        public int getCount() {
            return count;
        }
    }
}
