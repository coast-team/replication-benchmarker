/**
 * Replication Benchmarker
 * https://github.com/score-team/replication-benchmarker/ Copyright (C) 2013
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

import Tools.ExecTools;
import Tools.ProgressBar;
import collect.HashMapSet;
import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeoutException;
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
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

/**
 *
 * @author Stephane Martin <stephane.martin@loria.fr>
 */
public class GitWalker {

    public static final RawText EmptyFile = new RawText(new byte[0]);
    private final ContentSource source;
    private final ObjectReader reader;
    private FileRepositoryBuilder builder;
    private Repository repository;
    private Git git;
    private DiffAlgorithm diffAlgorithm;
    private AnyObjectId fromCommit;
    //private boolean countCreatedFile = false;
    //private boolean useOnlyGitCommand = true;
    private static final Logger logger = Logger.getLogger(GitWalker.class.getCanonicalName());
    private int fileNumber;
    private double filePercent;
    private int numberOfWorker = Runtime.getRuntime().availableProcessors();
    private int timeOut = 60000;
    /*results*/
    HashMap<String, BlockLine> files;
    HashMapSet<String, Couple> idCommitParentsToFiles;
    HashMapSet<String, String> commitParent;

    public static enum LogLevel {

        All, Warning, Severe
    };
    public static final Level[] levels = {Level.ALL, Level.WARNING, Level.SEVERE};
    CmdLineParser parser;
    /**
     * Options arg4j
     */
    @Option(name = "-o", metaVar = "outputfile", usage = "Set ouput file (default: standard ouput )")
    File outputFile = null;
    @Option(name = "-A", usage = "append file and not write file header")
    boolean appendFile = false;
    @Option(name = "-l", metaVar = "commitID", usage = "id of the begin commit defaut is origin/master")
    String fromCommitStr = "origin/master";
    @Option(name = "-f", metaVar = "filefilter", usage = "Filter of only this file")
    private String fileFilter = null;
    @Option(name = "-p", usage = "Display the progress bar")
    boolean progressBar = false;
    @Option(name = "--log", usage = "Set logger level")
    LogLevel logLevel = LogLevel.Severe;
    @Option(name = "-h", usage = "Display this message")
    boolean help = false;
    @Argument(usage = "Git repository path", metaVar = "repository", required = true)
    private String gitDir;

    /**
     *
     * @param arg
     * @throws Exception
     */
    public static void main(String... arg) throws Exception {
        GitWalker gw = new GitWalker(arg);
        gw.run();
    }

    /**
     * Display the help and exit with i value
     *
     * @param i exit value
     * @param opt Options defined for cli-command
     */
    final void help(int i) {
        System.out.println("GitWalker [option] <repository>");
        parser.printUsage(System.out);
       
        System.exit(i);
    }

    public GitWalker(String... args) throws IOException {

        try {
            this.parser = new CmdLineParser(this);

            parser.parseArgument(args);

            if (help) {
                help(0);
            }
        } catch (CmdLineException ex) {
            System.err.println("Error in argument " + ex);
            help(-1);
        }

        Logger.getLogger("").setLevel(levels[logLevel.ordinal()]);

        builder = new FileRepositoryBuilder();
        repository = builder.setGitDir(new File(gitDir + "/.git")).readEnvironment()
                .findGitDir().build();
        this.reader = repository.newObjectReader();
        this.git = new Git(repository);
        this.source = ContentSource.create(reader);
        this.diffAlgorithm = DiffAlgorithm.getAlgorithm(DiffAlgorithm.SupportedAlgorithm.MYERS);
        this.fromCommit = repository.resolve(fromCommitStr);

    }

    public void run() throws Exception {
        if (progressBar) {
            System.out.println("Extracting files path ...");
        }
        /*
         * phases 1 Extract file list from commit
         */
        extractFiles();
        if (progressBar) {
            System.out.println("\n\nNumber of Files :" + files.size() + "\n\n");
            System.out.println("Extracting Id of commits with " + getNumberOfWorker() + " thread(s) ...");
        }
        /*
         * Sort by id of father each files version
         */

        extractIDCommit();

        if (progressBar) {
            System.out.println("\n\nMesures diff...");
        }
        /*
         * Mesures diff patchs
         */
        mesuresDiff();
        if (progressBar) {
            System.out.println("");
        }

        printResults();
    }

    

    /**
     * Print result into p (PrintStream)
     *
     * @param p PrintStream to write the result
     * @param displayHead print columns name.
     */
    public void printResults() throws FileNotFoundException {
        PrintStream p;
        if (outputFile == null) {
            p = System.out;
        } else {
            p = new PrintStream(new FileOutputStream(outputFile, appendFile));
        }
        if (!appendFile) {
            p.println("File name;number of commit;number of merge;number of pass"
                    + ";number of block;number of line");
        }
        for (Entry<String, BlockLine> line : files.entrySet()) {
            p.println(line.getKey() + ";" + line.getValue().getCommitCount()
                    + ";" + line.getValue().getMergesSize()
                    + ";" + line.getValue().getPass()
                    + ";" + line.getValue().getBlock()
                    + ";" + line.getValue().getLine());
        }
    }

    public void setFileFilter(String fileFilter) {
        this.fileFilter = fileFilter;
    }

    public String getFileFilter() {
        return fileFilter;
    }

    /**
     * Initialize the walker from the commit id of constructor.
     *
     * @param fileFilter
     * @return RevWalk for git browsing
     * @throws MissingObjectException
     * @throws IncorrectObjectTypeException
     * @throws IOException
     */
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
     * Launch a command and wait the terminaison. If the return number of
     * command is not 0 then display all stream in warning logger. The current
     * directory is git dir.
     *
     * @param command
     * @throws IOException
     * @throws InterruptedException
     */
    public void launchAndWait(String command) throws IOException, InterruptedException, TimeoutException {
        ExecTools.launchAndWait(command, gitDir, timeOut);
    }

    /**
     * Put git repository to parents merging. git is reseted to parent commit
     * and merged to followed parent In phase 3
     *
     * @param Parents
     * @throws IOException
     * @throws InterruptedException
     */
    void gitPositionMerge(Set<String> parents) throws IOException, InterruptedException {
        do {
            try {
                Iterator<String> it = parents.iterator();
                launchAndWait("git reset --hard " + it.next());
                StringBuilder ids = new StringBuilder();
                while (it.hasNext()) {
                    ids.append(it.next());
                    ids.append(" ");
                }

                launchAndWait("git merge --no-commit " + ids.toString());
                return;
            } catch (TimeoutException ex) {
                //Thread.sleep(1000);
                if (progressBar) {
                    System.out.print("X");
                    System.out.flush();
                }
                logger.log(Level.WARNING, "Process timed out retrying {0}", ex);
                Thread.sleep(1000);
            }
        } while (true);
    }

    /**
     * List the files to compute in phases two
     *
     * @throws MissingObjectException
     * @throws IncorrectObjectTypeException
     * @throws IOException
     */
    public void extractFiles() throws MissingObjectException, IncorrectObjectTypeException, IOException {
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
        revWalk.release();
        this.fileNumber = files.size();
        this.filePercent = fileNumber / 100.0;

    }

    /**
     * generate id of parent commit and add to data structure if it does not
     * existing.
     *
     * In phase 2
     *
     * @param idCommit
     * @param revCom
     * @param filename
     * @return Newid composed by id of parents commit
     *
     */
    String addParrent(RevCommit revCom) {
        int parentnb = revCom.getParentCount();
        StringBuilder newIDB = new StringBuilder();
        /*
         * Compute the list of parent to a string
         */
        for (int i = 0; i < parentnb; i++) {
            newIDB.append(revCom.getParent(i).getName());
            newIDB.append(' ');
        }
        String newID = newIDB.toString();

        Set<String> parent = this.commitParent.getAll(newID);
        /*
         * create the entry is not existing
         */
        if (parent == null) {
            logger.info("Commit parent is created");
            for (int i = 0; i < parentnb; i++) {
                commitParent.put(newID, revCom.getParent(i).getName());
            }
        }
        return newID;
    }

    /**
     * for a file we add each commit the file id in commit and we link to
     * parents In phase 2
     *
     * @param file to compute the list of commits
     * @throws Exception
     */
    public void treatFileEntry(Entry<String, BlockLine> file) throws Exception {
        String fileName = file.getKey();
        BlockLine stats = file.getValue();
        RevWalk revWalk = initRevWalker(fileName);
        logger.log(Level.INFO, "--FileName {0}", fileName);
        /*
         * For each commmit of a file
         */
        for (RevCommit revCom : revWalk) {

            String idCommit = revCom.getName();
            logger.log(Level.INFO, "Commit <{0}>", idCommit);
            if (revCom.getParentCount() > 1) {// this is a merge
                String newId = addParrent(revCom);
                stats.incrementMerge(newId);
                logger.log(Level.INFO, "is a merge");
                TreeWalk tw = new TreeWalk(repository);
                tw.addTree(revCom.getTree());
                tw.setRecursive(true);
                tw.setFilter(PathFilter.create(fileName));
                /*
                 * Get file id from commit
                 */
                while (tw.next()) {
                    String f = tw.getPathString();
                    if (f.equals(fileName)) {
                        this.idCommitParentsToFiles.put(newId, new Couple(tw.getObjectId(0), f));
                    } else {
                        logger.log(Level.SEVERE, "Filefilter doesn''t work {0}!={1}", new Object[]{f, fileName});
                    }
                }
                tw.release();
            }
            stats.incrementCommitCount();
        }
        //revWalk.release();
        revWalk.dispose();
    }

    /**
     * For each file it extract list of commit This is multi-threaded it fill a
     * joblist In phase 2
     *
     * @throws Exception
     */
    public void extractIDCommit() throws Exception {
        int fileThreated = 0;

        commitParent = new HashMapSet<String, String>();
        idCommitParentsToFiles = new HashMapSet<String, Couple>();
        TryMultiThreaded tmt = new TryMultiThreaded(files.size(), this.numberOfWorker);
        tmt.start();
        for (Entry<String, BlockLine> file : files.entrySet()) {
            tmt.addJob(file);
            fileThreated++;
        }
        tmt.waitAll();
    }

    /**
     * For all registred parent combinaison place git repository in merged state
     * it finds file from id in next commit and it compares with file in
     * repository In phase 3
     *
     * @throws Exception
     */
    public void mesuresDiff() throws Exception {
       // int commit = 0;
        ProgressBar progress=new ProgressBar(idCommitParentsToFiles.keySet().size());
/*        double commitPercent = idCommitParentsToFiles.keySet().size() / 100.0;
        double nextStep = commitPercent;*/


        for (String idcommitParent : idCommitParentsToFiles.keySet()) {

            logger.log(Level.INFO, "Treat Commit {0}", idcommitParent);

            /*
             * place git repository in merged state with parents
             */
            gitPositionMerge(commitParent.getAll(idcommitParent));
            /*
             * Foreach file must be compared in this state
             */
            for (Couple c : idCommitParentsToFiles.getAll(idcommitParent)) {
                RawText stateAfterMerge = EmptyFile;
                RawText stateAfterCommit = EmptyFile;
                /*
                 * get file in this repository
                 */
                File f = new File(gitDir + "/" + c.getFileName());
                if (f.exists()) { // if file is existing after merge
                    launchAndWait("sed /^[<=>][<=>][<=>][<=>]/d -i " + f.getPath());
                    stateAfterMerge = new RawText(f);
                } else {
                    logger.log(Level.WARNING, "File{0} not found !", f.getPath());
                }
                /*
                 * get file from git with id. 
                 */
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

                /*
                 * make a diff with this file
                 */
                EditList editList = diffAlgorithm.diff(RawTextComparator.DEFAULT, stateAfterMerge, stateAfterCommit);

                BlockLine editCount = files.get(c.fileName);
                editCount.incrementPass();

                for (Edit ed : editList) {// Count line replace is two time counted
                    editCount.addLine(ed.getEndA() - ed.getBeginA() + ed.getEndB() - ed.getBeginB());
                }
                //Count the block size
                editCount.addBlock(editList.size());


            }
            logger.info("Next");
           // commit++;
            if (progressBar) {
                progress.progress(1);
            }
        }
    }

    /**
     * accessor
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

        private int block = 0; //sum of different block 
        private int line = 0;  //sum of different line
        private int commitCount = 0; //sum of commit
        private LinkedList<String> merges = new LinkedList(); //merges id phase 2
        private String fileName; //path of file
        private int pass;        //number of pass made in phase 3, 
        //must be equals to number of merge

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

    /**
     * link filename with id.
     */
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

    /**
     * Multi-Thread jobs
     */
    class TryMultiThreaded {

        int fileTotal = 0;
        int fileTaked = 0;
        ProgressBar progress;
        LinkedList<Entry<String, BlockLine>> jobs = new LinkedList();
        Thread[] threads;

        /**
         * Constructor
         *
         * @param fileTotal total file will be computed
         * @param nbThread total of thread generated
         */
        public TryMultiThreaded(int fileTotal, int nbThread) {
            this.fileTotal = fileTotal;
            if(progressBar){
                progress=new ProgressBar(fileTotal);
            }
            threads = new Thread[nbThread];

        }

        /**
         * Generate all threads and start it
         */
        void start() {
            for (int i = 0; i < threads.length; i++) {
                threads[i] = new Thread(new WorkerExtractGit());
                threads[i].start();
            }
        }

        /**
         * Wait all threads are finished
         *
         * @throws InterruptedException
         */
        void waitAll() throws InterruptedException {
            for (int i = 0; i < threads.length; i++) {
                threads[i].join();
            }
        }

        /**
         * Take next job and return null if all is taked Wait if job is
         * remaining and not added yet
         *
         * @return job or null if no more jobs remaining
         */
        synchronized Entry<String, BlockLine> getJobFileTaked() {

            while (jobs.size() <= 0 && fileTaked < fileTotal) {
                try {
                    wait();
                } catch (InterruptedException ex) {
                    Logger.getLogger(GitWalker.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (jobs.size() > 0) {
                fileTaked++;
                return jobs.removeFirst();
            } else {
                return null;
            }
        }

        /**
         * this counts number of computed jobs and displays the progress bar if
         * needed
         */
        synchronized void incFileThreated() {
            if (progressBar) {
               progress.progress(1);
            }
        }

        /**
         * pull a new jobs in waiting list and wakeup sleeped threads
         *
         * @param entry job
         */
        synchronized void addJob(Entry<String, BlockLine> entry) {
            jobs.addLast(entry);
            notifyAll();
        }

        /**
         * thread code
         */
        class WorkerExtractGit implements Runnable {

            @Override
            public void run() {
                /*
                 * while jobs are remaining 
                 */
                while (fileTaked < fileTotal) {
                    try {
                        Entry<String, BlockLine> job = getJobFileTaked();
                        if (job != null) {
                            treatFileEntry(job);
                            incFileThreated();
                        }
                    } catch (Exception ex) {
                        Logger.getLogger(GitWalker.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }
}
