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

import collect.HashMapSet;
import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.diff.ContentSource;
import org.eclipse.jgit.diff.DiffAlgorithm;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.diff.EditList;
import org.eclipse.jgit.diff.RawText;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.LargeObjectException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.AnyObjectId;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevSort;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.storage.pack.PackConfig;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.AndTreeFilter;
import org.eclipse.jgit.treewalk.filter.PathFilter;
import org.eclipse.jgit.treewalk.filter.TreeFilter;

/**
 *
 * @author Stephane Martin <stephane.martin@loria.fr>
 */
public class GitWalker2 {

    public static final RawText EmptyFile = new RawText(new byte[0]);
    private final ContentSource source;
    private final ObjectReader reader;
    private FileRepositoryBuilder builder;
    private Repository repository;
    private Git git;
    private DiffAlgorithm diffAlgorithm;
    private String gitDir;
    private AnyObjectId fromCommit;
    //private boolean countCreatedFile = false;
    //private boolean useOnlyGitCommand = true;
    static boolean progressBar = false;
    private String fileFilter = null;
    private static final Logger logger = Logger.getLogger(GitWalker2.class.getCanonicalName());
    private int fileNumber;
    private double filePercent;
    private int numberOfWorker = Runtime.getRuntime().availableProcessors();
    /*results*/
    HashMap<String, BlockLine> files;
    HashMapSet<String, Couple> idCommitToFiles;
    HashMapSet<String, String> commitParent;

    /**
     *
     * @param arg
     * @throws Exception
     */
    public static void main(String... arg) throws Exception {
        int idx;
        if (arg.length < 1) {
            System.out.println("GitTalker <git Repository> [-l lastcommit] [-o outputfile] [-f fileName] [-p] [-d/w]");
            System.out.println("-l the last commit");
            System.out.println("-o output csv file");
            System.out.println("-p display a progress bar");
            System.out.println("-f interest only on file");
            System.out.println("-d debug informations");
            System.out.println("-w display warning");

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

        if (argList.contains("-w")) {
            logger.setLevel(Level.WARNING);
        } else if (argList.contains("-d")) {
            logger.setLevel(Level.ALL);
        } else {
            logger.setLevel(Level.SEVERE);
        }


        GitWalker2 gw = new GitWalker2(arg[0], fromCommit);


        gw.setFileFilter(fileFilter);
        if (progressBar) {
            System.out.println("Extracting files path ...");
        }
        gw.extractFiles();
        System.out.println("\n\nNumber of Files :" + gw.files.size() + "\n\n");
        //gw.printResults(p);
        if (progressBar) {
            System.out.println("Extracting Id of commits with" +gw.getNumberOfWorker()+ " thread(s) ...");
        }
        gw.extractIDCommit();
        // gw.printResults(p);
        if (progressBar) {
            System.out.println("\n\nMesures diff...");
        }
        gw.mesuresDiff();
        //HashMap<String, BlockLine> res = gw.measure();
        //Filtre to obtain only file in last commit 
        //res = gw.filter(res, gw.getFromCommit());

        //Write the result
        gw.printResults(p);
    }

    /**
     *
     * @param gitDir
     * @param fromCommit
     * @throws IOException
     */
    public GitWalker2(String gitDir, String fromCommit) throws IOException {
        builder = new FileRepositoryBuilder();
        repository = builder.setGitDir(new File(gitDir + "/.git")).readEnvironment()
                .findGitDir().build();
        this.reader = repository.newObjectReader();
        this.gitDir = gitDir;
        this.git = new Git(repository);
        this.source = ContentSource.create(reader);
        this.diffAlgorithm = DiffAlgorithm.getAlgorithm(DiffAlgorithm.SupportedAlgorithm.MYERS);
        this.fromCommit = repository.resolve(fromCommit);

        // this.pairSource = new ContentSource.Pair(source, source);
    }

    public void printResults(PrintStream p) {
        p.println("File name;number of commit;number of merge;number of comp;number of block;number of line");
        for (Entry<String, BlockLine> line : files.entrySet()) {
            p.println(line.getKey() + ";" + line.getValue().getCommitCount() + ";" + line.getValue().getMergesSize() + ";" + line.getValue().getPass() + ";" + line.getValue().getBlock() + ";" + line.getValue().getLine());
        }
    }

    public void setFileFilter(String fileFilter) {
        this.fileFilter = fileFilter;
    }

    public String getFileFilter() {
        return fileFilter;
    }

    // initialize the walker from the commit id of constructor.
    RevWalk initRevWalker(String fileFilter) throws MissingObjectException, IncorrectObjectTypeException, IOException {
        RevWalk revWalk = new RevWalk(repository);
        revWalk.markStart(revWalk.parseCommit(fromCommit));
        if (fileFilter != null) {
            // revWalk.setTreeFilter(PathFilter.create(fileFilter));
            revWalk.setTreeFilter(AndTreeFilter.create(PathFilter.create(fileFilter), TreeFilter.ANY_DIFF));
        }
        revWalk.sort(RevSort.TOPO);
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
     * Put git in previous state than rev com and merge, if many files.
     *
     * @param revCom
     * @throws IOException
     * @throws InterruptedException
     */
    void gitPositionMerge(RevCommit revCom) throws IOException, InterruptedException {
        gitPositionMerge(revCom.getParents());
    }

    void gitPositionMerge(RevCommit[] parents) throws IOException, InterruptedException {
        launchAndWait("git reset --hard " + parents[0].getName());
        StringBuilder ids = new StringBuilder();
        for (int i = 1; parents.length > i; i++) {
            ids.append(parents[i].getName());
            ids.append(" ");
        }
        launchAndWait("git merge --no-commit " + ids.toString());
    }

    void gitPositionMerge(Set<String> parents) throws IOException, InterruptedException {
        Iterator<String> it = parents.iterator();

        launchAndWait("git reset --hard " + it.next());
        StringBuilder ids = new StringBuilder();
        //for (int i = 1; parents.length > i; i++) {
        while (it.hasNext()) {
            ids.append(it.next());
            ids.append(" ");
        }

        launchAndWait("git merge --no-commit " + ids.toString());
    }

    public void extractFiles() throws MissingObjectException, IncorrectObjectTypeException, IOException {
        //RevWalk revWalk = initRevWalker(fileFilter);

        RevWalk revWalk = new RevWalk(repository);
        RevCommit revCom = revWalk.parseCommit(fromCommit);
        TreeWalk tw = new TreeWalk(repository);



        files = new HashMap<String, BlockLine>();
        tw.addTree(revCom.getTree());

        if (this.fileFilter != null) {
            tw.setFilter(PathFilter.create(this.fileFilter));
        } else {
            tw.setFilter(TreeFilter.ALL);
        }

        tw.setRecursive(true);
        while (tw.next()) {
            String filename = tw.getPathString();
            files.put(filename, new BlockLine(filename));
            logger.log(Level.INFO, "Get file {0}.", filename);
        }
        tw.release();
        this.fileNumber = files.size();
        this.filePercent = fileNumber / 100.0;

        // return ret;
    }

    void addParrent(String idCommit, RevCommit revCom) {
        Set<String> parent = this.commitParent.getAll(idCommit);
        if (parent == null) {
            logger.info("Commit parent is created");
            RevCommit[] revParent = revCom.getParents();
            for (int i = 0; i < revParent.length; i++) {
                commitParent.put(idCommit, revParent[i].getName());
            }

        } else {
            if (commitParent.getAll(idCommit).size() != revCom.getParentCount()) {
                logger.log(Level.WARNING, "Number of parrent of commit {0} id is different !", idCommit);
            }
        }
    }

    public void treatFileEntry(Entry<String, BlockLine> file) throws Exception {
        String fileName = file.getKey();
        BlockLine stats = file.getValue();
        RevWalk revWalk = initRevWalker(fileName);
        logger.log(Level.INFO, "--FileName {0}", fileName);
        for (RevCommit revCom : revWalk) {

            String idCommit = revCom.getName();
            logger.log(Level.INFO, "Commit <{0}>", idCommit);
            if (revCom.getParentCount() > 1) {// this is a merge
                addParrent(idCommit, revCom);
                stats.incrementMerge(idCommit);
                logger.log(Level.INFO, "is a merge");
                TreeWalk tw = new TreeWalk(repository);
                tw.addTree(revCom.getTree());
                tw.setRecursive(true);
                tw.setFilter(PathFilter.create(fileName));
                while (tw.next()) {
                    String f = tw.getPathString();
                    if (f.equals(fileName)) {
                        this.idCommitToFiles.put(idCommit, new Couple(tw.getObjectId(0), f));
                    } else {
                        logger.log(Level.SEVERE, "Filefilter doesn''t work {0}!={1}", new Object[]{f, fileName});
                    }
                }
                tw.release();
            }
            stats.incrementCommitCount();
        }
    }

    public void extractIDCommit() throws Exception {
        int fileThreated = 0;

        commitParent = new HashMapSet<String, String>();
        idCommitToFiles = new HashMapSet<String, Couple>();
        TryMultiThreaded tmt = new TryMultiThreaded(files.size(), this.numberOfWorker);
        tmt.start();
        for (Entry<String, BlockLine> file : files.entrySet()) {
            tmt.addJob(file);
            //treatFileEntry(file);
            fileThreated++;
        }
        tmt.waitAll();
    }

    public void mesuresDiff() throws Exception {
        int commit = 0;
        double commitPercent = idCommitToFiles.size() / 100.0;
        double nextStep = commitPercent;


        for (String idcommit : idCommitToFiles.keySet()) {
            if (progressBar) {
                while (commit > nextStep) {
                    System.out.print(".");
                    System.out.flush();
                    nextStep += commitPercent;
                }
            }
            logger.log(Level.INFO, "Treat Commit {0}", idcommit);
            gitPositionMerge(commitParent.getAll(idcommit));
            for (Couple c : idCommitToFiles.getAll(idcommit)) {
                RawText stateAfterMerge = EmptyFile;
                RawText stateAfterCommit = EmptyFile;

                File f = new File(gitDir + "/" + c.getFileName());
                if (f.exists()) { // if file is existing after merge
                    launchAndWait("sed /^[<=>][<=>][<=>][<=>]/d -i " + f.getPath());
                    stateAfterMerge = new RawText(f);
                } else {
                    logger.log(Level.WARNING, "File{0} not found !", f.getPath());
                }
                try {
                    ObjectLoader ldr = source.open(c.fileName, c.getId());
                    ldr.getType();

                    stateAfterCommit = new RawText(ldr.getBytes(PackConfig.DEFAULT_BIG_FILE_THRESHOLD));
                } catch (LargeObjectException.ExceedsLimit overLimit) {// File is overlimits => binary
                    logger.log(Level.WARNING, "File {0} overlimits !", c.getFileName());
                    continue;
                } catch (LargeObjectException.ExceedsByteArrayLimit overLimit) {// File is overlimits => binary
                    logger.log(Level.WARNING, "File {0} exceed limits!", c.getFileName());
                    continue;
                } catch (Exception ex) { // if another exception like inexitant considere 0 file.
                    logger.log(Level.WARNING, "File {0} not found in after commit !", c.getFileName());
                }

                EditList editList = diffAlgorithm.diff(RawTextComparator.DEFAULT, stateAfterMerge, stateAfterCommit);
                BlockLine editCount = files.get(c.fileName);
                editCount.incrementPass();

                for (Edit ed : editList) {// Count line replace is two time counted
                    editCount.addLine(ed.getEndA() - ed.getBeginA() + ed.getEndB() - ed.getBeginB());
                }
                //Count the block size
                editCount.addBlock(editList.size());

            }
            commit++;
        }
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

    public int getNumberOfWorker() {
        return numberOfWorker;
    }

    /**
     * Statistic Element
     */
    public static class BlockLine {

        private int block = 0;
        private int line = 0;
        private int commitCount = 0;
        //private int count = 0;
        private LinkedList<String> merges = new LinkedList();
        private String fileName;
        private int pass;

        /**
         * empty constructor
         */
        public BlockLine(String fileName) {
            this.fileName = fileName;
        }

        /**
         * increment count of merge initialized at 0 by constructor
         */
        synchronized public void incrementMerge(String commitId) {
            merges.add(commitId);
        }

        synchronized public void incrementCommitCount() {
            commitCount++;
        }

        /**
         * get modified block count
         *
         * @return block count
         */
        synchronized public int getBlock() {
            return block;
        }

        /**
         * get modified line count
         *
         * @return modified line count
         */
        synchronized public int getLine() {
            return line;
        }

        /**
         * Increment modified line number
         *
         * @param line
         */
        synchronized public void addLine(int line) {
            this.line += line;
        }

        /**
         * Increment modified block number
         *
         * @param block
         */
        synchronized public void addBlock(int block) {
            this.block += block;
        }

        @Override
        synchronized public String toString() {
            return "BlockLine{" + "block=" + block + ", line=" + line + ", count=" + getMerges().size() + '}';
        }

        synchronized public int getCommitCount() {
            return commitCount;
        }

        synchronized public void incrementPass() {
            pass++;
        }

        synchronized public int getPass() {
            return pass;
        }

        synchronized public LinkedList<String> getMerges() {
            return merges;
        }

        synchronized public String getFileName() {
            return fileName;
        }

        synchronized public int getMergesSize() {
            return merges.size();
        }
    }

    class Couple {

        private ObjectId id;
        private String fileName;

        public Couple(ObjectId id, String fileName) {
            this.id = id;
            this.fileName = fileName;
        }

        public ObjectId getId() {
            return id;
        }

        public String getFileName() {
            return fileName;
        }
    }

    class TryMultiThreaded {

        int fileTotal = 0;
        int fileTaked = 0;
        int fileThreated = 0;
        double nextStep = filePercent;
        LinkedList<Entry<String, BlockLine>> jobs = new LinkedList();
        Thread[] threads;

        public TryMultiThreaded(int fileTotal, int nbThread) {
            this.fileTotal = fileTotal;
            threads = new Thread[nbThread];
            
        }
        void start(){
            for (int i = 0; i < threads.length; i++) {
                threads[i] = new Thread(new WorkerExtractGit());
                threads[i].start();
            }
        }
        void waitAll() throws InterruptedException {
            for (int i = 0; i < threads.length; i++) {
                threads[i].join();
            }
        }

        synchronized Entry<String, BlockLine> getJobFileTaked() {

            while (jobs.size() <= 0 && fileTaked < fileTotal) {
                try {
                    wait();
                } catch (InterruptedException ex) {
                    Logger.getLogger(GitWalker2.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (jobs.size() > 0) {
                fileTaked++;
                return jobs.removeFirst();
            } else {
                return null;
            }
        }

        synchronized void incFileThreated() {
            fileThreated++;
            if (progressBar) {
                while (fileThreated > nextStep) {
                    System.out.print(".");
                    System.out.flush();
                    nextStep += filePercent;
                }
            }
        }

        synchronized void addJob(Entry<String, BlockLine> entry) {
            jobs.addLast(entry);
            notifyAll();
        }

        class WorkerExtractGit implements Runnable {

            @Override
            public void run() {
                while (fileTaked < fileTotal) {
                    try {

                        Entry<String, BlockLine> job = getJobFileTaked();
                        if (job != null) {
                            treatFileEntry(job);
                            incFileThreated();
                        }
                    } catch (Exception ex) {
                        Logger.getLogger(GitWalker2.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }
            }
        }
    }
}
