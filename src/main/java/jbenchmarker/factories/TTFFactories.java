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
import jbenchmarker.ot.ttf.MC.TTFUndoDocument;
import jbenchmarker.ot.ttf.MC.TTFUndoMergeAlgorithm;
import jbenchmarker.ot.ttf.MC.TTFUndoTransformations;
import jbenchmarker.ot.ttf.TTFDocument;
import jbenchmarker.ot.ttf.TTFMergeAlgorithm;
import jbenchmarker.ot.ttf.TTFOperation;
import jbenchmarker.ot.ttf.TTFTransformations;

/**
 *
 * @author oster
 */
 public class TTFFactories {

    static int nbrReplic = 4;
    static TTFTransformations ttf = new TTFTransformations();
    
    static public class WithoutGCFactory extends ReplicaFactory {

        @Override
        public MergeAlgorithm create(int siteId) {
            return new TTFMergeAlgorithm(siteId);
        }
    }

   static public class WithGCBasic extends ReplicaFactory {

        @Override
        public MergeAlgorithm create(int siteId) {
            return new TTFMergeAlgorithm(new TTFDocument(), 0,
                    new SOCT2(new SOCT2Log(ttf), new SOCT2GarbageCollector(nbrReplic)));
        }
    }

    static public class WithGC3 extends ReplicaFactory {

        @Override
        public MergeAlgorithm create(int siteId) {
            return new TTFMergeAlgorithm(new TTFDocument(), 0,
                    new SOCT2<TTFOperation>(new TTFTransformations(), siteId,
                    new SOCT2GarbageCollector(nbrReplic, 3)));
        }
    }

    static public class WithGC10 extends ReplicaFactory {

        @Override
        public MergeAlgorithm create(int siteId) {
            return new TTFMergeAlgorithm(new TTFDocument(), 0,
                    new SOCT2<TTFOperation>(new TTFTransformations(), siteId,
                    new SOCT2GarbageCollector(nbrReplic)));
        }
    }


    static public class WithoutGCLL extends ReplicaFactory {

        @Override
        public MergeAlgorithm create(int siteId) {
            return new TTFMergeAlgorithm(new TTFDocument(), 0,
                    new SOCT2(new SOCT2LogOptimizedLast(ttf), null));
        }
    }

    static public class WithBasic_PGC extends ReplicaFactory {

        @Override
        public MergeAlgorithm create(int siteId) {
            return new TTFMergeAlgorithm(new TTFDocument(), 0,
                    new SOCT2(new SOCT2Log(ttf),
                    new PreemptiveGarbageCollector(20)));
        }
    }

    static public class WithLL_PGC extends ReplicaFactory {

        @Override
        public MergeAlgorithm create(int siteId) {
            return new TTFMergeAlgorithm(new TTFDocument(), 0,
                    new SOCT2(new SOCT2LogOptimizedLast(ttf),
                    new PreemptiveGarbageCollector(20)));
        }
    }

    static public class WithBasic_PGC_2 extends ReplicaFactory {

        @Override
        public MergeAlgorithm create(int siteId) {
            return new TTFMergeAlgorithm(new TTFDocument(), 0,
                    new SOCT2(new SOCT2Log(ttf),
                    new PreemptiveGarbageCollector(50)));
        }
    }

    static public class WithLL_PGC_2 extends ReplicaFactory {

        @Override
        public MergeAlgorithm create(int siteId) {
            return new TTFMergeAlgorithm(new TTFDocument(), 0,
                    new SOCT2(new SOCT2LogOptimizedLast(ttf),
                    new PreemptiveGarbageCollector(50)));
        }
    }

    static public class WithBasic_PGC_3 extends ReplicaFactory {

        @Override
        public MergeAlgorithm create(int siteId) {
            return new TTFMergeAlgorithm(new TTFDocument(), 0,
                    new SOCT2(new SOCT2Log(ttf),
                    new PreemptiveGarbageCollector(100)));
        }
    }

    static public class WithLL_PGC_3 extends ReplicaFactory {

        @Override
        public MergeAlgorithm create(int siteId) {
            return new TTFMergeAlgorithm(new TTFDocument(), 0,
                    new SOCT2(new SOCT2LogOptimizedLast(ttf),
                    new PreemptiveGarbageCollector(100)));
        }
    }
    
    
    
    static public class Undo extends ReplicaFactory {
        @Override
        public MergeAlgorithm create(int siteId) {
            return new TTFUndoMergeAlgorithm(new TTFUndoDocument(), 0,
                    new SOCT2(new SOCT2Log(new TTFUndoTransformations()), null));
        }
    }
    
    static public class UndoPGG extends ReplicaFactory {
        @Override
        public MergeAlgorithm create(int siteId) {
            return new TTFUndoMergeAlgorithm(new TTFUndoDocument(), 0,
                    new SOCT2(new SOCT2Log(new TTFUndoTransformations()), new PreemptiveGarbageCollector(100)));
        }
    }
}
