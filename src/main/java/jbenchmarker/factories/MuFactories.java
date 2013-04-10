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
package jbenchmarker.factories;

import jbenchmarker.core.ReplicaFactory;
import jbenchmarker.logoot.TreedocStrategy;
import jbenchmarker.mu.MuDocument;
import jbenchmarker.mu.MuMerge;

/**
 * Factories for replica that manages moves and updates
 * @author urso
 */
public class MuFactories {
    public static class UpdateFactory<T> extends ReplicaFactory {
        @Override
        public MuMerge<T> create(int r) {
            return new MuMerge<T>(new MuDocument<T>(r, new TreedocStrategy()), r, false);
        }
    }
    
    public static class MoveFactory<T> extends ReplicaFactory {
        @Override
        public MuMerge<T> create(int r) {
            return new MuMerge<T>(new MuDocument<T>(r, new TreedocStrategy()), r, true);
        }
    }    
}
