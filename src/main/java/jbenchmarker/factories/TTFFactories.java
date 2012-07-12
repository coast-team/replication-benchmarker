/**
 * Replication Benchmarker
 * https://github.com/score-team/replication-benchmarker/
 * Copyright (C) 2011 INRIA / LORIA / SCORE Team
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
import jbenchmarker.ot.ttf.TTFOperation;
import jbenchmarker.ot.ttf.TTFTransformations;

/**
 *
 * @author oster
 */
public class TTFFactories {

    static int nbrReplic = 4;

    static public class WithoutGC extends ReplicaFactory {

        @Override
        public MergeAlgorithm create(int siteId) {
            return new TTFMergeAlgorithm(new TTFDocument(), siteId);
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
    /***********************/
    static TTFTransformations ttf = new TTFTransformations();

    public static class WithoutGCLL extends ReplicaFactory {

        @Override
        public MergeAlgorithm create(int siteId) {
            return new TTFMergeAlgorithm(new TTFDocument(), 0,
                    new SOCT2(new SOCT2LogOptimizedLast(ttf), null));
        }
    }
    
    public static class WithBasic_PGC extends ReplicaFactory {

        @Override
        public MergeAlgorithm create(int siteId) {
            return new TTFMergeAlgorithm(new TTFDocument(), 0,
                    new SOCT2(new SOCT2Log(ttf),
                            new PreemptiveGarbageCollector(20)));
        }
    }
    
    public static class WithLL_PGC extends ReplicaFactory {

        @Override
        public MergeAlgorithm create(int siteId) {
            return new TTFMergeAlgorithm(new TTFDocument(), 0,
                    new SOCT2(new SOCT2LogOptimizedLast(ttf),
                    new PreemptiveGarbageCollector(20)));
        }
    }
    
    
    
    public static class WithBasic_PGC_2 extends ReplicaFactory {

        @Override
        public MergeAlgorithm create(int siteId) {
            return new TTFMergeAlgorithm(new TTFDocument(), 0,
                    new SOCT2(new SOCT2Log(ttf),
                            new PreemptiveGarbageCollector(50)));
        }
    }
    
    public static class WithLL_PGC_2 extends ReplicaFactory {

        @Override
        public MergeAlgorithm create(int siteId) {
            return new TTFMergeAlgorithm(new TTFDocument(), 0,
                    new SOCT2(new SOCT2LogOptimizedLast(ttf),
                    new PreemptiveGarbageCollector(50)));
        }
    }
    
     public static class WithBasic_PGC_3 extends ReplicaFactory {

        @Override
        public MergeAlgorithm create(int siteId) {
            return new TTFMergeAlgorithm(new TTFDocument(), 0,
                    new SOCT2(new SOCT2Log(ttf),
                            new PreemptiveGarbageCollector(100)));
        }
    }
    
    public static class WithLL_PGC_3 extends ReplicaFactory {

        @Override
        public MergeAlgorithm create(int siteId) {
            return new TTFMergeAlgorithm(new TTFDocument(), 0,
                    new SOCT2(new SOCT2LogOptimizedLast(ttf),
                    new PreemptiveGarbageCollector(100)));
        }
    }
//    
//    
//    
//
//    public static class WithoutGCLP extends ReplicaFactory {
//
//        @Override
//        public MergeAlgorithm create(int siteId) {
//            return new TTFMergeAlgorithm(new TTFDocument(), 0,
//                    new SOCT2(new SOCT2LogOptimizedPlace(ttf), null));
//        }
//    }
//
//    public static class WithoutGCLLP extends ReplicaFactory {
//
//        @Override
//        public MergeAlgorithm create(int siteId) {
//            return new TTFMergeAlgorithm(new TTFDocument(), 0,
//                    new SOCT2(new SOCT2LogOptimizedPlaceAndLast(ttf), null));
//        }
//    }
//
    public static class WithGCBasic extends ReplicaFactory {

        @Override
        public MergeAlgorithm create(int siteId) {
            return new TTFMergeAlgorithm(new TTFDocument(), 0,
                    new SOCT2(new SOCT2Log(ttf), new SOCT2GarbageCollector(nbrReplic)));
        }
    }
//
//    public static class WithLL_GC extends ReplicaFactory {
//
//        @Override
//        public MergeAlgorithm create(int siteId) {
//            return new TTFMergeAlgorithm(new TTFDocument(), 0,
//                    new SOCT2(new SOCT2LogOptimizedLast(ttf), new SOCT2GarbageCollector(nbrReplic)));
//        }
//    }
//
//    public static class WithLP_GC extends ReplicaFactory {
//
//        @Override
//        public MergeAlgorithm create(int siteId) {
//            return new TTFMergeAlgorithm(new TTFDocument(), 0,
//                    new SOCT2(new SOCT2LogOptimizedPlace(ttf), new SOCT2GarbageCollector(nbrReplic)));
//        }
//    }
//
//    public static class WithLLP_GC extends ReplicaFactory {
//
//        @Override
//        public MergeAlgorithm create(int siteId) {
//            return new TTFMergeAlgorithm(new TTFDocument(), 0,
//                    new SOCT2(new SOCT2LogOptimizedPlaceAndLast(ttf), new SOCT2GarbageCollector(nbrReplic)));
//        }
//    }
//
//    
//
//    
//
//    public static class WithLP_PGC extends ReplicaFactory {
//
//        @Override
//        public MergeAlgorithm create(int siteId) {
//            return new TTFMergeAlgorithm(new TTFDocument(), 0,
//                    new SOCT2(new SOCT2LogOptimizedPlace(ttf),
//                    new PreemptiveGarbageCollector(nbrReplic)));
//        }
//    }
//
//    public static class WithLLP_PGC extends ReplicaFactory {
//
//        @Override
//        public MergeAlgorithm create(int siteId) {
//            return new TTFMergeAlgorithm(new TTFDocument(), 0,
//                    new SOCT2(new SOCT2LogOptimizedPlaceAndLast(ttf),
//                    new PreemptiveGarbageCollector(nbrReplic)));
//        }
//    }
//    //*************************************************************
//
//    static public class WithoutGC_2 extends ReplicaFactory {
//
//        @Override
//        public MergeAlgorithm create(int siteId) {
//            return new TTFMergeAlgorithm(new TTFDocument(), siteId);
//        }
//    }
//
//    public static class WithoutGCLL_2 extends ReplicaFactory {
//
//        @Override
//        public MergeAlgorithm create(int siteId) {
//            System.out.println(siteId);
//            return new TTFMergeAlgorithm(new TTFDocument(), 0,
//                    new SOCT2(new SOCT2LogOptimizedLast(ttf), null));
//        }
//    }
//
//    public static class WithGCBasic_2 extends ReplicaFactory {
//
//        @Override
//        public MergeAlgorithm create(int siteId) {
//            return new TTFMergeAlgorithm(new TTFDocument(), 0,
//                    new SOCT2(new SOCT2Log(ttf), new SOCT2GarbageCollector(50, nbrReplic)));
//        }
//    }
//
//    public static class WithLL_GC_2 extends ReplicaFactory {
//
//        @Override
//        public MergeAlgorithm create(int siteId) {
//            return new TTFMergeAlgorithm(new TTFDocument(), 0,
//                    new SOCT2(new SOCT2LogOptimizedLast(ttf), new SOCT2GarbageCollector(50, nbrReplic)));
//        }
//    }
//
//    public static class WithLP_GC_2 extends ReplicaFactory {
//
//        @Override
//        public MergeAlgorithm create(int siteId) {
//            return new TTFMergeAlgorithm(new TTFDocument(), 0,
//                    new SOCT2(new SOCT2LogOptimizedPlace(ttf), new SOCT2GarbageCollector(50, nbrReplic)));
//        }
//    }
//
//    public static class WithLLP_GC_2 extends ReplicaFactory {
//
//        @Override
//        public MergeAlgorithm create(int siteId) {
//            return new TTFMergeAlgorithm(new TTFDocument(), 0,
//                    new SOCT2(new SOCT2LogOptimizedPlaceAndLast(ttf), new SOCT2GarbageCollector(50, nbrReplic)));
//        }
//    }
//
//    public static class WithLL_PGC_2 extends ReplicaFactory {
//
//        @Override
//        public MergeAlgorithm create(int siteId) {
//            return new TTFMergeAlgorithm(new TTFDocument(), 0,
//                    new SOCT2(new SOCT2LogOptimizedLast(ttf),
//                    new PreemptiveGarbageCollector(50, nbrReplic)));
//        }
//    }
//
//    public static class WithBasic_PGC_2 extends ReplicaFactory {
//
//        @Override
//        public MergeAlgorithm create(int siteId) {
//            return new TTFMergeAlgorithm(new TTFDocument(), 0,
//                    new SOCT2(new SOCT2Log(ttf), new PreemptiveGarbageCollector(50, nbrReplic)));
//        }
//    }
//
//    public static class WithLP_PGC_2 extends ReplicaFactory {
//
//        @Override
//        public MergeAlgorithm create(int siteId) {
//            return new TTFMergeAlgorithm(new TTFDocument(), 0,
//                    new SOCT2(new SOCT2LogOptimizedPlace(ttf),
//                    new PreemptiveGarbageCollector(50, nbrReplic)));
//        }
//    }
//
//    public static class WithLLP_PGC_2 extends ReplicaFactory {
//
//        @Override
//        public MergeAlgorithm create(int siteId) {
//            return new TTFMergeAlgorithm(new TTFDocument(), 0,
//                    new SOCT2(new SOCT2LogOptimizedPlaceAndLast(ttf),
//                    new PreemptiveGarbageCollector(50, nbrReplic)));
//        }
//    }
//
//    public static class WithGCBasic_3 extends ReplicaFactory {
//
//        @Override
//        public MergeAlgorithm create(int siteId) {
//            return new TTFMergeAlgorithm(new TTFDocument(), 0,
//                    new SOCT2(new SOCT2Log(ttf), new SOCT2GarbageCollector(100, nbrReplic)));
//        }
//    }
//
//    public static class WithLL_GC_3 extends ReplicaFactory {
//
//        @Override
//        public MergeAlgorithm create(int siteId) {
//            return new TTFMergeAlgorithm(new TTFDocument(), 0,
//                    new SOCT2(new SOCT2LogOptimizedLast(ttf), new SOCT2GarbageCollector(100, nbrReplic)));
//        }
//    }
//
//    public static class WithLP_GC_3 extends ReplicaFactory {
//
//        @Override
//        public MergeAlgorithm create(int siteId) {
//            return new TTFMergeAlgorithm(new TTFDocument(), 0,
//                    new SOCT2(new SOCT2LogOptimizedPlace(ttf), new SOCT2GarbageCollector(100, nbrReplic)));
//        }
//    }
//
//    public static class WithLLP_GC_3 extends ReplicaFactory {
//
//        @Override
//        public MergeAlgorithm create(int siteId) {
//            return new TTFMergeAlgorithm(new TTFDocument(), 0,
//                    new SOCT2(new SOCT2LogOptimizedPlaceAndLast(ttf), new SOCT2GarbageCollector(100, nbrReplic)));
//        }
//    }
//
//    public static class WithLL_PGC_3 extends ReplicaFactory {
//
//        @Override
//        public MergeAlgorithm create(int siteId) {
//            return new TTFMergeAlgorithm(new TTFDocument(), 0,
//                    new SOCT2(new SOCT2LogOptimizedLast(ttf),
//                    new PreemptiveGarbageCollector(100, nbrReplic)));
//        }
//    }
//
//    public static class WithBasic_PGC_3 extends ReplicaFactory {
//
//        @Override
//        public MergeAlgorithm create(int siteId) {
//            return new TTFMergeAlgorithm(new TTFDocument(), 0,
//                    new SOCT2(new SOCT2Log(ttf), new PreemptiveGarbageCollector(100, nbrReplic)));
//        }
//    }
//
//    public static class WithLP_PGC_3 extends ReplicaFactory {
//
//        @Override
//        public MergeAlgorithm create(int siteId) {
//            return new TTFMergeAlgorithm(new TTFDocument(), 0,
//                    new SOCT2(new SOCT2LogOptimizedPlace(ttf),
//                    new PreemptiveGarbageCollector(100, nbrReplic)));
//        }
//    }
//
//    public static class WithLLP_PGC_3 extends ReplicaFactory {
//
//        @Override
//        public MergeAlgorithm create(int siteId) {
//            return new TTFMergeAlgorithm(new TTFDocument(), 0,
//                    new SOCT2(new SOCT2LogOptimizedPlaceAndLast(ttf),
//                    new PreemptiveGarbageCollector(100, nbrReplic)));
//        }
//    }
}
