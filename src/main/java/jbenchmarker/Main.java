/**
 * Replication Benchmarker
 * https://github.com/score-team/replication-benchmarker/
 * Copyright (C) 2013 LORIA / Inria / SCORE Team
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
package jbenchmarker;
import java.util.Arrays;
import jbenchmarker.factories.ExperienceFactory;

public class Main {

    public static void main(String[] args) throws Exception {

            if(args.length<1){
            System.err.println("Arguments for Git experiment :::::::::: ");
             System.err.println("- Factory to run git main");
            System.err.println("- git directory ");
            System.err.println("- file [optional] path or number (default : all files)");
            System.err.println("- --save [optional] Store traces");
            System.err.println("- --clean [optional] clean DB");
            System.err.println("- --stat [optional] compute execution time and memory");
            System.err.println("- i :  number of file [To begin]");
            System.err.println("- Number of execution");
            System.err.println("- Factory");

            System.err.println("Arguments for real time trace experiment :::::::::: ");
            System.err.println("- Factory to run trace main");
            System.err.println("- Factory : a jbenchmaker.core.ReplicaFactory implementation ");
            System.err.println("- Trace : a file of a trace ");
            System.err.println("- nb_exec : the number of execution (default 1)");
            System.err.println("- thresold : the proportional thresold for not counting a result in times the average (default 2.0)");
            System.err.println("- number of serialization");
            System.err.println("- Save traces ? (0 don't save, else save)");
        }
               
        ExperienceFactory ef = (ExperienceFactory) Class.forName(args[0]).newInstance();
        ef.create(args);
    }

}
