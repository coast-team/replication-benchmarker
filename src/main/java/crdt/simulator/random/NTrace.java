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
package crdt.simulator.random;

import crdt.simulator.Trace;
import crdt.simulator.TraceOperation;
import java.util.Enumeration;
import java.util.List;

/**
 * Special Trace with is concatenation with many traces given in constructor.
 *
 * @author Stephane Martin <stephane@stephanemartin.fr>
 */
public class NTrace implements Trace {

    RandomParameters[] listRandomTrace;

    /**
     * @param traces many traces
     */
    public NTrace(RandomParameters... traces) {
        this.listRandomTrace = traces;
    }

    public NTrace(List<RandomParameters> traces) {
        this.listRandomTrace = traces.toArray(new RandomParameters[traces.size()]);
    }

    @Override
    public Enumeration<TraceOperation> enumeration() {

        return new Enumeration<TraceOperation>() {
            RandomTrace currentTrace=listRandomTrace.length > 0 ? new RandomTrace(null,listRandomTrace[0]):null;
            Enumeration<TraceOperation> currentTraceEnum = currentTrace!=null ? currentTrace.enumeration() : null;
            int currentPlace = 0;

            /**
             * Place check if no more operation existing If it is existing it
             * set currentTraceEnum to non empty trace.
             */
            private void setCurrent() {
                while (currentTraceEnum != null && !currentTraceEnum.hasMoreElements() && currentPlace < listRandomTrace.length - 1) {
                    currentTrace = new RandomTrace(currentTrace, listRandomTrace[++currentPlace]);
                    currentTraceEnum=currentTrace.enumeration();
                }
            }

            @Override
            public boolean hasMoreElements() {
                setCurrent();
                return currentTraceEnum != null ? currentTraceEnum.hasMoreElements() : false;
            }

            @Override
            public TraceOperation nextElement() {
                setCurrent();
                return currentTraceEnum.nextElement();
            }
        };
    }

    public static class RandomParameters {

        final long duration, delay;
        final double probability, sdv;
        final int replicas;
        final RandomTrace.ReplicaProfile rp;
        final OperationProfile op;

        public RandomParameters(long duration, RandomTrace.ReplicaProfile rp, OperationProfile op, double probability, long delay, double sdv, int replicas) {
            this.duration = duration;
            this.delay = delay;
            this.probability = probability;
            this.sdv = sdv;
            this.replicas = replicas;
            this.rp = rp;
            this.op = op;
        }

        public long getDuration() {
            return duration;
        }

        public long getDelay() {
            return delay;
        }

        public double getProbability() {
            return probability;
        }

        public double getSdv() {
            return sdv;
        }

        public int getReplicas() {
            return replicas;
        }

        public RandomTrace.ReplicaProfile getRp() {
            return rp;
        }

        public OperationProfile getOp() {
            return op;
        }
    }
}
