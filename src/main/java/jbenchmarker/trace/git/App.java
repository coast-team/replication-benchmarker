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

import crdt.PreconditionException;
import crdt.simulator.CausalSimulator;
import crdt.simulator.IncorrectTraceException;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import jbenchmarker.factories.LogootFactory;
import jbenchmarker.factories.RGAFactory;
import jbenchmarker.factories.TreedocFactory;
import jbenchmarker.factories.WootFactories;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;


/**
 * Hello world!
 *
 */
public class App {

    public static void main(String[] args) throws IOException, GitAPIException, IncorrectTraceException, PreconditionException {
        //        GitExtraction.parseRepository(("/Users/urso/Rech/github/linux", "http://localhost:5984", "kernel/sched.c", true);
        if (args.length < 1 ) {
            System.err.println("Arguments : ");
            System.err.println("- git directory ");
            System.err.println("- file [optional] path or number (default : all files)");
            System.err.println("- --save [optional] save trace");
            System.err.println("- --clean [optional] clean DB");
            System.exit(1);
        }
        String gitdir = args[0];
        
        List<String> paths = new LinkedList<String>();
        if (args.length > 1 && !args[1].startsWith("--") && !args[1].matches("[0-9]*")) {
            paths.add(args[1]);
        } else {
            extractFiles(new File(gitdir), gitdir, paths);
        }
        int end = paths.size();
        if (args.length > 1 && args[1].matches("[0-9]*")) {
            end = Integer.parseInt(args[1]);
        }
        
        boolean save = Arrays.asList(args).contains("--save");
        boolean clean = Arrays.asList(args).contains("--clean");
        
        System.out.println("*** Total number of files : " + paths.size());
        int i = 0;
        for (String path : paths.subList(0, end)) {
            System.out.println("----- " + path + " (" + ++i + '/' + end + ')');
            GitTrace trace = GitTrace.create(gitdir, "http://localhost:5984", path, clean);
            CausalSimulator cd = new CausalSimulator(new LogootFactory());        
            cd.run(trace, false, save, 0, false);
            System.out.println("Nb replica : " + cd.replicas.keySet().size());
            System.out.println("NB EDITS : " + GitTrace.editNb);
            System.out.println("EDITS SIZE : " + GitTrace.editSize);
        }      
        System.out.println("*** TOTAL NB EDITS : " + GitTrace.editNb);
        System.out.println("*** TOTAL EDITS SIZE : " + GitTrace.editSize);
//
//
//        GitTrace couchTrace = GitTrace.create("/Users/urso/Rech/github/linux", "http://localhost:5984", "MAINTAINERS", false);
//        GitTrace couchTrace = GitTrace.create("/Users/urso/Rech/github/linux", "http://localhost:5984", "kernel/sched.c", false);
        
        //System.out.println(cd.replicas.get(1).lookup());
//        System.out.println(cd.replicas.keySet().size());
    }

    private static void extractFiles(File dir, String gitdir, List<String> paths) {
        for (File f : dir.listFiles()) {
            if (f.isFile() && !f.getName().startsWith(".git")) { 
                paths.add(f.getAbsolutePath().substring(gitdir.length()+1));
            } else if (f.isDirectory() && !".git".equals(f.getName())) {
                extractFiles(f, gitdir, paths);
            }
        }
    }
}
// git : 967 authors, 30000 commits: Total time: 5:46:52.821s 