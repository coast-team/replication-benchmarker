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

import jbenchmarker.core.MergeAlgorithm;
import jbenchmarker.core.ReplicaFactory;
import jbenchmarker.ot.soct2.*;
import jbenchmarker.ot.ttf.TTFDocument;
import jbenchmarker.ot.ttf.TTFMergeAlgorithm;
import jbenchmarker.ot.ttf.TTFTransformations;
import jbenchmarker.ot.ttf.update.TTFUDelWinsTransformations;
import jbenchmarker.ot.ttf.update.TTFUDocument;
import jbenchmarker.ot.ttf.update.TTFUMergeAlgorithm;
import jbenchmarker.ot.ttf.update.TTFUTransformations;

/**
 *
 * @author oster
 */
 public class TTFUFactories {

    static public class NoUpdate extends ReplicaFactory {

        @Override
        public TTFMergeAlgorithm create(int siteId) {
            return new TTFMergeAlgorithm(new TTFDocument(), siteId,
                    new SOCT2(siteId, new SOCT2Log(new TTFTransformations()), new PreemptiveGarbageCollector(50)));
        }
    }
    
    static public class Update extends ReplicaFactory {

        @Override
        public TTFUMergeAlgorithm create(int siteId) {
            return new TTFUMergeAlgorithm(new TTFUDocument(), siteId,
                    new SOCT2(siteId, new SOCT2Log(new TTFUTransformations()), new PreemptiveGarbageCollector(50)));
        }
    }

    static public class DelWins extends ReplicaFactory {

        @Override
        public TTFUMergeAlgorithm create(int siteId) {
            return new TTFUMergeAlgorithm(new TTFUDocument(), siteId,
                    new SOCT2(siteId, new SOCT2Log(new TTFUDelWinsTransformations()), new PreemptiveGarbageCollector(50)));
        }
    }
}
